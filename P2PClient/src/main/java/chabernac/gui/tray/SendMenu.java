/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Menu;

public class SendMenu extends Menu {
  private static final long serialVersionUID = 1489728033427618807L;

  public SendMenu(){
    super("Verzenden");
    SendClosedMenuItem theSendCloseMenuItem = new SendClosedMenuItem();
    SendOpenMenuItem theSendOpenMenuItem = new SendOpenMenuItem();
    add(theSendCloseMenuItem);
    add(theSendOpenMenuItem);
    theSendCloseMenuItem.addActionListener(theSendOpenMenuItem);
    theSendOpenMenuItem.addActionListener(theSendCloseMenuItem);
  }
}
