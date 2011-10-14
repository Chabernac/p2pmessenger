/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.AsyncMessageProcotol;
import chabernac.protocol.message.Message;
import chabernac.protocol.packet.AbstractTransferState.State;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

public class AsyncTransferProtocol extends Protocol {
  private static final Logger LOGGER = Logger.getLogger(AsyncTransferException.class);

  public static final String ID = "ATP";

  private Map<String, AbstractTransferState> myTransferStates = new HashMap< String, AbstractTransferState >();

  private iStateChangeListener myStateChangeListener = new StateChangeListener();

  private static enum Command{SETUP_TRANSFER, STATE_CHANGE};
  private static enum Response{TRANSFER_ID_NOT_FOUND, UNKNOWN_COMMAND, NOK, OK};
  
  private iTransferListener myTransferListener;

  public AsyncTransferProtocol ( ) {
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
  
  public iTransferListener getTransferListener() {
    return myTransferListener;
  }

  public void setTransferListener( iTransferListener aTransferListener ) {
    myTransferListener = aTransferListener;
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

      try{
        AbstractTransferState.State theNewStat = AbstractTransferState.State.valueOf( theNewState );
        myTransferStates.get( theTransferId ).changeToState( theNewStat );
      }catch(StateChangeException e){
        LOGGER.error("Could not change state", e);
        return Response.NOK.name();
      }
    } else if(anInput.startsWith( Command.SETUP_TRANSFER.name() )){
      try{
        String[] theParams = anInput.split( ";" );
        String theTransferId = theParams[1];
        int theNrOfPackets = Integer.parseInt( theParams[2] );
        String theFile = theParams[3];
        String theRemotePeer = theParams[4];
        AbstractTransferState theReceiveTransferState = FileTransferState.createForReceive( getPacketProtocol(), theTransferId, new File(theFile), theRemotePeer, theNrOfPackets);
        theReceiveTransferState.addStateChangeListener( myStateChangeListener );
        myTransferStates.put( theTransferId, theReceiveTransferState );
        myTransferListener.incomingTransfer( theReceiveTransferState );
        return Response.OK.name();
      }catch(Exception e){
        LOGGER.error("Unable to setup file transfer", e);
        return Response.NOK.name();
      }
    }
    return Response.UNKNOWN_COMMAND.name();
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub

  }

  public AbstractTransferState startFileTransfer(File aFile, String aPeer) throws AsyncTransferException{
    try{
      String theTransferId = UUID.randomUUID().toString();
      FileTransferState theFileTransfeState = FileTransferState.createForSend( getPacketProtocol(), theTransferId, aFile, aPeer );
      theFileTransfeState.addStateChangeListener( myStateChangeListener );
      myTransferStates.put( theTransferId, theFileTransfeState );
      
      String theResponse = sendMessage( Command.SETUP_TRANSFER + ";" + theTransferId + ";" + theFileTransfeState.getNrOfPackets() + ";" + aFile.getName() + ";" + getRoutingTable().getLocalPeerId(), aPeer );
      if(!Response.OK.name().equalsIgnoreCase( theResponse )) throw new AsyncTransferException("an error occured while setting up transfer with id '" + theTransferId + "'");
      
      return theFileTransfeState;
    }catch(Exception e){
      throw new AsyncTransferException("Could not start transfer", e);
    }
  }

  private String sendMessage(String aMessage, String aPeer) throws AsyncTransferException{
    try{
      Message theMessage = new Message();
      theMessage.setDestination( getRoutingTable().getEntryForPeer( aPeer ).getPeer() );
      theMessage.setProtocolMessage( true );
      theMessage.setMessage( createMessage( aMessage ) );
      return getMessageProtocol().sendAndWaitForResponse(  theMessage, 10, TimeUnit.SECONDS );
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
