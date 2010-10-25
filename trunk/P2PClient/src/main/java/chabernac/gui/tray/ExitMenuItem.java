/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.MenuItem;

import chabernac.p2pclient.gui.ChatMediator;
import chabernac.p2pclient.gui.action.ActionFactory;
import chabernac.p2pclient.gui.action.CommandActionListener;

public class ExitMenuItem extends MenuItem {
  private static final long serialVersionUID = 194782738909400622L;
  
  public ExitMenuItem(ChatMediator aMediator){
    super("Exit");
    addActionListener( new CommandActionListener(aMediator.getActionFactory(), ActionFactory.Action.EXIT));
  }
}
