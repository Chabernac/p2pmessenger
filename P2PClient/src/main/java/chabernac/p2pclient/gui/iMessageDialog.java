/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import chabernac.protocol.message.MultiPeerMessage;

public interface iMessageDialog {
  public void showMessage(MultiPeerMessage aMessage);
}
