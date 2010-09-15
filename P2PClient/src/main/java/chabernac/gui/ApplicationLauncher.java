/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui;

import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.events.EventDispatcher;
import chabernac.gui.event.SavePreferencesEvent;
import chabernac.io.ClassPathResource;
import chabernac.io.SocketProxy;
import chabernac.ldapuserinfoprovider.AXALDAPUserInfoProvider;
import chabernac.ldapuserinfoprovider.BackupUserInfoProviderDecorator;
import chabernac.lock.FileLock;
import chabernac.lock.iLock;
import chabernac.p2pclient.gui.ChatFrame;
import chabernac.p2pclient.gui.NewMessageDialog5;
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

    if("true".equals(theInterPretser.getKeyValue("checklock", "true"))){
      if(!checkLockAndActivate()) return;
    }

    BasicConfigurator.configure();

    addRun2Startup();

    SocketProxy.setTraceEnabled( true );

    startTimers();

    startFacade(theInterPretser);

    addActivationListener();

    if("true".equals(theInterPretser.getKeyValue( "visible" ))){
      showChatFrame();
    }

    createSystemTray();
  }

  private static void addActivationListener() throws P2PFacadeException{
    myFacade.setApplicationProtocolDelegate( new ActivationProtocolDelegate() );
  }

  public static synchronized void showChatFrame() throws P2PFacadeException{
    if(myChatFrame == null){
      myChatFrame = new ChatFrame(myFacade);
    }
    myChatFrame.setVisible( true );
    myChatFrame.setState( Frame.NORMAL );
    myChatFrame.requestFocus();
    NewMessageDialog5.getInstance( myChatFrame.getMediator() ).cancelPendingTasks();
    NewMessageDialog5.getInstance( myChatFrame.getMediator() ).setVisible( false );
  }

  private static void startFacade(ArgsInterPreter anInterPreter) throws P2PFacadeException{
    iUserInfoProvider theUserInfoProvider = new BackupUserInfoProviderDecorator(new AXALDAPUserInfoProvider());

    myFacade = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( "true".equals(anInterPreter.getKeyValue( "persist" )) )
    .setUserInfoProvider( theUserInfoProvider )
    .setSuperNodesDataSource( new ClassPathResource("supernodes.txt") )
    .setStopWhenAlreadyRunning( true )
    .setChannel(anInterPreter.getKeyValue("channel", "default"))
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
      RoutingProtocol theRoutingProtocol = new RoutingProtocol(null, -1, true, null, false, "AXA");
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

  private static void createSystemTray(){
    if(SystemTray.isSupported()){
      try{
        SystemTray theTray = SystemTray.getSystemTray();


        PopupMenu theMenu = new PopupMenu();
        final TrayIcon theIcon = new TrayIcon(ImageIO.read( new ClassPathResource("images/message.png").getInputStream()), "P2PClient", theMenu);

        MenuItem theOpenItem = new MenuItem("Open");
        theOpenItem.addActionListener( new ActionListener(){
          public void actionPerformed(ActionEvent evt){
            try {
              showChatFrame();
            } catch ( P2PFacadeException e ) {
              LOGGER.error("Unable to load chat frame", e);
            }
          }
        });
        MenuItem theExitItem = new MenuItem("Exit");
        theExitItem.addActionListener( new ActionListener(){
          public void actionPerformed(ActionEvent evt){
            if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog( myChatFrame, "Ben je zeker dat je wilt afsluiten? Als je afsluit kan je geen berichten meer ontvangen.", "Afsluiten", JOptionPane.OK_CANCEL_OPTION)){
              try{
                theIcon.setToolTip( "P2PClient: Bezig met afsluiten" );
                if(myChatFrame != null) myChatFrame.setVisible( false );
                EventDispatcher.getInstance( SavePreferencesEvent.class ).fireEvent( new SavePreferencesEvent() );
                ApplicationPreferences.getInstance().save();
                myFacade.stop();
              }catch(Throwable e){
                LOGGER.error("Could not properly exit", e);
              } finally {
                System.exit(0);
              }
            }
          }
        });

        final MenuItem theOntopItem = new MenuItem("Always on top");
        theOntopItem.addActionListener( new ActionListener(){
          public void actionPerformed(ActionEvent evt){
            if(myChatFrame != null){
              if(myChatFrame.isAlwaysOnTop()){
                myChatFrame.setAlwaysOnTop( false);
                theOntopItem.setLabel( "Always on top" );
              } else {
                myChatFrame.setAlwaysOnTop( true );
                theOntopItem.setLabel( "Not always on top" );
              }
            }
          }
        });

        theMenu.add( theOpenItem );
        theMenu.add( theOntopItem );
        theMenu.add( theExitItem );


        theTray.add( theIcon );
        theIcon.addActionListener( new ActionListener(){
          public void actionPerformed(ActionEvent evt){
            if(myChatFrame == null || !myChatFrame.isVisible() || myChatFrame.getState() == Frame.ICONIFIED){
              try {
                showChatFrame();
              } catch ( P2PFacadeException e ) {
                LOGGER.error("Unable to show chat frame");
              }
            } else {
              myChatFrame.setVisible( false );
            }
          }
        });
        theIcon.addMouseListener( new MouseAdapter(){
          public void mouseClicked(MouseEvent anEvent){

          }
        });

      }catch(Exception e){
        LOGGER.error("Could not create system tray icon", e);
      }
    }
  }
}
