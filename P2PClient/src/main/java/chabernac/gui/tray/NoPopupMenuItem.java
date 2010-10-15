/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import chabernac.io.ClassPathResource;
import chabernac.p2pclient.gui.ChatMediator;
import chabernac.p2pclient.gui.action.ActionFactory;
import chabernac.p2pclient.gui.action.CommandActionListener;
import chabernac.p2pclient.settings.Settings;
import chabernac.preference.ApplicationPreferences;
import chabernac.preference.iApplicationPreferenceListener;

public class NoPopupMenuItem extends MenuItem implements ActionListener, iApplicationPreferenceListener {
  private static final long serialVersionUID = -5224352709920368154L;
  private final BufferedImage myImage;

  public NoPopupMenuItem(ChatMediator aMediator) throws IOException{
    super("Popup geblokkeerd");
    addActionListener( new CommandActionListener(aMediator.getActionFactory(), ActionFactory.Action.NO_POPUP));
    myImage = ImageIO.read( new ClassPathResource("images/message_blocked.png").getInputStream());
    setBold();
    addPreferenceListener();
  }
  
  private void addPreferenceListener(){
    ApplicationPreferences.getInstance().addApplicationPreferenceListener( this );
  }
  
  public void actionPerformed(ActionEvent evt){
    if(evt.getSource() == this) ApplicationPreferences.getInstance().setEnumProperty(Settings.ReceiveEnveloppe.NO_POPUP);
  }

  private void setBold(){
    setFont( new Font("Arial", ApplicationPreferences.getInstance().hasEnumProperty(Settings.ReceiveEnveloppe.NO_POPUP) ? Font.BOLD : Font.PLAIN, 12 ) );
    if(ApplicationPreferences.getInstance().hasEnumProperty(Settings.ReceiveEnveloppe.NO_POPUP)){
      SystemTray.getSystemTray().getTrayIcons()[0].setImage( myImage );
    }
  }
  
  @Override
  public void applicationPreferenceChanged( String aKey, String aValue ) {
  }

  @Override
  public void applicationPreferenceChanged( Enum anEnumValue ) {
    setBold();
  }
}
