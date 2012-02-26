/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

public abstract class NamedRunnable implements Runnable {
  private static AtomicLong myRunnableCounter = new AtomicLong(0);
  
  private final Logger LOGGER = Logger.getLogger(NamedRunnable.class);
  private final String myName;
  
  public NamedRunnable(){
    this(null);
  }
  
  public NamedRunnable(String aName){
    if(aName == null){
      myName = getClass().getName(); 
    } else {
      myName = aName;
    }
  }

  @Override
  public void run() {
    myRunnableCounter.incrementAndGet();
    String[] theName = Thread.currentThread().getName().split( "\\[" );
    
    Thread.currentThread().setName( theName[0].trim() + " [" + myName + "] start" );
    try{
      doRun();
    }catch(RuntimeException e){
      LOGGER.error("An error occured while executing runnable", e);
      throw e;
    } finally {
      Thread.currentThread().setName( theName[0].trim() + " [" + myName + "] end" );
      Thread.yield();
      myRunnableCounter.decrementAndGet();
//      LOGGER.debug("Number of named runnables: '" + myRunnableCounter.get() + "'");
    }
  }
  
  protected abstract void doRun();
}
