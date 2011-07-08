/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
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

public class AsyncFileTransferProtocol extends Protocol {
  private static final Logger LOGGER = Logger.getLogger( AsyncFileTransferException.class );
  
  public static final String ID = "AFP";

  private static enum Command{ACCEPT_FILE, RESEND_PACKET, ACCEPT_PACKET, END_FILE_TRANSFER};
  private static enum Response{FILE_ACCEPTED, PACKET_OK, NOK, UNKNOWN_ID, END_FILE_TRANSFER_OK};

  private static final int PACKET_SIZE = 1024;

  private iObjectStringConverter<FilePacket> myObjectPerister = new Base64ObjectStringConverter<FilePacket>();

  private Map<String, FilePacketIO> myFilePacketIO = new HashMap<String, FilePacketIO>();

  private iAsyncFileTransferHandler myHandler = null;

  public AsyncFileTransferProtocol( String anId ) {
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
    try{
      if(anInput.startsWith( Command.ACCEPT_FILE.name() )){
        String[] theParams = anInput.substring( Command.ACCEPT_FILE.name().length() + 1 ).split( " " );
        
        String theFileName = theParams[0];
        String theUUId = theParams[1];
        int thePacketSize = Integer.parseInt( theParams[2] );
        int theNrOfPackets = Integer.parseInt( theParams[3] );
        
        File theFile = myHandler.acceptFile( theFileName );
        FilePacketIO theIO = FilePacketIO.createForWrite( theFile, theUUId, thePacketSize, theNrOfPackets );
        myFilePacketIO.put( theUUId, theIO );
        
        //create a 
        return Response.FILE_ACCEPTED.name();
      } else if(anInput.startsWith( Command.ACCEPT_PACKET.name() )){
        String thePack = anInput.substring(Command.ACCEPT_PACKET.name().length() + 1 );
        FilePacket thePacket = myObjectPerister.getObject( thePack );
        
        if(!myFilePacketIO.containsKey( thePacket.getId() )){
          return Response.UNKNOWN_ID.name();
        }
        myFilePacketIO.get( thePacket.getId() ).writePacket( thePacket );
        
        return Response.PACKET_OK.name();
      } else if(anInput.startsWith( Command.END_FILE_TRANSFER.name() )){
        String[] theParams = anInput.substring( Command.END_FILE_TRANSFER.name().length() + 1 ).split( " " );
        
        String theUUId = theParams[0];
        FilePacketIO theIO = myFilePacketIO.get(theUUId);
        if(theIO.isComplete()){
          if(myHandler
          return Response.END_FILE_TRANSFER_OK.name();
        } else {
          String theIncompletePacktets = "";
          for(int i=0;i<theIO.getWrittenPackts().length;i++){
            if(!theIO.getWrittenPackts()[i]){
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

  private void sendPacket(AbstractPeer aPeer, FilePacket aPacket) throws AsyncFileTransferException{
    try{
      Message theMessage = new Message();
      theMessage.setDestination( aPeer );
      theMessage.setMessage( createMessage( Command.ACCEPT_PACKET + " " + myObjectPerister.toString( aPacket ) ) );
      getMessageProtocol().sendMessage( theMessage );
    }catch(Exception e){
      throw new AsyncFileTransferException( "Error occured when transferring packet", e );
    }
  }

  private String sendMessageTo(AbstractPeer aPeer, String aMessage) throws AsyncFileTransferException{
    try{
      Message theMessage = new Message();
      theMessage.setDestination( aPeer );
      theMessage.setMessage( aMessage );
      return getMessageProtocol().sendAndWaitForResponse( theMessage, 5, TimeUnit.SECONDS );
    }catch(Exception e){
      throw new AsyncFileTransferException("Could not send message", e);
    }
  }

  public void sendFile(String aPeer, File aFile) throws AsyncFileTransferException{
    try{
      AbstractPeer theDestination = getRoutingTable().getEntryForPeer( aPeer ).getPeer();

      //create a new FilePacketIO for this file transfer
      FilePacketIO theIO = FilePacketIO.createForRead( aFile, PACKET_SIZE );
      //store it
      myFilePacketIO.put( theIO.getId(), theIO );

      //init file transfer with other peer
      String theResult = sendMessageTo( theDestination, createMessage( Command.ACCEPT_FILE.name() + " " + aFile.getName() ) );

      if(theResult.startsWith( Response.FILE_ACCEPTED.name() )){
        //only continue if the file was accepted by the client
        //now loop over all packets and send them to the other peer
        for(int i=0;i<theIO.getNrOfPackets();i++){
          sendPacket( theDestination, theIO.getPacket( i ) );
        }
      }

      String theResponse = null;
      while( !(theResponse = sendMessageTo( theDestination, Command.END_FILE_TRANSFER.name()  + " " + theIO.getId())).equalsIgnoreCase(Response.END_FILE_TRANSFER_OK.name())){
        //if we get here not all packets where correctly delivered resend the missed packets
        String[] thePacketsToResend = theResponse.split(" ");
        for(int i=0;i<thePacketsToResend.length;i++){
          sendPacket(theDestination, theIO.getPacket(Integer.parseInt(thePacketsToResend[i])));
        }
      }

    }catch(Exception e){
      throw new AsyncFileTransferException("Could not send file to peer '" + aPeer + "'", e);
    }
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub

  }

}
