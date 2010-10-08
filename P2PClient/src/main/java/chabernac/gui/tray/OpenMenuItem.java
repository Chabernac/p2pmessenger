/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

import chabernac.gui.ApplicationLauncher;
import chabernac.protocol.facade.P2PFacadeException;

public class OpenMenuItem extends MenuItem {
  private static final long serialVersionUID = 2952304314664542509L;
  
  private static Logger LOGGER = Logger.getLogger( OpenMenuItem.class );

  public OpenMenuItem(){
    super("Open");
    addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        try {
          ApplicationLauncher.showChatFrame();
        } catch ( P2PFacadeException e ) {
          LOGGER.error("Unable to load chat frame", e);
        }
      }
    });
  }
}