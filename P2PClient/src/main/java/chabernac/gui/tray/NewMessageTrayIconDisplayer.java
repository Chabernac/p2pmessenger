/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;

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
  private Image myPreviousImage = null;

  public NewMessageTrayIconDisplayer ( ChatMediator anMediator ) throws P2PFacadeException, IOException {
    super();
    myMediator = anMediator;
    myMediator.getP2PFacade().addMessageListener( new MyMessageListener() );
    myNewMessageImage = ImageIO.read( new ClassPathResource("images/message_new.png").getInputStream());
    SystemTray.getSystemTray().getTrayIcons()[0].addActionListener( new MyActionListener() );
  }

  public class MyMessageListener implements iMultiPeerMessageListener {

    @Override
    public void messageReceived( MultiPeerMessage aMessage ) {
      if(ApplicationPreferences.getInstance().hasEnumProperty( Settings.ReceiveEnveloppe.NO_POPUP )){
        myPreviousImage = SystemTray.getSystemTray().getTrayIcons()[0].getImage();
        SystemTray.getSystemTray().getTrayIcons()[0].setImage( myNewMessageImage );
      }
    }
  }
  
  
  public class MyActionListener implements ActionListener {
    @Override
    public void actionPerformed( ActionEvent anE ) {
      SystemTray.getSystemTray().getTrayIcons()[0].setImage( myPreviousImage );
    }
  }
}
