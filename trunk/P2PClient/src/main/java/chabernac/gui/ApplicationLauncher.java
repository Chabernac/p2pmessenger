/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chabernac.io.ClassPathResource;
import chabernac.io.SocketProxy;
import chabernac.ldapuserinfoprovider.AXALDAPUserInfoProvider;
import chabernac.ldapuserinfoprovider.BackupUserInfoProviderDecorator;
import chabernac.p2pclient.gui.ChatFrame;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.userinfo.UserInfoException;
import chabernac.protocol.userinfo.iUserInfoProvider;

public class ApplicationLauncher {
  private static ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool( 1 );

  /**
   * @param args
   * @throws P2PFacadeException 
   * @throws UserInfoException 
   */
  public static void main( String[] args ) throws P2PFacadeException, UserInfoException {
    //TODO remove
    SocketProxy.setTraceEnabled( true );
    
    SocketProxy.setTraceEnabled(true);
//    BasicConfigurator.configure();
    
    startTimers();
    
    iUserInfoProvider theUserInfoProvider = new BackupUserInfoProviderDecorator(new AXALDAPUserInfoProvider());
    
    P2PFacade theFacade = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( Boolean.parseBoolean( args[0] ) )
    .setUserInfoProvider( theUserInfoProvider )
    .setSuperNodesDataSource( new ClassPathResource("supernodes.txt") )
    .setStopWhenAlreadyRunning( true )
    .start( 20 );
    
    ChatFrame theFrame = new ChatFrame(theFacade);
//    theUserInfoProvider.getUserInfo().setStatus( Status.ONLINE );
    theFrame.setVisible( true );
  }
  
  private static void startTimers(){
    SERVICE.scheduleAtFixedRate( new SavePreference(), 5, 10, TimeUnit.MINUTES );
  }
  
  private static class SavePreference implements Runnable {
    @Override
    public void run() {
      ApplicationPreferences.save();
    }
  }
}
