/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.util.List;

import chabernac.protocol.message.MultiPeerMessage;

public interface iUserSelectionProvider {
  public List< String > getSelectedUsers();
  public void setSelectedUsers(List<String> aUserList);
  public void clear();
  public void setMultiPeerMessage(MultiPeerMessage aMessage);
  public void addSelectionChangedListener(iSelectionChangedListener aListener);
}
