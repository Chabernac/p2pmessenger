/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

public class SocketSenderReply {
  private final String myReply;
  private final String myRemoteId;
  
  public SocketSenderReply( String aReply, String aRemoteId ) {
    super();
    myReply = aReply;
    myRemoteId = aRemoteId;
  }
  public String getReply() {
    return myReply;
  }
  public String getRemoteId() {
    return myRemoteId;
  }
} 
