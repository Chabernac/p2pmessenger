/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.AsyncMessageProcotol;
import chabernac.protocol.message.Message;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.thread.DynamicSizeExecutor;

public class AsyncFileTransferProtocol extends Protocol {
  private static final Logger LOGGER = Logger.getLogger( AsyncFileTransferProtocol.class );

  public static final String ID = "AFP";

  private static enum Command{ACCEPT_FILE, RESEND_PACKET, ACCEPT_PACKET, END_FILE_TRANSFER};
  private static enum Response{FILE_ACCEPTED, FILE_REFUSED, PACKET_OK, PACKET_REFUSED, NOK, UNKNOWN_ID, END_FILE_TRANSFER_OK, ABORT_FILE_TRANSFER};

  private int myPacketSize = 1024;
  private int myMaxRetries = 8;

  private iObjectStringConverter<FilePacket> myObjectPerister = new Base64ObjectStringConverter<FilePacket>();

  private Map<String, FilePacketIO> myFilePacketIO = new HashMap<String, FilePacketIO>();

  private iAsyncFileTransferHandler myHandler = null;

  private ExecutorService myService = DynamicSizeExecutor.getTinyInstance();
  
  //just for test purposes, if set to a number > 0 everty 1 / myIsIgnorePacketRatio packets will be simulated as being lost so that a resend
  //of the packed will be triggered
  private int myIsIgnorePacketRatio = -1;
  private Random myRandom = new Random();

  public AsyncFileTransferProtocol( ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Async file transfer protocol";
  }

  private AsyncMessageProcotol getMessageProtocol() throws ProtocolException{
    return (AsyncMessageProcotol)findProtocolContainer().getProtocol( AsyncMessageProcotol.ID);
  }

