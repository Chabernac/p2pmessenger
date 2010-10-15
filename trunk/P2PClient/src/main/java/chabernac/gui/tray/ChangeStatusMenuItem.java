/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import chabernac.p2pclient.gui.ChatMediator;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.userinfo.UserInfo.Status;

public class ChangeStatusMenuItem extends MenuItem implements ActionListener {
  private static final long serialVersionUID = 3790451282427644415L;

  private static final Logger LOGGER = Logger.getLogger(ChangeStatusMenuItem.class);
  
  private final ChatMediator myMediator;
  private final Status myStatus;

  public ChangeStatusMenuItem ( ChatMediator aMediator, Status aStatus) throws P2PFacadeException {
    super(ResourceBundle.getBundle( "resources", Locale.getDefault() ).getString( aStatus.name() ));
    myMediator = aMediator;
    myStatus = aStatus;
    myMediator.getP2PFacade().getPersonalInfo().addObserver( new UserInfoObserver() );
    addActionListener( this );
    setBold();
  }

  @Override
  public void actionPerformed( ActionEvent anE ) {
    try {
      myMediator.getP2PFacade().getPersonalInfo().setStatus( myStatus );
    } catch ( P2PFacadeException e ) {
      LOGGER.error("Could not change status", e);
    }
  }
  
  private void setBold(){
    try {
      setFont( new Font("Arial", myMediator.getP2PFacade().getPersonalInfo().getStatus() == myStatus ? Font.BOLD : Font.PLAIN, 12 ) );
    } catch ( P2PFacadeException e ) {
      LOGGER.error("Unable to change font", e);
    }
  }

  public class UserInfoObserver implements Observer {
    @Override
    public void update( Observable anO, Object anArg ) {
      setBold();
    }
  }
}
