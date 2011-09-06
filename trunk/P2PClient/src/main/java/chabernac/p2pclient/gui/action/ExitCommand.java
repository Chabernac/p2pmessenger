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
  private final boolean isCloseDirectly;

  public ExitCommand ( ChatMediator aMediator, boolean isCloseDirectly ) {
    myMediator = aMediator;
    this.isCloseDirectly = isCloseDirectly;
  }

  @Override
  public void execute() {
    JFrame myFrame = null;
    if(myMediator.getTitleProvider() instanceof JFrame){
      myFrame = ((JFrame)myMediator.getTitleProvider());
    }
    
    LOGGER.error( "Showing confirm dialog "  + isCloseDirectly );
    
    if(isCloseDirectly || JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog( myFrame, "Ben je zeker dat je wilt afsluiten? Als je afsluit kan je geen berichten meer ontvangen.", "Afsluiten", JOptionPane.OK_CANCEL_OPTION)){
      try{
        LOGGER.error( "Changing tray icon text" );
        SystemTray.getSystemTray().getTrayIcons()[0].setToolTip( "P2PClient: Bezig met afsluiten" );
        
        myFrame.setVisible( false );
        
        EventDispatcher.getInstance( SavePreferencesEvent.class ).fireEvent( new SavePreferencesEvent() );
        LOGGER.error( "Saving" );
        ApplicationPreferences.getInstance().save();
        LOGGER.error( "Stopping facade" );
        myMediator.getP2PFacade().stop();
        
        //give some time to end sockets
        Thread.sleep( 1000 );
      }catch(Throwable e){
        LOGGER.error("Could not properly exit", e);
      } finally {
        LOGGER.error( "Exiting" );
        System.exit(0);
      }
    }
  }

}
