/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

public class DummyMessageProvider implements iMessageProvider {
  private String myMessage = "";

  @Override
  public void clear() {
   myMessage = ""; 
  }

  @Override
  public String getMessage() {
    return myMessage;
  }

  @Override
  public void setMessage( String aMessage ) {
    myMessage = aMessage;
  }

}
