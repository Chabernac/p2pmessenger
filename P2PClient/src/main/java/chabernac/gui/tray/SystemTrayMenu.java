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

import javax.imageio.ImageIO;

import chabernac.io.ClassPathResource;
import chabernac.p2pclient.gui.ChatFrame;
import chabernac.p2pclient.gui.action.CommandActionListener;
import chabernac.p2pclient.gui.action.ActionFactory.Action;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;

public class SystemTrayMenu extends PopupMenu {
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
    add(new SearchPeersMenuItem(myChatFrame.getMediator()));
    add( new ExitMenuItem(myChatFrame.getMediator()) );

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
