/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

public class MessageAndResponse {
  private final Message myMessage;
  private String myResponse = null;
  private long myCreationTime = -1;
  private long myResponseTime = -1;
  
  public MessageAndResponse ( Message aMessage ) {
    super();
    myMessage = aMessage;
    myCreationTime = System.currentTimeMillis();
  }
  public String getResponse() {
    return myResponse;
  }
  public void setResponse( String aResponse ) {
    myResponse = aResponse;
    myResponseTime = System.currentTimeMillis();
  }
  public Message getMessage() {
    return myMessage;
  }
  
  public long getResponseTime(){
    return myResponseTime - myCreationTime;
  }
}
