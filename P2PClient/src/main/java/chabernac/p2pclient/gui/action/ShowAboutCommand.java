/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui.action;

import javax.swing.JFrame;

import chabernac.command.Command;
import chabernac.gui.AboutDialog;
import chabernac.p2pclient.gui.AboutPanel;
import chabernac.p2pclient.gui.ChatMediator;

public class ShowAboutCommand implements Command {
  private final ChatMediator myMediator;
  
  public ShowAboutCommand(ChatMediator aMediator){
    myMediator = aMediator;
  }

  @Override
  public void execute() {
    if(myMediator.getChatFrame() instanceof JFrame){
      JFrame theParent = (JFrame)myMediator.getChatFrame();
      
      AboutDialog theDialog = new AboutDialog( theParent, "About", new AboutPanel());
      theDialog.setVisible( true );
    }
  }
}
