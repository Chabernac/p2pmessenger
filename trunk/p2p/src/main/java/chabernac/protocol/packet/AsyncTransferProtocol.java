/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.AsyncMessageProcotol;
import chabernac.protocol.message.Message;
import chabernac.protocol.packet.AbstractTransferState.State;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

public class AsyncTransferProtocol extends Protocol implements iPacketTransferController {
  private static final Logger LOGGER = Logger.getLogger(AsyncTransferException.class);
  
  public static final String ID = "ATP";

  private Map<String, AbstractTransferState> myTransferStates = new HashMap< String, AbstractTransferState >();

  private iStateChangeListener myStateChangeListener = new StateChangeListener();

  private static enum Command{STATE_CHANGE};
  private static enum Response{TRANSFER_ID_NOT_FOUND, UNKNOWN_COMMAND};

  public AsyncTransferProtocol ( String anId ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Async transfer protocol";
  }

  private AsyncMessageProcotol getMessageProtocol() throws ProtocolException{
    return (AsyncMessageProcotol)findProtocolContainer().getProtocol( AsyncMessageProcotol.ID);
  }

  private PacketProtocol getPacketProtocol() throws ProtocolException{
    return (PacketProtocol)findProtocolContainer().getProtocol( PacketProtocol.ID);
  }

  private RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID)).getRoutingTable();
  }


  @Override
  public void addPacketTransferListener( String aTransferId, iPacketTransferListener aPacketTransferListener ) {
    // TODO Auto-generated method stub

  }

  @Override
  public void start( String aTransferId ) throws StateChangeException {
    if(myTransferStates.containsKey( aTransferId )){
      myTransferStates.get( aTransferId ).start();
    }
  }

  @Override
  public void stop( String aTransferId ) throws StateChangeException {
    if(myTransferStates.containsKey( aTransferId )){
      myTransferStates.get( aTransferId ).stop();
    }
  }

  @Override
  public void waitUntillDone( String aTransferId ) {
    if(myTransferStates.containsKey( aTransferId )){
      //TODO think of how we should implement this
    }

  }

  @Override
  public String handleCommand( String aSessionId, String anInput ) {
    if(anInput.startsWith( Command.STATE_CHANGE.name() )){
      String[] theParams = anInput.split( ";" );
      String theTransferId = theParams[1];
      String theNewState =  theParams[2];
      if(!myTransferStates.containsKey( theTransferId )){
        return Response.TRANSFER_ID_NOT_FOUND.name();
      }
      
      AbstractTransferState.State theNewStat = AbstractTransferState.State.valueOf( theNewState );
      myTransferStates.get( theTransferId ).changeToState( theNewStat );
    }
    return Response.UNKNOWN_COMMAND.name();
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub

  }

  public PacketTransferHandler startFileTransfer(File aFile, String aPeer) throws AsyncTransferException{
    try{
      String theTransferId = UUID.randomUUID().toString();
      FileTransferState theFileTransfeState = FileTransferState.createForSend( getPacketProtocol(), theTransferId, aFile, aPeer );
      theFileTransfeState.addStateChangeListener( myStateChangeListener );
      myTransferStates.put( theTransferId, theFileTransfeState );
      return new PacketTransferHandler( theTransferId, this );
    }catch(ProtocolException e){
      throw new AsyncTransferException("Could not start transfer", e);
    }
  }

  private void sendMessage(String aMessage, String aPeer) throws AsyncTransferException{
    try{
      Message theMessage = new Message();
      theMessage.setDestination( getRoutingTable().getEntryForPeer( aPeer ).getPeer() );
      theMessage.setProtocolMessage( true );
      theMessage.setMessage( createMessage( aMessage ) );
      getMessageProtocol().sendMessage( theMessage );
    }catch(Exception e){
      throw new AsyncTransferException("Could not send message", e);
    }
  }

  private class StateChangeListener implements iStateChangeListener {
    @Override
    public void stateChanged( String aTransferId, State anOldState, State aNewState ) {
      try{
      if(myTransferStates.containsKey( aTransferId )){
        String theRemotePeer = myTransferStates.get(aTransferId).getRemotePeer();
        sendMessage( Command.STATE_CHANGE.name() + ";" + aTransferId + ";" + aNewState, theRemotePeer );
      }
      }catch(AsyncTransferException e){
        LOGGER.error("Unable to announce state changed", e);
      }
    }
  }

}
