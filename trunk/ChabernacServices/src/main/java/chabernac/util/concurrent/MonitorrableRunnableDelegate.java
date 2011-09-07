/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.util.concurrent;

public class MonitorrableRunnableDelegate extends MonitorrableRunnable {
  private final Runnable myDelegate;
  private String myExtraInfo = null;
  
  public MonitorrableRunnableDelegate( Runnable aDelegate ) {
    super();
    myDelegate = aDelegate;
  }

  @Override
  protected void doRun() {
    myDelegate.run();
  }

  public String getExtraInfo() {
    return myExtraInfo;
  }

  public void setExtraInfo( String aExtraInfo ) {
    myExtraInfo = aExtraInfo;
  }
}
