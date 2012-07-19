/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

public class SocketSenderReply {
  private final String myReply;
  private final String myRemoteId;
  
  //the local interface through which the message was send
  private final iCommunicationInterface myCommunicationInterface;
  
  public SocketSenderReply( String aReply, String aRemoteId, iCommunicationInterface aCommunicationInterface ) {
    super();
    myReply = aReply;
    myRemoteId = aRemoteId;
    myCommunicationInterface = aCommunicationInterface;
  }
  public String getReply() {
    return myReply;
  }
  
  public String getRemoteId() {
    return myRemoteId;
  }
  
  public iCommunicationInterface getCommunicationInterface() {
    return myCommunicationInterface;
  }
} 
