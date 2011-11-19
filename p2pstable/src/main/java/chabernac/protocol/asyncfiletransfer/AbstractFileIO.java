/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractFileIO implements iFileIO {
  private List<iFileTransferListener> myListeners = new ArrayList<iFileTransferListener>();

  @Override
  public void addFileTransferListener( iFileTransferListener aListener ) {
    myListeners.add(aListener);
  }
  
  protected void notifyListeners(){
    for(iFileTransferListener theListener : myListeners) theListener.transferStateChanged();
  }
  
  @Override
  public boolean isPaused() {
    if(getPercentageComplete() == null ) return false;
    if(getPercentageComplete().getDivisor() == 0) return false;
    if(isTransferring()) return false;
    return !isComplete();
  }
}
