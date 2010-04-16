package chabernac.queue;

import chabernac.log.Logger;

public class TriggeredQueueReader implements iQueueListener, Runnable{
  private TriggeringQueue myQueue = null;
  private int myThreads = 1;
  private iObjectProcessor myObjectProcessor = null;
  private int activeThreads = 0;
  
  public TriggeredQueueReader(TriggeringQueue aQueue, iObjectProcessor aProcessor){
    this(aQueue, 1, aProcessor);
  }
  
  public TriggeredQueueReader(TriggeringQueue aQueue, int aTriggeringLimit, iObjectProcessor aProcessor){
    myQueue = aQueue;
    myObjectProcessor = aProcessor;
    myQueue.addQueueListener(this, aTriggeringLimit);
  }

  public void trigger() {
    while(activeThreads < myThreads && myQueue.size() != 0){
      new Thread(this).start();
      Thread.yield();
      activeThreads++;
      Logger.log(this,"Active threads: " + activeThreads);
    }
  }
  
  public void run(){
    while(myQueue.size() > 0){
      myObjectProcessor.processObject(myQueue.get());
    }
    activeThreads--;
  }
  
  public int getThreads(){ return myThreads; }
  public void setThread(int threads){ myThreads = threads; }

}
