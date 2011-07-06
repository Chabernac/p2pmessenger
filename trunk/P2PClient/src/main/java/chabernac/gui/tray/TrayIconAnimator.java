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

import chabernac.events.EventDispatcher;
import chabernac.events.iEventListener;
import chabernac.gui.event.FocusGainedEvent;
import chabernac.io.ClassPathResource;
import chabernac.p2pclient.gui.ChatMediator;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.facade.P2PFacadeException;

public abstract class TrayIconAnimator {
  protected final ChatMediator myMediator;
  private Image myImage1 = null;
  private Image myImage2 = null;
  private ScheduledExecutorService myService = null;
  private ScheduledExecutorService myResetService = null;
  private long myMaxDurationInSeconds = -1;
  
  private Object LOCK = new Object();

  public TrayIconAnimator ( ChatMediator anMediator, String anImageResource1, String anImageResource2, long aMaxDurationInSeconds ) throws Exception {
    super();
    myMediator = anMediator;
    myMaxDurationInSeconds = aMaxDurationInSeconds;
    addListeners();
    myImage1 = ImageIO.read( new ClassPathResource(anImageResource1).getInputStream());
    myImage2 = ImageIO.read( new ClassPathResource(anImageResource2).getInputStream());
    MyListener theListener = new MyListener();
    SystemTray.getSystemTray().getTrayIcons()[0].addActionListener( theListener );
    SystemTray.getSystemTray().getTrayIcons()[0].addMouseListener( theListener );
    EventDispatcher.getInstance(FocusGainedEvent.class).addEventListener(theListener);
  }

  protected abstract void addListeners() throws Exception;
  
  protected void animate(){
    SystemTray.getSystemTray().getTrayIcons()[0].setImage( myImage1 );
    
    synchronized(LOCK){
      if(myService == null){
        myService = Executors.newScheduledThreadPool( 1 );
        myService.scheduleAtFixedRate( new ImageSwitcher(), 2, 2, TimeUnit.SECONDS );
      }
      
      if(myMaxDurationInSeconds > 0 && myResetService == null){
        myResetService = Executors.newScheduledThreadPool( 1 );
        myResetService.schedule( new ResetCommand(), myMaxDurationInSeconds, TimeUnit.SECONDS );
      }
    }
  
  }
  
  private void resetImage(){
    synchronized(LOCK){
      if(myService != null) {
        myService.shutdownNow();
        myService  = null;
      }
      if(myResetService != null){
        myResetService.shutdown();
        myResetService = null;
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
      theTrayIcon.setImage( myImage2 );
      try {
        Thread.sleep( 500 );
      } catch ( InterruptedException e ) {
      }
      theTrayIcon.setImage( myImage1 );
    }
  }
  
  public class ResetCommand implements Runnable{
    public void run(){
      resetImage();
    }
  }
}
