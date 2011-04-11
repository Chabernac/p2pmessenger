/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.reactorplugin;

import java.util.concurrent.TimeUnit;

public class ReactorSettings {
  private long myTimeout;
  private TimeUnit myTimeUnit;
  private int myQuestionsPerRound;
  private boolean isRandomTimeout;
  
  public long getTimeout() {
    return myTimeout;
  }
  public void setTimeout( long aTimeout ) {
    myTimeout = aTimeout;
  }
  public TimeUnit getTimeUnit() {
    return myTimeUnit;
  }
  public void setTimeUnit( TimeUnit aTimeUnit ) {
    myTimeUnit = aTimeUnit;
  }
  public int getQuestionsPerRound() {
    return myQuestionsPerRound;
  }
  public void setQuestionsPerRound( int aQuestionsPerRound ) {
    myQuestionsPerRound = aQuestionsPerRound;
  }
  public boolean isRandomTimeout() {
    return isRandomTimeout;
  }
  public void setRandomTimeout( boolean aRandomTimeout ) {
    isRandomTimeout = aRandomTimeout;
  }
  
  
}
