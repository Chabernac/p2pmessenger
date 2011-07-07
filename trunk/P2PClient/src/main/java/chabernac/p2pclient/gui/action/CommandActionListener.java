/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

import chabernac.command.CommandSession;
import chabernac.p2pclient.gui.action.ActionFactory.Action;

public class CommandActionListener implements ActionListener {
  private static Logger LOGGER = Logger.getLogger(CommandActionListener.class);
  private final Action myAction;
  private final ActionFactory myActionFactory;
  
  public CommandActionListener ( ActionFactory anActionFactory, Action anAction) {
    super();
    myAction = anAction;
    myActionFactory = anActionFactory;
  }

  @Override
  public void actionPerformed( ActionEvent anE ) {
    LOGGER.debug( "Executing command action for " + myAction);
    CommandSession.getInstance().execute( myActionFactory.getCommand( myAction ) );
  }

}
