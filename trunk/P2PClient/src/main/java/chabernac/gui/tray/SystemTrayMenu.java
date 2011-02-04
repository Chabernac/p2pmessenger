/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import chabernac.io.ClassPathResource;
import chabernac.p2pclient.gui.ChatFrame;
import chabernac.p2pclient.gui.action.CommandActionListener;
import chabernac.p2pclient.gui.action.ActionFactory.Action;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;

public class SystemTrayMenu extends PopupMenu {
  private static final Logger LOGGER = Logger.getLogger(SystemTray.class);
  private static final long serialVersionUID = 3911652810490182171L;
  private final ChatFrame myChatFrame;

  private SystemTrayMenu(ChatFrame aChatFrame){
    myChatFrame = aChatFrame;
  }

  private void build ( P2PFacade aFacade, TrayIcon anIcon ) throws HeadlessException, IOException, P2PFacadeException {
    anIcon.addActionListener(new CommandActionListener(myChatFrame.getMediator().getActionFactory(), Action.TOGGLE_SHOW_FRAME));

    add( new OpenMenuItem(myChatFrame.getMediator()) );
    add( new OnTopMenuItem(myChatFrame) );

    //    add(new SendMenu());
    add(new ReceiveMenu(myChatFrame.getMediator()));
    add(new StatusMenu(myChatFrame.getMediator()));
//    add(new SearchPeersMenuItem(myChatFrame.getMediator()));
    add( new ExitMenuItem(myChatFrame.getMediator()) );

  }

  public static void buildSystemTray(ChatFrame aChatFrame, P2PFacade aFacade) throws IOException, AWTException, HeadlessException, P2PFacadeException{
    if(SystemTray.isSupported()){
      SystemTray theTray = SystemTray.getSystemTray();
      SystemTrayMenu theMenu = new SystemTrayMenu(aChatFrame);
      TrayIcon theIcon = new TrayIcon(ImageIO.read( new ClassPathResource("images/message.png").getInputStream()), "P2PClient", theMenu);
      theTray.add( theIcon );
      theMenu.build( aFacade, theIcon);
      Executors.newScheduledThreadPool( 1 ).scheduleAtFixedRate( new TrayIconPersister( theIcon ), 10, 10, TimeUnit.MINUTES);
    }
  }
  
  public static boolean refreshIcon(){
    if(SystemTray.isSupported()){
      SystemTray theTray = SystemTray.getSystemTray();
      if(theTray.getTrayIcons().length > 0){
        TrayIcon theIcon = theTray.getTrayIcons()[0];
        theTray.remove( theIcon );
        try {
          theTray.add( theIcon );
          return true;
        } catch ( AWTException e ) {
          return false;
        }
      }
    }
    return false;
  }

  private static class TrayIconPersister implements Runnable{
    private final TrayIcon myIcon;


    public TrayIconPersister( TrayIcon aIcon ) {
      super();
      myIcon = aIcon;
    }


    public void run(){

      //very ugly way of forcing the system to redisplay the tray icon after an explorer crash
      //currently seems to cause the application to crash after a certain period
      //      try {
      //        Field theField = TrayIcon.class.getDeclaredField( "peer" );
      //        theField.setAccessible( true );
      //        theField.set( myIcon, null );
      //        
      //        Method theMethod = TrayIcon.class.getDeclaredMethod( "addNotify", new Class[]{} );
      //        theMethod.setAccessible( true );
      //        theMethod.invoke( myIcon, new Object[]{} );
      //      } catch ( Throwable e ) {
      //        LOGGER.error( "Could not restore system tray icon", e );
      //      }

      //also not the best way since the task tray flashes when reputting thte icon this way.
      refreshIcon();
    }
  }
}
