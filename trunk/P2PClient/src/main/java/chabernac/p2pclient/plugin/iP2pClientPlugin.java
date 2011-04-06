/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.plugin;

import chabernac.p2pclient.gui.ChatMediator;

public interface iP2pClientPlugin {
  public void init(ChatMediator aFacade);
  public void remove(ChatMediator aFacade);
}
