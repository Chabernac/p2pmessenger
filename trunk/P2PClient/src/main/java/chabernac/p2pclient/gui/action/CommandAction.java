/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import chabernac.command.CommandSession;
import chabernac.p2pclient.gui.action.ActionFactory.Action;

public class CommandAction extends AbstractAction {
  private static final long serialVersionUID = 4564182788232730364L;
  private final Action myAction;
  private final ActionFactory myActionFactory;
  

  public CommandAction ( ActionFactory aFactory, Action anAction) {
    super();
    myAction = anAction;
    myActionFactory = aFactory;
  }


  @Override
  public void actionPerformed( ActionEvent anE ) {
    CommandSession.getInstance().execute( myActionFactory.getCommand( myAction ) );
  }

}
