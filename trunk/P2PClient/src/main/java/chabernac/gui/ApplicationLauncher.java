/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.gui.tray.NewMessageInfoPanelDisplayer;
import chabernac.gui.tray.NewMessageTrayIconDisplayer;
import chabernac.gui.tray.SystemTrayMenu;
import chabernac.io.SocketProxy;
import chabernac.ldapuserinfoprovider.AXALDAPUserInfoProvider;
import chabernac.ldapuserinfoprovider.BackupUserInfoProviderDecorator;
import chabernac.lock.FileLock;
import chabernac.lock.iLock;
import chabernac.p2pclient.gui.ChatFrame;
import chabernac.p2pclient.gui.ChatMediator;
import chabernac.p2pclient.plugin.iP2pClientPlugin;
import chabernac.p2pclient.settings.Settings.ReceiveEnveloppe;
import chabernac.p2pclient.settings.Settings.SendEnveloppe;
import chabernac.plugin.PluginActivator;
import chabernac.plugin.PluginRegistry;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.iProtocolDelegate;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.filetransfer.FileHandlerDialogDispatcher;
import chabernac.protocol.pominfoexchange.POMInfo;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.userinfo.UserInfoException;
import chabernac.protocol.userinfo.iUserInfoProvider;
import chabernac.tools.SaveMessagesToFile;
import chabernac.utils.ArgsInterPreter;
import chabernac.utils.ServiceTools;

public class ApplicationLauncher {
  private static Logger LOGGER = Logger.getLogger(ApplicationLauncher.class);
  private static ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool( 1 );
  private static ChatFrame myChatFrame = null;
  private static ChatMediator myMediator = null;
  
  private static P2PFacade myFacade = null;

  /**
   * @param args
   * @throws P2PFacadeException 
   * @throws UserInfoException 
   * @throws AWTException 
   * @throws IOException 
   */
  public static void main( String[] args ) {
    try{
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

      myMediator = new ChatMediator( myFacade );
      myChatFrame = new ChatFrame(myMediator);
      
      if("true".equals(theInterPretser.getKeyValue( "visible" ))){
        showChatFrame();
      }

      SystemTrayMenu theMenu = SystemTrayMenu.buildSystemTray( myChatFrame, myFacade );
      myMediator.setSystemTrayMenu( theMenu );

      new NewMessageTrayIconDisplayer(myChatFrame.getMediator());
      new NewMessageInfoPanelDisplayer(myChatFrame.getMediator());

      initSaveMessages();
      
      loadPlugins();
    }catch(Exception e){
      LOGGER.error("An error occured during boot process", e);
      System.exit(-1);
    }
  }
  
  private static void loadPlugins(){
    PluginActivator.loadAll( true );
    List<iP2pClientPlugin> thePlugins = PluginRegistry.getInstance().getPlugins( iP2pClientPlugin.class );
    for(iP2pClientPlugin thePlugin : thePlugins){
      thePlugin.init( myMediator );
    }
  }

  private static void initSaveMessages(){
    String theLocation = ApplicationPreferences.getInstance().getProperty( "message.backup.file" );
    if(theLocation != null){
      try{
        new SaveMessagesToFile(new File(theLocation), myFacade);
      } catch(Exception e){
        LOGGER.error("Could not init save messages to file", e);
      }
    }
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
      myChatFrame = new ChatFrame(myMediator);
    }
    myChatFrame.showFrame();
  }

  private static void startFacade(ArgsInterPreter anInterPreter) throws P2PFacadeException, IOException{
    iUserInfoProvider theUserInfoProvider = new BackupUserInfoProviderDecorator(new AXALDAPUserInfoProvider());

    myFacade = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( "true".equals(anInterPreter.getKeyValue( "persist" )) )
    .setUserInfoProvider( theUserInfoProvider )
    .addSuperNode( "http://localhost:8080/" )
//    .setSuperNodesDataSource( new ClassPathResource("supernodes.txt") )
    .setStopWhenAlreadyRunning( true )
    .setChannel(anInterPreter.getKeyValue("channel", "default"))
    .setFileHandler( new FileHandlerDialogDispatcher() )
    .setInfoObject( "pom.info", new POMInfo() )
    .setInfoObject( "version", "v2011.06.07" )
    .setSocketReuse( true )
    .setMessageResenderActivated( true )
    .start( 256 );
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
      RoutingProtocol theRoutingProtocol = new RoutingProtocol(null, -1, true, null, false, "AXA");
      RoutingTableEntry theLocalEntry = theRoutingProtocol.getRoutingTable().getEntryForLocalPeer();
      return "ok".equals( theRoutingProtocol.getPeerSender().send( theLocalEntry.getPeer(), "APPactivate" ));
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
    public String handleCommand( String aSessionId, String anInput ) {
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
