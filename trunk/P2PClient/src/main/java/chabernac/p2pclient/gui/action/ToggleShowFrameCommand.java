/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui.action;

import java.awt.Frame;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import chabernac.command.Command;
import chabernac.gui.ApplicationLauncher;
import chabernac.p2pclient.gui.ChatMediator;
import chabernac.p2pclient.gui.iChatFrame;
import chabernac.protocol.facade.P2PFacadeException;

public class ToggleShowFrameCommand implements Command {
  private static final Logger LOGGER = Logger.getLogger(ToggleShowFrameCommand.class);
  private final ChatMediator myMediator;

  public ToggleShowFrameCommand( ChatMediator aMediator ) {
    myMediator = aMediator;
  }

  @Override
  public void execute() {
    iChatFrame theChatFrame = myMediator.getChatFrame();
    
    if(theChatFrame instanceof JFrame){
      JFrame theFrame = (JFrame)theChatFrame;

      if(theFrame == null || !theFrame.isVisible() || theFrame.getState() == Frame.ICONIFIED){
        try {
          ApplicationLauncher.showChatFrame();
        } catch ( P2PFacadeException e ) {
          LOGGER.error("Unable to show chat frame");
        }
      } else {
        theFrame.setVisible( false );
      } 
    }
  }

}
