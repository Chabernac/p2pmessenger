/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.squeue;

import org.apache.log4j.Logger;

import chabernac.command.Command;
import chabernac.utils.Timer;

/**
 *
 *
 * @version v1.0.0      17-jun-2004
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 17-jun-2004 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:Guy.Chauliac@axa.be"> Guy Chauliac </a>
 */
public abstract class QueueReader implements Runnable{
  private static Logger        logger       = Logger.getLogger(Queue.class);
  
  private Queue myQueue = null;
  private int myThreads;
  private int myCurrentThreads = 0;
  private boolean stop = false;
  private boolean stopOnEmptyQueue = false;
  private boolean timeout = false;
  private int myTimeout = -1;
  private Timer myTimer = null;
  
  public QueueReader(Queue aQueue){
    this(aQueue, 10);
  }
  
  public QueueReader(Queue aQueue, int threads){
    myQueue = aQueue;
    myThreads = threads;
    //startReadingQueue();
  }
  
  public synchronized void setThreads(int threads){ myThreads = threads; }
  public int getThreads(){ return myThreads; }
  public Queue getQueue(){ return myQueue; }
  public void setStopOnEmptyQueue(boolean aBoolean){ stopOnEmptyQueue = aBoolean; }
  public void setTimeout(int aTimeout){ myTimeout = aTimeout; }
  public int getTimeout(){ return myTimeout; }
  
  
  public void startReadingQueue(){
    stop = false;
    new Thread(this).start();
  }
  
  public void stopReadingQueue(){
    stop = true;
    myQueue.unlock();
  }
  
  public void finalize(){
    stopReadingQueue();
    myQueue = null;
  }
  
  private synchronized void waitForFinishedThread(){
    while(myCurrentThreads >= myThreads){
      try{
        wait();
      }catch(InterruptedException e){
        logger.error("Could not wait", e);
      }
    }
  } 
  
  private synchronized void incrementThreads(){
    myCurrentThreads++;
  }
  
  private synchronized void decreaseThreads(){
    myCurrentThreads--;
    if(myCurrentThreads < myThreads) notifyAll();
  }
  
  public void run(){
  	startTimer();
  	
    while(!stop()){
      waitForFinishedThread();   
      //logger.debug("Waiting for object...");
      Object theObject = myQueue.get();
	  //logger.debug("Object retrieved");
      if(myTimer != null) myTimer.restart();
      if(theObject != null) new ObjectProcesser(theObject);
    }
    
    if(myTimer != null) {
    	myTimer.stopTimer();
    	myTimer = null;
    }
    
    synchronized(this){
      notifyAll();
    }
  }
  
  private void startTimer(){
  	timeout = false;
  	if(myTimeout > -1){
  		myTimer = new Timer(myTimeout, new Command(){
  			public void execute(){
  				//logger.debug("Timeout occured, unlocking queue ...");
  				timeout = true;
  				myQueue.unlock();
  			}
  		});
  		//logger.debug("Starting timer with timeout: " + myTimeout);
  		myTimer.startTimer();
  	}
  }
  
  public synchronized void waitTillFinished(){
    while(!stop || myCurrentThreads > 0){
      try{
      	//logger.debug("Waiting ..." + stop + " " + myCurrentThreads);
        wait();
		//logger.debug("Notify received!");
      }catch(InterruptedException e){
        logger.error("could not wait", e);
      }
    }
  }
  
  private synchronized boolean stop(){
  	//logger.debug("Timeout: " + timeout + " StopOnEmptyQueue: " + stopOnEmptyQueue + " queue size: " + myQueue.size());
  	if(timeout == true) stop = true;
    if(myQueue == null) stop = true;
    if(stopOnEmptyQueue && myQueue.size() == 0) stop = true;
	//logger.debug("Stop: " + stop);
    return stop;
  }
  
  protected abstract void processObject(Object anObject); 
  
  private class ObjectProcesser implements Runnable{
    Object myObject = null;
    
    public ObjectProcesser(Object anObject){
      myObject = anObject;
      incrementThreads();
      try{
        new Thread(this).start();
      }catch(Exception e){
        decreaseThreads();
      }     
    }
    
    public void run(){
      try{
        processObject(myObject);
      }finally{
        decreaseThreads();
      }
    }
  }
}
