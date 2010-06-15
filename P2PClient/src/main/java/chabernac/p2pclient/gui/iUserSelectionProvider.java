/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.util.List;

public interface iUserSelectionProvider {
  public List< String > getSelectedUsers();
  public void setSelectedUsers(List<String> aUserList);
}
