/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;

import be.axa.fi.io.ClassPathResource;

import chabernac.ldapuserinfoprovider.AXALDAPUserInfoProvider;
import chabernac.p2pclient.gui.UserPanel;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;

public class ApplicationLauncher {

  /**
   * @param args
   * @throws P2PFacadeException 
   */
  public static void main( String[] args ) throws P2PFacadeException {
    BasicConfigurator.configure();
    
    P2PFacade theFacade = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( new AXALDAPUserInfoProvider() )
    .setSuperNodesDataSource( new ClassPathResource("supernodes.txt") )
    .start( 5 );
    
    JFrame theFrame = new JFrame();
    theFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    theFrame.getContentPane().setLayout( new BorderLayout() );
    theFrame.getContentPane().add( new UserPanel(theFacade) );
    
    theFrame.pack();
    theFrame.setVisible( true );
  }

}
