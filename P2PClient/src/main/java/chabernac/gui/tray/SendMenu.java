/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Menu;

import chabernac.p2pclient.gui.ChatFrame;

public class SendMenu extends Menu {
  private static final long serialVersionUID = 1489728033427618807L;

  public SendMenu(ChatFrame aFrame){
    super("Verzenden");
    
    add(new SendClosedMenuItem(aFrame));
    add(new SendOpenMenuItem(aFrame));
  }
}
