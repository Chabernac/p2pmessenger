/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import chabernac.command.CommandSession;
import chabernac.p2pclient.gui.action.ActionFactory.Action;

public class CommandActionListener implements ActionListener {
  private final Action myAction;
  private final ActionFactory myActionFactory;
  
  public CommandActionListener ( ActionFactory anActionFactory, Action anAction) {
    super();
    myAction = anAction;
    myActionFactory = anActionFactory;
  }

  @Override
  public void actionPerformed( ActionEvent anE ) {
    CommandSession.getInstance().execute( myActionFactory.getCommand( myAction ) );
  }

}
