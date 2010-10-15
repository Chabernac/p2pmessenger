/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

import chabernac.p2pclient.gui.ChatMediator;
import chabernac.protocol.facade.P2PFacadeException;

public class SearchPeersMenuItem extends MenuItem implements ActionListener {
  private static final Logger LOGGER = Logger.getLogger( SearchPeersMenuItem.class );
  private final ChatMediator myMediator;

  public SearchPeersMenuItem ( ChatMediator anMediator ) {
    super("Zoeken naar peers");
    myMediator = anMediator;
    addActionListener( this );
  }

  @Override
  public void actionPerformed( ActionEvent anE ) {
    try {
      myMediator.getP2PFacade().scanSuperNodes();
    } catch ( P2PFacadeException e ) {
      LOGGER.error("Could not scan super nodes");
    }
  }
}