  public RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID)).getRoutingTable();
  }


  @Override
  public String handleCommand( String aSessionId, String anInput ) {
    //we can not do anything if there is no file handler
    if(myHandler == null) return Response.ABORT_FILE_TRANSFER.name();

    try{
      if(anInput.startsWith( Command.ACCEPT_FILE.name() )){
        String[] theParams = anInput.substring( Command.ACCEPT_FILE.name().length() + 1 ).split( " " );

        String theFileName = theParams[0];
        String theUUId = theParams[1];
        int thePacketSize = Integer.parseInt( theParams[2] );
        int theNrOfPackets = Integer.parseInt( theParams[3] );

        File theFile = myHandler.acceptFile( theFileName, theUUId );

        FilePacketIO theIO = FilePacketIO.createForWrite( theFile, theUUId, thePacketSize, theNrOfPackets );
        myFilePacketIO.put( theUUId, theIO );

        //create a 
        return Response.FILE_ACCEPTED.name();
      } else if(anInput.startsWith( Command.ACCEPT_PACKET.name() )){
        String thePack = anInput.substring(Command.ACCEPT_PACKET.name().length() + 1 );
        FilePacket thePacket = myObjectPerister.getObject( thePack );
        
        if(isSimulateLostPacket()) {
          LOGGER.debug("Simulating lost packet '" + thePacket.getPacket() + "'");
          return Response.NOK.name();
        }

        if(!myFilePacketIO.containsKey( thePacket.getId() )){
          return Response.UNKNOWN_ID.name();
        }

        FilePacketIO theIO = myFilePacketIO.get(thePacket.getId());
        theIO.writePacket( thePacket );


        myHandler.fileTransfer( theIO.getFile().getName(), thePacket.getId(), theIO.getPercentageWritten());

        LOGGER.debug( "Packet accepted '" + thePacket.getPacket() + "'" );
        return Response.PACKET_OK.name();
      } else if(anInput.startsWith( Command.END_FILE_TRANSFER.name() )){
        String[] theParams = anInput.substring( Command.END_FILE_TRANSFER.name().length() + 1 ).split( " " );

        String theUUId = theParams[0];
        FilePacketIO theIO = myFilePacketIO.get(theUUId);
        if(theIO.isComplete()){
          myHandler.fileSaved( theIO.getFile() );
          return Response.END_FILE_TRANSFER_OK.name();
        } else {
          String theIncompletePacktets = "";
          for(int i=0;i<theIO.getWrittenPackets().length;i++){
            if(!theIO.getWrittenPackets()[i]){
              theIncompletePacktets += i + " ";
            }
          }
          return theIncompletePacktets;
        }
      }
    }catch(Exception e){
      LOGGER.error( "Error occured in ayncfiletransferprotocol", e );
      return Response.NOK.name();
    }

    // TODO Auto-generated method stub
    return null;
  }

  private boolean isSimulateLostPacket() {
    if(myIsIgnorePacketRatio <= 0) return false;
    return (myRandom.nextInt() % myIsIgnorePacketRatio == 0); 
  }

  private void sendPacket(final AbstractPeer aPeer, final FilePacket aPacket,final CountDownLatch aLatch, final AtomicBoolean isContinue){
    myService.execute( new Runnable(){
      public void run(){
        try{
          Message theMessage = new Message();
          theMessage.setDestination( aPeer );
          theMessage.setProtocolMessage( true );
          theMessage.setMessage( createMessage( Command.ACCEPT_PACKET + " " + myObjectPerister.toString( aPacket ) ) );
          LOGGER.debug("Packet send '" + aPacket.getPacket() + "'");
          String theResponse = getMessageProtocol().sendAndWaitForResponse( theMessage, 5, TimeUnit.SECONDS );
          if(theResponse.startsWith( Response.PACKET_REFUSED.name() )){
            isContinue.set( false );
          }
        }catch(Exception e){
          LOGGER.error("Error occured while sending packet " + aPacket.getPacket(), e);
        }finally {
          aLatch.countDown();
        }
      }
    });
  }

  private String sendMessageTo(AbstractPeer aPeer, String aMessage) throws AsyncFileTransferException{
    try{
      Message theMessage = new Message();
      theMessage.setDestination( aPeer );
      theMessage.setMessage( createMessage( aMessage ));
      theMessage.setProtocolMessage( true );
      return getMessageProtocol().sendAndWaitForResponse( theMessage, 5, TimeUnit.SECONDS );
    }catch(Exception e){
      throw new AsyncFileTransferException("Could not send message", e);
    }
  }

  public void sendFile(File aFile, String aPeer) throws AsyncFileTransferException{
    try{
      AbstractPeer theDestination = getRoutingTable().getEntryForPeer( aPeer ).getPeer();

      //create a new FilePacketIO for this file transfer
      FilePacketIO theIO = FilePacketIO.createForRead( aFile, myPacketSize );
      //store it
      myFilePacketIO.put( theIO.getId(), theIO );

      AtomicBoolean isContinue = new AtomicBoolean(true);

      //init file transfer with other peer
      String theResult = sendMessageTo( theDestination, Command.ACCEPT_FILE.name() + " " + 
                                        aFile.getName()  + " " + 
                                        theIO.getId() + " " + 
                                        theIO.getPacketSize() + " " + 
                                        theIO.getNrOfPackets());

      //only continue if the file was accepted by the client
      if(!theResult.startsWith( Response.FILE_ACCEPTED.name() )) throw new AsyncFileTransferException("Transferring file aborted");

      CountDownLatch theLatch = new CountDownLatch( theIO.getNrOfPackets() );
      //now loop over all packets and send them to the other peer
      for(int i=0;i<theIO.getNrOfPackets() && isContinue.get();i++){
        sendPacket( theDestination, theIO.getPacket( i ), theLatch, isContinue );
        if(myHandler != null) myHandler.fileTransfer( theIO.getFile().getName(), theIO.getId(), (double)i / (double)theIO.getNrOfPackets());
      }
      
      if(!isContinue.get()) throw new AsyncFileTransferException("Transferring file aborted because a packet was refused");

      theLatch.await( 5, TimeUnit.SECONDS );

      String theResponse = null;
      int j=0;
      while( j++ < myMaxRetries && !(theResponse = sendMessageTo( theDestination, Command.END_FILE_TRANSFER.name()  + " " + theIO.getId())).equalsIgnoreCase(Response.END_FILE_TRANSFER_OK.name())){
        //if we get here not all packets where correctly delivered resend the missed packets
        LOGGER.debug("Some packets need to be resended '" + theResponse + "'");
        String[] thePacketsToResend = theResponse.split(" ");
        theLatch = new CountDownLatch( thePacketsToResend.length );
        for(int i=0;i<thePacketsToResend.length && isContinue.get();i++){
          LOGGER.debug("Resending packet '" + thePacketsToResend[i] + "'");
          sendPacket(theDestination, theIO.getPacket(Integer.parseInt(thePacketsToResend[i])), theLatch, isContinue);
        }
        
        if(!isContinue.get()) throw new AsyncFileTransferException("Transferring file aborted because a packet was refused");
        
        theLatch.await(5, TimeUnit.SECONDS);
      }

      if(!theResponse.equalsIgnoreCase(Response.END_FILE_TRANSFER_OK.name())){
        throw new AsyncFileTransferException("Transferring file failed, not all packets where send successfull, missing packet number '" + theResponse + "'");
      }
    }catch(Exception e){
      if(e instanceof AsyncFileTransferException) throw (AsyncFileTransferException)e;
      throw new AsyncFileTransferException("Could not send file to peer '" + aPeer + "'", e);
    }
  }

  public iAsyncFileTransferHandler getHandler() {
    return myHandler;
  }

  public void setFileHandler( iAsyncFileTransferHandler aHandler ) {
    myHandler = aHandler;
  }
  
  int getIsIgnorePacketRatio() {
    return myIsIgnorePacketRatio;
  }

  void setIsIgnorePacketRatio( int aIsIgnorePacketRatio ) {
    myIsIgnorePacketRatio = aIsIgnorePacketRatio;
  }

  protected int getPacketSize() {
    return myPacketSize;
  }

  protected void setPacketSize( int aPacketSize ) {
    myPacketSize = aPacketSize;
  }

  protected int getMaxRetries() {
    return myMaxRetries;
  }

  protected void setMaxRetries( int aMaxRetries ) {
    myMaxRetries = aMaxRetries;
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub
  }
}
