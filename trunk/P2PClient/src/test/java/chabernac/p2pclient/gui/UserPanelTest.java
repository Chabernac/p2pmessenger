/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import junit.framework.TestCase;
import chabernac.ldapuserinfoprovider.AXALDAPUserInfoProvider;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;

public class UserPanelTest extends TestCase {
  public void testUserPanel() throws P2PFacadeException, InterruptedException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( new AXALDAPUserInfoProvider() )
    .start( 5 );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( new AXALDAPUserInfoProvider() )
    .start( 5 );
    
    JFrame theFrame = new JFrame();
    theFrame.getContentPane().setLayout( new BorderLayout() );
    theFrame.getContentPane().add( new UserPanel(theFacade1) );
    
    Thread.sleep( 2000 );
    
    theFrame.pack();
    theFrame.setVisible( true );
    
    Thread.sleep( 600000 );
  }
  
}
