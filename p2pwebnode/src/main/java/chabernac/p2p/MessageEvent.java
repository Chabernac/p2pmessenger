/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p;

import chabernac.protocol.message.Message;
import chabernac.protocol.message.MultiPeerMessage;

public class MessageEvent extends P2PEvent {
  private final Message myMessage;
  
  public MessageEvent ( Message anMessage ) {
    super();
    myMessage = anMessage;
  }

  @Override
  public void handle( iP2PEventHandler aHandler ) {
    aHandler.handleEvent( this );
  }

  public Message getMessage() {
    return myMessage;
  }
}
