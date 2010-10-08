/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.util.HashSet;
import java.util.Set;

import chabernac.protocol.message.MultiPeerMessage;

public class DummyUserSelectionProvider implements iUserSelectionProvider {
  private Set<String> mySelectedUsers = new HashSet< String >();

  @Override
  public Set< String > getSelectedUsers() {
    return mySelectedUsers;
  }

  @Override
  public void setSelectedUsers( Set< String > aUserList ) {
    mySelectedUsers = aUserList;
  }

  @Override
  public void clear() {
    mySelectedUsers.clear();
  }

  @Override
  public void addSelectionChangedListener( iSelectionChangedListener aListener ) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setMultiPeerMessage( MultiPeerMessage aMessage ) {
    // TODO Auto-generated method stub
    
  }

}
