/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

public class MessageAndResponse {
  private final Message myMessage;
  private String myResponse = null;
  
  public MessageAndResponse ( Message aMessage ) {
    super();
    myMessage = aMessage;
  }
  public String getResponse() {
    return myResponse;
  }
  public void setResponse( String aResponse ) {
    myResponse = aResponse;
  }
  public Message getMessage() {
    return myMessage;
  }
}
