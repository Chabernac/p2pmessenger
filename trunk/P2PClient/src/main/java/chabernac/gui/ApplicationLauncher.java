/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.gui.tray.NewMessageTrayIconDisplayer;
import chabernac.gui.tray.SystemTrayMenu;
import chabernac.io.ClassPathResource;
import chabernac.io.SocketProxy;
import chabernac.ldapuserinfoprovider.AXALDAPUserInfoProvider;
import chabernac.ldapuserinfoprovider.BackupUserInfoProviderDecorator;
import chabernac.lock.FileLock;
import chabernac.lock.iLock;
import chabernac.p2pclient.gui.ChatFrame;
import chabernac.p2pclient.settings.Settings.ReceiveEnveloppe;
import chabernac.p2pclient.settings.Settings.SendEnveloppe;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.iProtocolDelegate;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.filetransfer.FileHandlerDialogDispatcher;
import chabernac.protocol.pominfoexchange.POMInfo;
import chabernac.protocol.routing.PeerSender;
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
   * @throws AWTException 
   * @throws IOException 
   */
  public static void main( String[] args ) throws P2PFacadeException, UserInfoException, IOException, AWTException {
    ArgsInterPreter theInterPretser = new ArgsInterPreter(args);

    initProxy(theInterPretser);
    
    initLocale(theInterPretser);
    
    initDefaultSettigns();

    if("true".equals(theInterPretser.getKeyValue("checklock", "true"))){
      if(!checkLockAndActivate()) return;
    }

    BasicConfigurator.configure();

    addRun2Startup();

    SocketProxy.setTraceEnabled( true );

    startTimers();

    startFacade(theInterPretser);

    addActivationListener();

    myChatFrame = new ChatFrame(myFacade);
    if("true".equals(theInterPretser.getKeyValue( "visible" ))){
      showChatFrame();
    }

    SystemTrayMenu.buildSystemTray( myChatFrame, myFacade );
    
    new NewMessageTrayIconDisplayer(myChatFrame.getMediator());
  }

  private static void initDefaultSettigns() {
    ApplicationPreferences thePrefs = ApplicationPreferences.getInstance();
    if(!thePrefs.hasEnumType(ReceiveEnveloppe.class)) thePrefs.setEnumProperty(ReceiveEnveloppe.AS_MESSAGE_INDICATES);
    if(!thePrefs.hasEnumType(SendEnveloppe.class)) thePrefs.setEnumProperty(SendEnveloppe.OPEN);
  }

  private static void initProxy(ArgsInterPreter anInterpreter){
    if(anInterpreter.containsKey( "http.proxyHost" ) && anInterpreter.containsKey( "http.proxyPort" )){
      System.getProperties().put("http.proxyHost", anInterpreter.getKeyValue( "http.proxyHost" ));
      System.getProperties().put("http.proxyPort", anInterpreter.getKeyValue( "http.proxyPort" ));
    }
  }
  
  private static void initLocale(ArgsInterPreter anInterpreter){
    Locale.setDefault( new Locale(anInterpreter.getKeyValue( "locale", "nl" )));
  }

  private static void addActivationListener() throws P2PFacadeException{
    myFacade.setApplicationProtocolDelegate( new ActivationProtocolDelegate() );
  }

  public static synchronized void showChatFrame() throws P2PFacadeException{
    if(myChatFrame == null){
      myChatFrame = new ChatFrame(myFacade);
    }
    myChatFrame.showFrame();
  }

  private static void startFacade(ArgsInterPreter anInterPreter) throws P2PFacadeException, IOException{
    iUserInfoProvider theUserInfoProvider = new BackupUserInfoProviderDecorator(new AXALDAPUserInfoProvider());

    myFacade = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( "true".equals(anInterPreter.getKeyValue( "persist" )) )
    .setUserInfoProvider( theUserInfoProvider )
    .setSuperNodesDataSource( new ClassPathResource("supernodes.txt") )
    .setStopWhenAlreadyRunning( true )
    .setChannel(anInterPreter.getKeyValue("channel", "default"))
    .setFileHandler( new FileHandlerDialogDispatcher() )
    .setInfoObject( "pom.info", new POMInfo() )
    .setInfoObject( "version", "v2011.01.12" )
    .setSocketReuse( false )
    .start( 10 );
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
      RoutingProtocol theRoutingProtocol = new RoutingProtocol(null, -1, true, null, false, "AXA", new PeerSender());
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
          SystemTrayMenu.refreshIcon();
          return "ok";
        } catch ( P2PFacadeException e ) {
          LOGGER.error( "Coul not activate", e );
        }
      }
      return "nok";
    }
  }
}
