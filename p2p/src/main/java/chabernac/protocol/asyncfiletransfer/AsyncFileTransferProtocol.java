/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

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
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.thread.DynamicSizeExecutor;

public class AsyncFileTransferProtocol extends Protocol implements iTransferController {
  private static final Logger LOGGER = Logger.getLogger( AsyncFileTransferProtocol.class );

  public static final String ID = "AFP";

  static enum Command{ACCEPT_FILE, RESEND_PACKET, ACCEPT_PACKET, END_FILE_TRANSFER};
  static enum Response{FILE_ACCEPTED, FILE_REFUSED, PACKET_OK, PACKET_REFUSED, NOK, UNKNOWN_ID, END_FILE_TRANSFER_OK, ABORT_FILE_TRANSFER};

  int myPacketSize = 1024;
  int myMaxRetries = 8;
  //when this number of failers happen consecutive the file transfer will be aborted to avoid floading the network with unroutable messages
  int myMaxConsecutiveFailures = 10;

  iObjectStringConverter<FilePacket> myObjectPerister = new Base64ObjectStringConverter<FilePacket>();

//  private Map<String, FilePacketIO> myFilePacketIO = new HashMap<String, FilePacketIO>();

  private final Object LOCK = new Object();
  private Map<String, FileSender> mySendingFiles = new HashMap<String, FileSender>();
  private Map<String, FileReceiver> myReceivingFiles = new HashMap<String, FileReceiver>();

  iAsyncFileTransferHandler myHandler = null;

  ExecutorService myPacketSenderService = DynamicSizeExecutor.getTinyInstance();
  ExecutorService myFileSenderService = DynamicSizeExecutor.getTinyInstance();


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

  AsyncMessageProcotol getMessageProtocol() throws ProtocolException{
    return (AsyncMessageProcotol)findProtocolContainer().getProtocol( AsyncMessageProcotol.ID);
  }

