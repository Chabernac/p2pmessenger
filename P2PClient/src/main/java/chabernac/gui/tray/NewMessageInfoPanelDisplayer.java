/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.io.IOException;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import chabernac.gui.ApplicationLauncher;
import chabernac.p2pclient.gui.ChatMediator;
import chabernac.p2pclient.settings.Settings;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iMultiPeerMessageListener;

public class NewMessageInfoPanelDisplayer {
  private static final Logger LOGGER = Logger.getLogger(NewMessageInfoPanelDisplayer.class);
  
  private final ChatMediator myMediator;
  
  private InfoPanel myInfoPanel = null;

  public NewMessageInfoPanelDisplayer ( ChatMediator anMediator ) throws P2PFacadeException, IOException {
    super();
    myMediator = anMediator;
    myMediator.getP2PFacade().addMessageListener( new MyMessageListener() );
    myInfoPanel = new InfoPanel();
    myInfoPanel.setDisplayTime( 5 );
    myInfoPanel.setSize( 100, 20 );
    myInfoPanel.setCommandOnClick( new Runnable(){
      public void run(){
        try {
          ApplicationLauncher.showChatFrame();
        } catch ( P2PFacadeException e ) {
          LOGGER.error("Could not show chat frame", e);
        } 
      }
    });

  }

  public class MyMessageListener implements iMultiPeerMessageListener {

    @Override
    public void messageReceived( MultiPeerMessage aMessage ) {
      if(ApplicationPreferences.getInstance().hasEnumProperty( Settings.ReceiveEnveloppe.INFO_PANEL ) && !((JFrame)myMediator.getTitleProvider()).hasFocus()){
        myInfoPanel.setText( "Nieuw bericht" );
      }
    }
  }
}
