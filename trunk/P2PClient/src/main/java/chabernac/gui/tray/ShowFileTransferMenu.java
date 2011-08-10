/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import org.apache.log4j.Logger;

import chabernac.p2pclient.gui.ChatMediator;
import chabernac.protocol.facade.P2PFacadeException;

public class ShowFileTransferMenu extends MenuItem implements ActionListener{
  private static final long serialVersionUID = -9010911314375885233L;
  private static final Logger LOGGER = Logger.getLogger(ShowFileTransferMenu.class);
  private final ChatMediator myMediator;

  public ShowFileTransferMenu(ChatMediator aMediator) throws IOException, P2PFacadeException{
    super("Bestands overdracht");
    myMediator = aMediator;
    addActionListener( this );
  }

  @Override
  public void actionPerformed( ActionEvent aE ) {
    try {
      myMediator.getP2PFacade().showFileTransferOverView();
    } catch ( P2PFacadeException e ) {
      LOGGER.error("An error occured while showing file transfer", e);
    }
  }
}
