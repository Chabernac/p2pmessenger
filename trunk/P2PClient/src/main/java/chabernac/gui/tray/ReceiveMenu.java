/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Menu;
import java.awt.TrayIcon;
import java.io.IOException;

import chabernac.p2pclient.gui.ChatFrame;

public class ReceiveMenu extends Menu {
  private static final long serialVersionUID = 1489728033427618807L;

  public ReceiveMenu(ChatFrame aFrame, TrayIcon anIcon) throws IOException{
    super("Ontvangen");
    
    add(new ReceiveClosedMenuItem(anIcon));
    add(new ReceiveAsMessageIndicatesMenuItem(anIcon));
    add(new NoPopupMenuItem(anIcon));
  }
}
