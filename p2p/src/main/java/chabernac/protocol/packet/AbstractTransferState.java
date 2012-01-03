/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTransferState {
  public static enum State{PENDING, RUNNING, STOPPED, CANCELLED, DONE, FAILED};
  public static enum Direction{SEND, RECEIVE};
  
  protected final String myTransferId;
  private State myState = State.PENDING;
  private iPacketTransfer myTransfer;
  protected final String myRemotePeer;
  
  private List< iStateChangeListener > myStateChangeListeners = new ArrayList< iStateChangeListener >();
  
  private final PacketTransferListener myPacketTransferListener = new PacketTransferListener();
  
  private PacketTransferStateAdapterListener myAdapterListener = null;
  protected final Direction myDirection;
  
  public AbstractTransferState ( String aTransferId, String aRemotePeer, Direction aDirection ) {
    super();
    myTransferId = aTransferId;
    myRemotePeer = aRemotePeer;
    myDirection = aDirection;
  }
  
  public void start() throws StateChangeException{
    startInternal( true );
  }

  private void startInternal(boolean isFireStateChange) throws StateChangeException{
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
      changeState( State.RUNNING, isFireStateChange );
    } else {
      throw new StateChangeException("Could not start from state '" + myState + "'");
    }
  }
  
  public void stop() throws StateChangeException{
    stopInternal(true);
  }
  
  private void stopInternal(boolean isFireChangeStateEvent) throws StateChangeException{
    //we can only stop if the current state is running
    if(myState == State.RUNNING){
      myTransfer.stop();
      changeState( State.STOPPED, isFireChangeStateEvent );
    } else {
      throw new StateChangeException("Could not stop from state '" + myState + "'");
    }
  }
  
  public void cancel() throws StateChangeException{
    cancelInternal( true);
  }
  
  private void cancelInternal(boolean isFireStateChangeEvent) throws StateChangeException{
    //we can only cancel if the current state is pending running or stopped
    if(myState == State.RUNNING || myState == State.STOPPED || myState == State.PENDING){
      if(myTransfer != null){
        myTransfer.stop();
      }
      changeState( State.CANCELLED, isFireStateChangeEvent );
    } else {
      throw new StateChangeException("Could not cancel from state '" + myState + "'");
    }
    
  }
  
  protected abstract iPacketTransfer createPacketTransfer() throws IOException;
  public abstract String getTransferDescription();
  
  public void changeToState(State aState) throws StateChangeException{
    if(myState == aState) return;
    if(aState == State.RUNNING) startInternal(true);
    else if(aState == State.STOPPED) stopInternal(true);
    else if(aState == State.CANCELLED) cancelInternal(true);
  }
  
  private void changeState(State aNewState, boolean isFireStatChangeEvent){
    State theOldState = myState;
    myState = aNewState;
    //if the state is changed to running then add auto detection for the states DONE and FAILED
    if(theOldState != State.RUNNING &&  myState == State.RUNNING){
      myTransfer.addPacketTransferListener( myPacketTransferListener );
    } else if( theOldState == State.RUNNING && myState != State.RUNNING){
      myTransfer.removePacketTransferListener( myPacketTransferListener );
    }
    if(isFireStatChangeEvent) notifyStateChange(theOldState, aNewState);
  }
  
  public State getState(){
    return myState;
  }
  
  public PacketTransferState getPacketTransferState() throws TransferStateException{
    if(myTransfer == null) throw new TransferStateException("Transfer has not yet been started");
    return myTransfer.getTransferState();
  }
  
  public TransferState getTransferState(){
    try {
      return new TransferState(myTransferId, myDirection, myState, myTransfer == null ? null : getPacketTransferState());
    } catch (TransferStateException e) {
      return null;
    }
  }
  
  public boolean waitForState(State aState, int aTimeout, TimeUnit aTimeUnit) throws InterruptedException{
    CountDownLatch theLatch = new CountDownLatch( 1 );
    addStateChangeListener( new WaitForStateListener(aState, theLatch) );
    theLatch.await(aTimeout, aTimeUnit);
    return getState() == aState;
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
  
  public void addPacketTransferListener(iPacketTransferListener aPacketTransferListener){
    myTransfer.addPacketTransferListener( aPacketTransferListener );
  }
  
  public synchronized void addTransferStateListener(iTransferStateListener aListener){
    if(myAdapterListener == null) myAdapterListener = new PacketTransferStateAdapterListener(this);
    myAdapterListener.addTransferStateListener(aListener);
  }
  
  public void removeTransferStateListener(iTransferStateListener aListener){
    if(myAdapterListener != null){
      myAdapterListener.removeTransferStateListener(aListener);
    }
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
        changeState( State.FAILED, true );
      } else if(aPacketTransferState.getState() == PacketTransferState.State.DONE){
        changeState( State.DONE, true );
      }
    }
  }
  
  private class WaitForStateListener implements iStateChangeListener {
    private final State myState;
    private final CountDownLatch myLatch;
    
    public WaitForStateListener ( State aState , CountDownLatch aLatch ) {
      myState = aState;
      myLatch = aLatch;
    }

    @Override
    public void stateChanged( String aTransferId, State anOldState, State aNewState ) {
     if(aNewState == myState){
       myLatch.countDown();
     }
    }
  }
}
