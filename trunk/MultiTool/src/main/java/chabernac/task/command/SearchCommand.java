/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.task.command;

import javax.swing.JFrame;

import chabernac.application.ApplicationRefBase;
import chabernac.search.SearchDialog;
import chabernac.task.utils.SearchTaskProvider;

public class SearchCommand extends ActivityCommand{

  @Override
  protected void executeCommand() {
    new SearchDialog( (JFrame)ApplicationRefBase.getObject( ApplicationRefBase.MAINFRAME ), new SearchTaskProvider( getSelectedTask()), false );
  }

  @Override
  public String getName() {
    return "Search";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

}
