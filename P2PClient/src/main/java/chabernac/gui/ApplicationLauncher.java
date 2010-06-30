/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui;

import org.apache.log4j.BasicConfigurator;

import chabernac.io.ClassPathResource;
import chabernac.ldapuserinfoprovider.AXALDAPUserInfoProvider;
import chabernac.ldapuserinfoprovider.BackupUserInfoProviderDecorator;
import chabernac.p2pclient.gui.ChatFrame;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.userinfo.UserInfoException;
import chabernac.protocol.userinfo.iUserInfoProvider;
import chabernac.protocol.userinfo.UserInfo.Status;

public class ApplicationLauncher {

  /**
   * @param args
   * @throws P2PFacadeException 
   * @throws UserInfoException 
   */
  public static void main( String[] args ) throws P2PFacadeException, UserInfoException {
    BasicConfigurator.configure();
    
    iUserInfoProvider theUserInfoProvider = new BackupUserInfoProviderDecorator(new AXALDAPUserInfoProvider());
    
    P2PFacade theFacade = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( theUserInfoProvider )
    .setSuperNodesDataSource( new ClassPathResource("supernodes.txt") )
    .start( 5 );
    
    ChatFrame theFrame = new ChatFrame(theFacade);
    theUserInfoProvider.getUserInfo().setStatus( Status.ONLINE );
    theFrame.setVisible( true );
  }

}