  RoutingTable getRoutingTable() throws ProtocolException{
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
        //we should receive the peer id from who we are receiving the file
        FileReceiver theReceiver = new FileReceiver( null, theIO, this);
        myReceivingFiles.put( theUUId, theReceiver );

        //create a 
        return Response.FILE_ACCEPTED.name();
      } else if(anInput.startsWith( Command.ACCEPT_PACKET.name() )){
        String thePack = anInput.substring(Command.ACCEPT_PACKET.name().length() + 1 );
        FilePacket thePacket = myObjectPerister.getObject( thePack );

        if(isSimulateLostPacket()) {
          LOGGER.debug("Simulating lost packet '" + thePacket.getPacket() + "'");
          return Response.NOK.name();
        }

        if(!myReceivingFiles.containsKey( thePacket.getId() )){
          return Response.UNKNOWN_ID.name();
        }

        FileReceiver theIO = myReceivingFiles.get(thePacket.getId());
        theIO.writePacket( thePacket );


        myHandler.fileTransfer( theIO.getFile().getName(), thePacket.getId(), theIO.getPercentageComplete());

        LOGGER.debug( "Packet accepted '" + thePacket.getPacket() + "'" );
        return Response.PACKET_OK.name();
      } else if(anInput.startsWith( Command.END_FILE_TRANSFER.name() )){
        String[] theParams = anInput.substring( Command.END_FILE_TRANSFER.name().length() + 1 ).split( " " );

        String theUUId = theParams[0];
        FilePacketIO theIO = myReceivingFiles.get(theUUId).getIO();
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

  String sendMessageTo(AbstractPeer aPeer, String aMessage) throws AsyncFileTransferException{
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

  void testReachable(String aPeer) throws AsyncFileTransferException{
    RoutingTableEntry theEntryForPeer;
    try {
      theEntryForPeer = getRoutingTable().getEntryForPeer( aPeer );
      if(!theEntryForPeer.isReachable()) throw new AsyncFileTransferException("The peer became you want to send a file to is unreachable");
    } catch (Exception e) {
      throw new AsyncFileTransferException("Not able to retrieve routing table entry for peer '" + aPeer + "'");
    }
  }

  public FileTransferHandler sendFile(final File aFile,final String aPeer){
    //create a new FilePacketIO for this file transfer
    FilePacketIO theIO = FilePacketIO.createForRead( aFile, myPacketSize );
    //store it
    final FileSender theFileSender = new FileSender(aPeer, theIO, this);
    mySendingFiles.put(theIO.getId(), theFileSender);


    myFileSenderService.submit(new Runnable(){
      @Override
      public void run() {
        try{
          theFileSender.start();
        }catch(AsyncFileTransferException e) {
          LOGGER.error("An error occured while transferring file", e);
        }
      }
    });

    return new FileTransferHandler(theIO.getId(), this);
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
    for(FileSender theSender : mySendingFiles.values()){
      theSender.stop();
    }
  }

  @Override
  public void removeAndInterrupt(String aTransferId) throws AsyncFileTransferException {
    synchronized(LOCK){
      iFileIO theIO = getFileIO( aTransferId );
      theIO.stop();
      removeFileIO( aTransferId );
    }
  }

  @Override
  public void pause(String aTransferId) throws AsyncFileTransferException {
    synchronized(LOCK){
      iFileIO theIO = getFileIO( aTransferId );
      theIO.stop();
    }

  }

  @Override
  public FileTransferHandler resume(final String aTransferId) throws AsyncFileTransferException {
    synchronized(LOCK){
      getFileIO( aTransferId );
    }

    myFileSenderService.submit(new Runnable(){
      @Override
      public void run(){
        try{
          getFileIO( aTransferId ).start();
        }catch(AsyncFileTransferException e) {
          LOGGER.error("An error occured while transferring file", e);
        }
      }
    });

    return new FileTransferHandler(aTransferId, this);

  }

  @Override
  public FileTransferState getState(String aTransferId){
    synchronized(LOCK){
      if(!containsTransferId( aTransferId )) {
        return new FileTransferState(0, FileTransferState.State.CANCELLED_OR_REMOVED);
      }

      try{
        iFileIO theSender = getFileIO( aTransferId );
        if(theSender.isComplete())  {
          return new FileTransferState(1D, FileTransferState.State.DONE);
        } else if(theSender.isTransferring()){
          return new FileTransferState(theSender.getPercentageComplete(), FileTransferState.State.RUNNING);
        }  else {
          return new FileTransferState(0, FileTransferState.State.NOT_STARTED);
        }
      }catch(Exception e){
        LOGGER.error("We should not come here", e);
        return null;
      }
    }
  }

  @Override
  public void removeFinished() {
    synchronized(LOCK){
      for(Iterator<String> theTransferIds = mySendingFiles.keySet().iterator();theTransferIds.hasNext();){
        String theTransferId = theTransferIds.next();
        FileSender theSender = mySendingFiles.get(theTransferId);
        if(theSender.isComplete()){
          mySendingFiles.remove(theTransferId);
        }
      }
      for(Iterator<String> theTransferIds = myReceivingFiles.keySet().iterator();theTransferIds.hasNext();){
        String theTransferId = theTransferIds.next();
        FileReceiver theReceiver = myReceivingFiles.get(theTransferId);
        if(theReceiver.isComplete()){
          myReceivingFiles.remove(theTransferId);
        }
      }

    }
  }

  @Override
  public Set<String> getSendingTransfers() {
    return mySendingFiles.keySet();
  }

  @Override
  public Set<String> getReceivingTransfers() {
    return myReceivingFiles.keySet();
  }

  @Override
  public FileTransferHandler getTransferHandler( String aTransferId ) throws AsyncFileTransferException {
    if(myReceivingFiles.containsKey( aTransferId )) return new FileTransferHandler( aTransferId, this);
    if(mySendingFiles.containsKey( aTransferId )) return new FileTransferHandler( aTransferId, this);
    throw new AsyncFileTransferException("No handler for transfer id '" + aTransferId + "'");
  }

  private iFileIO getFileIO( String aTransferId ) throws AsyncFileTransferException {
    if(myReceivingFiles.containsKey( aTransferId )) return myReceivingFiles.get( aTransferId );
    if(mySendingFiles.containsKey( aTransferId )) return mySendingFiles.get(aTransferId);
    throw new AsyncFileTransferException("No file io for transfer id '" + aTransferId + "'");
  }

  private void removeFileIO(String aTransferId){
    if(myReceivingFiles.containsKey( aTransferId )) myReceivingFiles.remove( aTransferId );
    if(mySendingFiles.containsKey( aTransferId )) mySendingFiles.remove(aTransferId);
  }

  @Override
  public void waitUntillDone( String aTransferId ) throws AsyncFileTransferException {
    getFileIO( aTransferId ).waitTillDone();
  }

  public boolean containsTransferId(String aTransferId){
    if(myReceivingFiles.containsKey( aTransferId )) return true;
    if(mySendingFiles.containsKey( aTransferId )) return true;
    return false;
  }
}
