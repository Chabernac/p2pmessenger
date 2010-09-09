/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import chabernac.command.Command;

public class CommandAction extends AbstractAction {
  private static final long serialVersionUID = 4564182788232730364L;
  private final Command myCommand;
  

  public CommandAction ( Command anCommand ) {
    super();
    myCommand = anCommand;
  }


  @Override
  public void actionPerformed( ActionEvent anE ) {
    myCommand.execute();
  }

}
