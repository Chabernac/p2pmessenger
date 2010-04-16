/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */


package chabernac.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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

public class TriggeredQueueReader implements iQueueListener{
  private static Logger        logger       = Logger.getLogger(TriggeredQueueReader.class);

  private TriggeringQueue myQueue = null;
  private iObjectProcessor myObjectProcessor = null;
  private ExecutorService myExecutorService = null;
  private AtomicLong myCounter = new AtomicLong(0);


  public TriggeredQueueReader(TriggeringQueue aQueue, iObjectProcessor aProcessor){
    this(aQueue, 1, aProcessor);
  }

  public TriggeredQueueReader(TriggeringQueue aQueue, int aTriggeringLimit, iObjectProcessor aProcessor){
    myQueue = aQueue;
    myObjectProcessor = aProcessor;
    myQueue.addQueueListener(this, aTriggeringLimit);
    myExecutorService = Executors.newFixedThreadPool( 1 );
  }

  public void trigger() {
//    System.out.println("Trigger!");
    //execute the followwing code synchronized on the queue
    //this way (at least for ArrayQueue) no item can be get from the queue
    //between the size() and get() and.  Otherwise this would lead to a deadlock
    //because get() would block until an element is put on the queue
    synchronized(myQueue){
      while(myQueue.size() > 0){
        Object theObject = myQueue.get();
  //      System.out.println("Putting object on objectprocessor: " + theObject);
        myCounter.incrementAndGet();
        myExecutorService.submit( new ObjectProcessor(theObject) );
      }
    }
  }

  public synchronized void waitTillFinished(long aTimeout, TimeUnit aTimeUnit) throws InterruptedException{
    myExecutorService.shutdown();
    myExecutorService.awaitTermination( aTimeout, aTimeUnit);
  }

  public void setThreads(int threads){ 
    if(myExecutorService != null){
      myExecutorService.shutdown();
    }
    myExecutorService = Executors.newFixedThreadPool( threads );
  }

  private class ObjectProcessor implements Runnable{
    private Object myObject = null;

    public ObjectProcessor(Object anObject){
      myObject = anObject;
    }

    public void run(){
      try{
        myObjectProcessor.processObject(myObject);
      }catch(Throwable e){
        logger.error("An error occured while processing object", e);
      } finally {
        myCounter.decrementAndGet();
      }
    }
  }
  
  
  public long getUnProcessedObjects(){
    return myCounter.get();
  }

}
