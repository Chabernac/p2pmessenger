/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.util.ArrayList;
import java.util.List;

public class DummyUserSelectionProvider implements iUserSelectionProvider {
  private List<String> mySelectedUsers = new ArrayList< String >();

  @Override
  public List< String > getSelectedUsers() {
    return mySelectedUsers;
  }

  @Override
  public void setSelectedUsers( List< String > aUserList ) {
    mySelectedUsers = aUserList;
  }

}
