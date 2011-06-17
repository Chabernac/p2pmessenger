/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.testingutils;

import java.util.concurrent.atomic.AtomicInteger;

import chabernac.protocol.message.Message;
import chabernac.protocol.message.iMessageListener;

public class MessageCounterListener implements iMessageListener{
  private AtomicInteger myCounter = new AtomicInteger();

  @Override
  public void messageReceived( Message aMessage ) {
    myCounter.incrementAndGet();
  }

  public int getCounter(){
    return myCounter.get();
  }

  @Override
  public void messageUpdated( Message aMessage ) {
    
  }
}