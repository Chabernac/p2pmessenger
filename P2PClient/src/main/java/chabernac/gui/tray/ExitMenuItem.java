/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.MenuItem;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import chabernac.events.EventDispatcher;
import chabernac.gui.event.SavePreferencesEvent;
import chabernac.p2pclient.gui.ChatFrame;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.facade.P2PFacade;

public class ExitMenuItem extends MenuItem implements ActionListener {
  private static Logger LOGGER = Logger.getLogger( OpenMenuItem.class );
  private static final long serialVersionUID = 194782738909400622L;
  private final TrayIcon myTrayIcon; 
  private final ChatFrame myChatFrame;
  private final P2PFacade myFacade;
  
  public ExitMenuItem(TrayIcon aTrayIcon, ChatFrame aChatFrame, P2PFacade aFacade){
    super("Exit");
    addActionListener(this);
    myTrayIcon = aTrayIcon; 
    myChatFrame = aChatFrame;
    myFacade = aFacade;
  }

  @Override
  public void actionPerformed( ActionEvent anE ) {
    if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog( myChatFrame, "Ben je zeker dat je wilt afsluiten? Als je afsluit kan je geen berichten meer ontvangen.", "Afsluiten", JOptionPane.OK_CANCEL_OPTION)){
      try{
        myTrayIcon.setToolTip( "P2PClient: Bezig met afsluiten" );
        if(myChatFrame != null) myChatFrame.setVisible( false );
        EventDispatcher.getInstance( SavePreferencesEvent.class ).fireEvent( new SavePreferencesEvent() );
        ApplicationPreferences.getInstance().save();
        myFacade.stop();
      }catch(Throwable e){
        LOGGER.error("Could not properly exit", e);
      } finally {
        System.exit(0);
      }
    }
    
  }
}
