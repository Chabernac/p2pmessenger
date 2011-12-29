package chabernac.protocol.packet;

import java.util.ArrayList;
import java.util.List;

import chabernac.protocol.packet.AbstractTransferState.State;

public class PacketTransferStateAdapterListener {
  private final AbstractTransferState myTransferState;
  private List<iTransferStateListener> myListeners = new ArrayList<iTransferStateListener>();
  private PacketTransferListener myPacketTransferListener = new PacketTransferListener();

  public PacketTransferStateAdapterListener(AbstractTransferState aTransferState) {
    super();
    myTransferState = aTransferState;
    addListeners();
  }

  private void addListeners() {
    myTransferState.addStateChangeListener(new StatecChangeListener());
  }
  
  public void addTransferStateListener(iTransferStateListener aListener){
    myListeners.add(aListener);
  }
  
  public void removeTransferStateListener(iTransferStateListener aListener){
    myListeners.remove(aListener);
  }
  
  private void notifyListeners(){
    for(iTransferStateListener theListener : myListeners){
      theListener.transferStateChanged(myTransferState.getTransferState());
    }
  }
  
  private class StatecChangeListener implements iStateChangeListener{

    @Override
    public void stateChanged(String aTransferId, State anOldState, State aNewState) {
      if(aNewState == State.RUNNING){
        myTransferState.addPacketTransferListener(myPacketTransferListener);
      } 
      notifyListeners();
    }
  }
  
  private class PacketTransferListener implements iPacketTransferListener {

    @Override
    public void transferUpdated(PacketTransferState aPacketTransferState) {
      notifyListeners();
    }
  }
  
  
}
