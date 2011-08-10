/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Menu;
import java.io.IOException;

import chabernac.p2pclient.gui.ChatMediator;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.userinfo.UserInfo.Status;

public class StatusMenu extends Menu {
  private static final long serialVersionUID = -9010911314375885233L;

  public StatusMenu(ChatMediator aMediator) throws IOException, P2PFacadeException{
    super("Status");
    
    add(new ChangeStatusMenuItem(aMediator, Status.ONLINE));
    add(new ChangeStatusMenuItem(aMediator, Status.AWAY));
    add(new ChangeStatusMenuItem(aMediator, Status.BUSY));
    add(new ChangeStatusMenuItem(aMediator, Status.OFFLINE));
  }
}
