/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.util.concurrent;

import java.util.ArrayList;
import java.util.List;

public abstract class MonitorrableRunnable implements Runnable {
  public static enum Status{NOT_RUNNING, RUNNING, END};
  
  private Status myStatus = Status.NOT_RUNNING;
  
  private List< iRunnableListener > myListeners = new ArrayList< iRunnableListener >();
  
  public void addListener(iRunnableListener aListener){
    myListeners.add(aListener);
  }
  
  public void removeListener(iRunnableListener aListener){
    myListeners.remove( aListener );
  }
  
  public void notifyListeners(){
    for(iRunnableListener theListener : myListeners){
      theListener.statusChanged( myStatus, getExtraInfo() );
    }
  }
  
  private void setStatus(Status aStatus){
    myStatus = aStatus;
    notifyListeners();
  }

  public void run(){
    setStatus( Status.RUNNING );
    try{
      doRun();
    }finally{
      setStatus( Status.END );
    }
  }

  protected abstract void doRun();
  protected abstract String getExtraInfo();
}
