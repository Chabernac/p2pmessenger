/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Menu;
import java.io.IOException;

import chabernac.p2pclient.gui.ChatMediator;

public class ReceiveMenu extends Menu {
  private static final long serialVersionUID = 1489728033427618807L;

  public ReceiveMenu(ChatMediator aMediator) throws IOException{
    super("Ontvangen");
    
    add(new ReceiveClosedMenuItem(aMediator));
    add(new ReceiveAsMessageIndicatesMenuItem(aMediator));
    add(new NoPopupMenuItem(aMediator));
    add(new InfoPanelMenuItem( aMediator ));
  }
}
