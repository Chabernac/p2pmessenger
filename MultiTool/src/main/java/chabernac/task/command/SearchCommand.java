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
  private final SearchTaskProvider mySearchTaskProvider = new SearchTaskProvider( getSelectedTask());
  private final SearchDialog mySearchDialog  = new SearchDialog( (JFrame)ApplicationRefBase.getObject( ApplicationRefBase.MAINFRAME ), mySearchTaskProvider, false);
  
  public SearchCommand(){
  }
  

  @Override
  protected void executeCommand() {
    mySearchTaskProvider.setRootTask( getRootTask() );
    mySearchDialog.setVisible( true );
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
