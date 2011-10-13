/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTransferState {
  public static enum State{PENDING, RUNNING, STOPPED, CANCELLED, DONE, FAILED};
  
  private final String myTransferId;
  private State myState = State.PENDING;
  private iPacketTransfer myTransfer;
  private final String myRemotePeer;
  
  private List< iStateChangeListener > myStateChangeListeners = new ArrayList< iStateChangeListener >();
  
  private final PacketTransferListener myPacketTransferListener = new PacketTransferListener();
  
  public AbstractTransferState ( String aTransferId, String aRemotePeer ) {
    super();
    myTransferId = aTransferId;
    myRemotePeer = aRemotePeer;
  }

  public void changeToState(State aState) throws StateChangeException{
    if(myState == aState) return;
    if(aState == State.RUNNING) start();
    else if(aState == State.STOPPED) stop();
    else if(aState == State.CANCELLED) cancel();
  }
  
  public void start() throws StateChangeException{
    //we can only start if the current state is pending or stopped
    
    if(myState == State.PENDING || myState == State.STOPPED){
      if(myTransfer == null){
        try {
          myTransfer = createPacketTransfer();
        } catch ( IOException e ) {
          throw new StateChangeException("Could not instantiate packet transfer", e);
        }
      }
      myTransfer.start();
      changeState( State.RUNNING );
    } else {
      throw new StateChangeException("Could not start from state '" + myState + "'");
    }
  }
  
  public void stop() throws StateChangeException{
    //we can only stop if the current state is running
    if(myState == State.RUNNING){
      myTransfer.stop();
      changeState( State.STOPPED );
    } else {
      throw new StateChangeException("Could not stop from state '" + myState + "'");
    }
  }
  
  public void cancel() throws StateChangeException{
    //we can only cancel if the current state is pending running or stopped
    if(myState == State.RUNNING || myState == State.STOPPED || myState == State.PENDING){
      if(myTransfer != null){
        myTransfer.stop();
      }
      changeState( State.CANCELLED );
    } else {
      throw new StateChangeException("Could not cancel from state '" + myState + "'");
    }
    
  }
  
  protected abstract iPacketTransfer createPacketTransfer() throws IOException;
  
  private void changeState(State aNewState){
    State theOldState = myState;
    myState = aNewState;
    //if the state is changed to running then add auto detection for the states DONE and FAILED
    if(theOldState != State.RUNNING &&  myState == State.RUNNING){
      myTransfer.addPacketTransferListener( myPacketTransferListener );
    } else if( theOldState == State.RUNNING && myState != State.RUNNING){
      myTransfer.removePacketTransferListener( myPacketTransferListener );
    }
    notifyStateChange(theOldState, aNewState);
  }

  private void notifyStateChange( State anOldState, State aNewState ) {
    for(iStateChangeListener theListener : myStateChangeListeners){
      theListener.stateChanged( myTransferId, anOldState, aNewState );
    }
  }
  
  public void addStateChangeListener(iStateChangeListener aStateChangeListener){
    myStateChangeListeners.add(aStateChangeListener);
  }
  
  public void removeStateChangeListener(iStateChangeListener aStateChangeListener){
    myStateChangeListeners.remove(aStateChangeListener);
  }
  
  public String getRemotePeer(){
    return myRemotePeer;
  }
  
  public String getTransferId(){
    return myTransferId;
  }
  
  private class PacketTransferListener implements iPacketTransferListener {
    @Override
    public void transferUpdated( PacketTransferState aPacketTransferState ) {
      if(aPacketTransferState.getState() == PacketTransferState.State.FAILED){
        changeState( State.FAILED );
      } else if(aPacketTransferState.getState() == PacketTransferState.State.DONE){
        changeState( State.DONE );
      }
    }
  }

}
