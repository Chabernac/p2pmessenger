/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

public abstract class NamedRunnable implements Runnable {

  @Override
  public void run() {
    String[] theName = Thread.currentThread().getName().split( "\\[" );
    
    String theClassName = getClass().getName();
    Thread.currentThread().setName( theName[0].trim() + "[" + theClassName + "] start" );
    doRun();
    Thread.currentThread().setName( theName[0].trim() + "[" + theClassName + "] end" );
  }
  
  protected abstract void doRun();
}
