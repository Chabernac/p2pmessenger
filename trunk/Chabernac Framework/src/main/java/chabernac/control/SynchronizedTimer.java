
package chabernac.control;

import chabernac.utils.Debug;

public class SynchronizedTimer implements Runnable{
  private int myTimeout;
  private boolean stop = false;
  
  public SynchronizedTimer(int aTimeout){
    myTimeout = aTimeout;  
  }
  
  public synchronized void waitForSynch(){
    waitForSynch(1);
  }
  
  public synchronized void waitForSynch(int aNrSyncPoint){
    int aCounter = 0;
    while(aCounter < aNrSyncPoint){
      try{
        Thread.yield();
        wait();
      }catch(Exception e){ Debug.log(this,"Exception",e); }
      aCounter++;
    }
  }
  
  public void run(){
    while(!stop){
      synchronized(this){
        notifyAll();
      }
      try{
        Thread.sleep(myTimeout);
      }catch(Exception e){ }
    }
  }
  
  public void startTimer(){
    stop = false;
    new Thread(this).start();
  }
  
  public void stopTimer(){
    stop = true;
  }
  
  
}
