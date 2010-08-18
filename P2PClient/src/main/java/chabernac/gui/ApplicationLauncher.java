/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.io.ClassPathResource;
import chabernac.io.SocketProxy;
import chabernac.ldapuserinfoprovider.AXALDAPUserInfoProvider;
import chabernac.ldapuserinfoprovider.BackupUserInfoProviderDecorator;
import chabernac.lock.FileLock;
import chabernac.lock.iLock;
import chabernac.p2pclient.gui.ChatFrame;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.iProtocolDelegate;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.userinfo.UserInfoException;
import chabernac.protocol.userinfo.iUserInfoProvider;
import chabernac.utils.ArgsInterPreter;
import chabernac.utils.ServiceTools;

public class ApplicationLauncher {
  private static Logger LOGGER = Logger.getLogger(ApplicationLauncher.class);
  private static ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool( 1 );
  private static ChatFrame myChatFrame = null;
  private static P2PFacade myFacade = null;

  /**
   * @param args
   * @throws P2PFacadeException 
   * @throws UserInfoException 
   */
  public static void main( String[] args ) throws P2PFacadeException, UserInfoException {
    ArgsInterPreter theInterPretser = new ArgsInterPreter(args);
    
    if(!checkLockAndActivate()) return;

    BasicConfigurator.configure();

    addRun2Startup();

    SocketProxy.setTraceEnabled( true );

    startTimers();

    startFacade(theInterPretser);
    
    addActivationListener();
    
    if("true".equals(theInterPretser.getKeyValue( "visible" ))){
      showChatFrame();
    }
  }
  
  private static void addActivationListener() throws P2PFacadeException{
    myFacade.setApplicationProtocolDelegate( new ActivationProtocolDelegate() );
  }
  
  public static synchronized void showChatFrame() throws P2PFacadeException{
    if(myChatFrame == null){
      myChatFrame = new ChatFrame(myFacade);
    }
    myChatFrame.setVisible( true );
  }
  
  private static void startFacade(ArgsInterPreter anInterPreter) throws P2PFacadeException{
    iUserInfoProvider theUserInfoProvider = new BackupUserInfoProviderDecorator(new AXALDAPUserInfoProvider());

    myFacade = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( "true".equals(anInterPreter.getKeyValue( "persist" )) )
    .setUserInfoProvider( theUserInfoProvider )
    .setSuperNodesDataSource( new ClassPathResource("supernodes.txt") )
    .setStopWhenAlreadyRunning( true )
    .start( 20 );
  }

  private static void addRun2Startup(){
    try {
      ServiceTools.addRun2Startup(new File("p2pclient.cmd"));
    } catch (IOException e) {
      LOGGER.error("Could not add p2pclient to startup", e);
    }
  }

  private static boolean checkLockAndActivate(){
    iLock theLock = new FileLock("p2pclient");
    if(theLock.isLocked()){
      activate();
      return false;
    } else {
      theLock.createLock();
    }
    return true;
  }

  private static boolean activate(){
    try {
      RoutingProtocol theRoutingProtocol = new RoutingProtocol(null, -1, true, null, false);
      RoutingTableEntry theLocalEntry = theRoutingProtocol.getRoutingTable().getEntryForLocalPeer();
      return "ok".equals( theLocalEntry.getPeer().send( "APPactivate" ));
    } catch ( Exception e ) {
      LOGGER.error("Could not activate", e);
      return false;
    }
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
  
  private static class ActivationProtocolDelegate implements iProtocolDelegate {
    @Override
    public String handleCommand( long aSessionId, String anInput ) {
      if("activate".equalsIgnoreCase(  anInput )){
        try {
          showChatFrame();
          return "ok";
        } catch ( P2PFacadeException e ) {
          LOGGER.error( "Coul not activate", e );
        }
      }
      return "nok";
    }

  }
}
