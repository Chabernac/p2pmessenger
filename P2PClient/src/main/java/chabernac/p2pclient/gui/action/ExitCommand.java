/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui.action;

import java.awt.SystemTray;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import chabernac.command.Command;
import chabernac.events.EventDispatcher;
import chabernac.gui.event.SavePreferencesEvent;
import chabernac.p2pclient.gui.ChatMediator;
import chabernac.preference.ApplicationPreferences;

public class ExitCommand implements Command {
  private static Logger LOGGER = Logger.getLogger( ExitCommand.class );
  private final ChatMediator myMediator;

  public ExitCommand ( ChatMediator aMediator ) {
    myMediator = aMediator;
  }

  @Override
  public void execute() {
    JFrame myFrame = null;
    if(myMediator.getTitleProvider() instanceof JFrame){
      myFrame = ((JFrame)myMediator.getTitleProvider());
    }
    
    if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog( myFrame, "Ben je zeker dat je wilt afsluiten? Als je afsluit kan je geen berichten meer ontvangen.", "Afsluiten", JOptionPane.OK_CANCEL_OPTION)){
      try{
        SystemTray.getSystemTray().getTrayIcons()[0].setToolTip( "P2PClient: Bezig met afsluiten" );
        
        myFrame.setVisible( false );
        
        EventDispatcher.getInstance( SavePreferencesEvent.class ).fireEvent( new SavePreferencesEvent() );
        ApplicationPreferences.getInstance().save();
        
        myMediator.getP2PFacade().stop();
        
        //give some time to end sockets
        Thread.sleep( 3000 );
      }catch(Throwable e){
        LOGGER.error("Could not properly exit", e);
      } finally {
        System.exit(0);
      }
    }
  }

}
