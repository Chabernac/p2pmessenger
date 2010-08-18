/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.event;

import chabernac.events.Event;
import chabernac.protocol.message.MultiPeerMessage;

public class MultiPeerMessageEvent extends Event {
  private static final long serialVersionUID = 5993892444405185869L;
  private final MultiPeerMessage myMessage;

  public MultiPeerMessageEvent ( MultiPeerMessage anMessage ) {
    myMessage = anMessage;
  }

  public MultiPeerMessage getMessage() {
    return myMessage;
  }
}
