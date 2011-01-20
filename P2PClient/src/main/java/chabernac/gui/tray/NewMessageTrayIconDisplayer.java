/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import chabernac.events.EventDispatcher;
import chabernac.events.iEventListener;
import chabernac.gui.event.FocusGainedEvent;
import chabernac.io.ClassPathResource;
import chabernac.p2pclient.gui.ChatMediator;
import chabernac.p2pclient.settings.Settings;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iMultiPeerMessageListener;

public class NewMessageTrayIconDisplayer {
  private final ChatMediator myMediator;
  private Image myNewMessageImage = null;
  private Image myNewMessageImage2 = null;
  private ScheduledExecutorService myService = null;
  
  private Object LOCK = new Object();

  public NewMessageTrayIconDisplayer ( ChatMediator anMediator ) throws P2PFacadeException, IOException {
    super();
    myMediator = anMediator;
    myMediator.getP2PFacade().addMessageListener( new MyMessageListener() );
    myNewMessageImage = ImageIO.read( new ClassPathResource("images/message_new.png").getInputStream());
    myNewMessageImage2 = ImageIO.read( new ClassPathResource("images/message_open.png").getInputStream());
    MyListener theListener = new MyListener();
    SystemTray.getSystemTray().getTrayIcons()[0].addActionListener( theListener );
    SystemTray.getSystemTray().getTrayIcons()[0].addMouseListener( theListener );
    EventDispatcher.getInstance(FocusGainedEvent.class).addEventListener(theListener);
  }

  public class MyMessageListener implements iMultiPeerMessageListener {

    @Override
    public void messageReceived( MultiPeerMessage aMessage ) {
      if(ApplicationPreferences.getInstance().hasEnumProperty( Settings.ReceiveEnveloppe.NO_POPUP ) && !((JFrame)myMediator.getTitleProvider()).hasFocus()){
        SystemTray.getSystemTray().getTrayIcons()[0].setImage( myNewMessageImage );
        
        synchronized(LOCK){
          if(myService == null){
            myService = Executors.newScheduledThreadPool( 1 );
            myService.scheduleAtFixedRate( new ImageSwitcher(), 3, 3, TimeUnit.SECONDS );
          }
        }
      }
    }
  }
  
  private void resetImage(){
    synchronized(LOCK){
      if(myService != null) {
        myService.shutdownNow();
        myService  = null;
      }
    }
    //just trigger the ReceiveEnveloppe property, it will cause the other menu items to evaluate and reset the tray icon to the correct one
    ApplicationPreferences.getInstance().notifyListeners( (Enum)null );
    
  }
  
  
  public class MyListener extends MouseAdapter implements ActionListener, iEventListener<FocusGainedEvent>{
    @Override
    public void actionPerformed( ActionEvent anE ) {
     resetImage();
    }

    @Override
    public void mouseClicked(MouseEvent anArg0) {
      resetImage();
    }
    
    @Override
    public void eventFired(FocusGainedEvent anEvent) {
      resetImage();
    }
  }
  
  public class ImageSwitcher implements Runnable {

    @Override
    public void run() {
      TrayIcon theTrayIcon = SystemTray.getSystemTray().getTrayIcons()[0];
      theTrayIcon.setImage( myNewMessageImage2 );
      try {
        Thread.sleep( 1000 );
      } catch ( InterruptedException e ) {
      }
      theTrayIcon.setImage( myNewMessageImage );
    }
  }
}
