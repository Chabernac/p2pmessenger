/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui.action;

import chabernac.command.Command;
import chabernac.command.CommandSession;

public class RedoCommand implements Command {

  @Override
  public void execute() {
    CommandSession.getInstance().redoNumberOfSteps( 1 );
  }

}
