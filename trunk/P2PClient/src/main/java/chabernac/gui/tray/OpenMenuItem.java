/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.MenuItem;

import chabernac.p2pclient.gui.ChatMediator;
import chabernac.p2pclient.gui.action.CommandActionListener;
import chabernac.p2pclient.gui.action.ActionFactory.Action;

public class OpenMenuItem extends MenuItem {
  private static final long serialVersionUID = 2952304314664542509L;
  
  public OpenMenuItem(ChatMediator aMediator){
    super("Open");
    addActionListener( new CommandActionListener(aMediator.getActionFactory(), Action.SHOW_FRAME));
  }
}