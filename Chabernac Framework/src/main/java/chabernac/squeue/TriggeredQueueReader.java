/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */


package chabernac.squeue;

import org.apache.log4j.Logger;

/**
 * 
* Queue reader made the listen to a triggering queue.  From the moment the queue exceeds the triggering limit
* it starts triggering all its listerens.  The triggeringQueueReader will start reading the queue and create as 
* many threads as specified in the setThreads method.
*
* @version v1.0.0      Sep 20, 2005
*<pre><u><i>Version History</u></i>
*
* v1.0.0 Sep 20, 2005 - initial release       - Guy Chauliac
*
*</pre>
*
* @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */

public class TriggeredQueueReader implements iQueueListener, Runnable{
  private static Logger        logger       = Logger.getLogger(TriggeredQueueReader.class);
  
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
    }
  }
  
  public void run(){
    while(myQueue.size() > 0){
      try{
        myObjectProcessor.processObject(myQueue.get());
      }catch(Throwable e){
        logger.error("An error occured while processing object", e);
      }
    }
    activeThreads--;
    synchronized(this){
      notifyAll();
    }
  }
  
  public synchronized void waitTillFinished(){
    while(activeThreads > 0 || myQueue.size() > 0){
      try{
        wait();
      }catch(InterruptedException e){
        logger.debug("Could not wait", e);
      }
    }
  }
  
  public int getThreads(){ return myThreads; }
  public void setThreads(int threads){ myThreads = threads; }

}
