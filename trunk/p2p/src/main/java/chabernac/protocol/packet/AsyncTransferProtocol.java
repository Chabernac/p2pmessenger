/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;

import org.apache.log4j.Logger;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.AsyncMessageProcotol;
import chabernac.protocol.message.Message;
import chabernac.protocol.packet.AbstractTransferState.State;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

public class AsyncTransferProtocol extends Protocol implements iTransferContainer{
  private static final Logger LOGGER = Logger.getLogger(AsyncTransferException.class);

  public static final String ID = "ATP";

  private Map<String, AbstractTransferState> myTransferStates = new ConcurrentHashMap<String, AbstractTransferState >();

  //local instance of state change listener which will announce the new state to the remote peer
  private iStateChangeListener myStateChangeListener = new StateChangeListener();

  private static enum Command{SETUP_TRANSFER, STATE_CHANGE};
  private static enum Response{TRANSFER_ID_NOT_FOUND, UNKNOWN_COMMAND, NOK, OK};
  private static enum TransferType{FILE, AUDIO};

  private List<iTransferListener> myTransferListener = new ArrayList< iTransferListener >();

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

  public void addTransferListener(iTransferListener aTransferListener){
    myTransferListener.add( aTransferListener );
  }

  public void removeTransferListener(iTransferListener aTransferListener){
    myTransferListener.remove( aTransferListener );
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
        TransferType theTransferType = TransferType.valueOf(theParams[1]);
        AbstractTransferState theReceiveTransferState = null;
        if(theTransferType == TransferType.FILE){
          String theTransferId = theParams[2];
          int theNrOfPackets = Integer.parseInt( theParams[3] );
          int thePacketSize = Integer.parseInt( theParams[4] );
          String theFile = theParams[5];
          String theRemotePeer = theParams[6];
          theReceiveTransferState = FileTransferState.createForReceive( getPacketProtocol(), theTransferId, new File(theFile), theRemotePeer, theNrOfPackets, thePacketSize);
        } else if(theTransferType == TransferType.AUDIO){
          AudioFormat.Encoding theEncoding = new AudioFormat.Encoding(theParams[2]);
          int theSamplesPerSeconds = Integer.parseInt(theParams[3]);
          int theBits = Integer.parseInt(theParams[4]);
          int theSpeexQuality = Integer.parseInt(theParams[5]);
          int thePacketsPerSecond = Integer.parseInt(theParams[6]);
          String theTransferId = theParams[7];
          String theRemotePeer = theParams[8];
          theReceiveTransferState = AudioTransferState.createForReceive( getPacketProtocol(), theTransferId, theRemotePeer, theEncoding, theSamplesPerSeconds, theBits, theSpeexQuality, thePacketsPerSecond);
        }
        theReceiveTransferState.addStateChangeListener( myStateChangeListener );
        addTransfer( theReceiveTransferState, true );
        return Response.OK.name();
      }catch(Exception e){
        LOGGER.error("Unable to setup file transfer", e);
        return Response.NOK.name();
      }
    }
    return Response.UNKNOWN_COMMAND.name();
  }

  private void addTransfer(AbstractTransferState aTransfer, boolean isIncoming){
    myTransferStates.put(aTransfer.getTransferId(), aTransfer);
    for(iTransferListener theListener : myTransferListener){
      theListener.newTransfer( aTransfer, isIncoming );
    }
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub

  }

  public AbstractTransferState startFileTransfer(File aFile, String aPeer, int aPacketSize, int anOutstandingPackets) throws AsyncTransferException{
    try{
      String theTransferId = UUID.randomUUID().toString();
      FileTransferState theFileTransfeState = FileTransferState.createForSend( getPacketProtocol(), theTransferId, aFile, aPeer, aPacketSize, anOutstandingPackets );
      theFileTransfeState.addStateChangeListener( myStateChangeListener );

      addTransfer( theFileTransfeState, false );
      String theResponse = sendMessage( Command.SETUP_TRANSFER + ";" + TransferType.FILE +  ";" + theTransferId + ";" + theFileTransfeState.getNrOfPackets() + ";" + aPacketSize + ";" + aFile.getName() + ";" + getRoutingTable().getLocalPeerId(), aPeer );
      if(!Response.OK.name().equalsIgnoreCase( theResponse )) throw new AsyncTransferException("an error occured while setting up transfer with id '" + theTransferId + "'");

      return theFileTransfeState;
    }catch(Exception e){
      throw new AsyncTransferException("Could not start transfer", e);
    }
  }
  
  public AbstractTransferState startAudioTransfer(String aPeer, AudioFormat.Encoding anEncoding, int aSamplesPerSecond, int aBits, int aSpeexQuality, int aPacketsPerSecond) throws AsyncTransferException{
    try{
      String theTransferId = UUID.randomUUID().toString();
      AudioTransferState theFileTransfeState = AudioTransferState.createForSend( getPacketProtocol(), theTransferId, aPeer, anEncoding, aSamplesPerSecond, aBits, aSpeexQuality, aPacketsPerSecond);
      theFileTransfeState.addStateChangeListener( myStateChangeListener );

      addTransfer( theFileTransfeState, false );
      String theResponse = sendMessage( Command.SETUP_TRANSFER + ";" + TransferType.AUDIO  + ";" + anEncoding.toString() + ";" + aSamplesPerSecond + ";" + aBits + ";" + aSpeexQuality + ";" + aPacketsPerSecond + ";" + theTransferId + ";" +  getRoutingTable().getLocalPeerId(), aPeer); 
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

  @Override
  public Set< AbstractTransferState > getTransferStates() {
    return Collections.unmodifiableSet(  new LinkedHashSet< AbstractTransferState >(myTransferStates.values()));
  }

  @Override
  public AbstractTransferState getTransferState( String aTransferId ) {
    return myTransferStates.get(aTransferId);
  }

}
