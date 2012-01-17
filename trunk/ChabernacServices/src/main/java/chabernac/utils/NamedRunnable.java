/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

public abstract class NamedRunnable implements Runnable {
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
    String[] theName = Thread.currentThread().getName().split( "\\[" );
    
    Thread.currentThread().setName( theName[0].trim() + " [" + myName + "] start" );
    doRun();
    Thread.currentThread().setName( theName[0].trim() + " [" + myName + "] end" );
  }
  
  protected abstract void doRun();
}
