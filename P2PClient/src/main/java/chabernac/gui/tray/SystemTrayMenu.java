/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import chabernac.gui.ApplicationLauncher;
import chabernac.io.ClassPathResource;
import chabernac.p2pclient.gui.ChatFrame;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;

public class SystemTrayMenu extends PopupMenu implements ActionListener {
  private static final long serialVersionUID = 3911652810490182171L;
  private final ChatFrame myChatFrame;
  private Logger LOGGER = Logger.getLogger( SystemTrayMenu.class );

  private SystemTrayMenu(ChatFrame aChatFrame){
    myChatFrame = aChatFrame;
    addActionListener( this );
  }

  private void build ( P2PFacade aFacade, TrayIcon anIcon ) throws HeadlessException, IOException, P2PFacadeException {

    add( new OpenMenuItem() );
    add( new OnTopMenuItem(myChatFrame) );

//    add(new SendMenu());
    add(new ReceiveMenu(myChatFrame.getMediator()));
    add(new StatusMenu(myChatFrame.getMediator()));
    add(new SearchPeersMenuItem(myChatFrame.getMediator()));
    add( new ExitMenuItem(myChatFrame.getMediator()) );

  }

  public void actionPerformed(ActionEvent evt){
    if(myChatFrame == null || !myChatFrame.isVisible() || myChatFrame.getState() == Frame.ICONIFIED){
      try {
        ApplicationLauncher.showChatFrame();
      } catch ( P2PFacadeException e ) {
        LOGGER.error("Unable to show chat frame");
      }
    } else {
      myChatFrame.setVisible( false );
    }
  }

  public static void buildSystemTray(ChatFrame aChatFrame, P2PFacade aFacade) throws IOException, AWTException, HeadlessException, P2PFacadeException{
    if(SystemTray.isSupported()){
      SystemTray theTray = SystemTray.getSystemTray();
      SystemTrayMenu theMenu = new SystemTrayMenu(aChatFrame);
      TrayIcon theIcon = new TrayIcon(ImageIO.read( new ClassPathResource("images/message.png").getInputStream()), "P2PClient", theMenu);
      theTray.add( theIcon );
      theMenu.build( aFacade, theIcon);
    }
  }
}
