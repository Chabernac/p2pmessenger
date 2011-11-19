/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.testingutils;

import java.util.ArrayList;
import java.util.List;

import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iMultiPeerMessageListener;

public class MessageCollector implements iMultiPeerMessageListener {
  private List< MultiPeerMessage > myMultiPeerMessages = new ArrayList< MultiPeerMessage >();

  @Override
  public void messageReceived( MultiPeerMessage aMessage ) {
    myMultiPeerMessages.add(aMessage);
  }
  
  public List<MultiPeerMessage> getMessages(){
    return myMultiPeerMessages;
  }

}
