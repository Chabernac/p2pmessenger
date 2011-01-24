/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.SystemTray;
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

public class InfoPanelMenuItem extends MenuItem implements iApplicationPreferenceListener {
  private static final long serialVersionUID = -5224352709920368154L;
  private final BufferedImage myImage;

  public InfoPanelMenuItem(ChatMediator aMediator) throws IOException{
    super("Toon informatie ballon");
    myImage = ImageIO.read( new ClassPathResource("images/message_dialog.png").getInputStream());
    addActionListener( new CommandActionListener(aMediator.getActionFactory(), ActionFactory.Action.INFO_PANEL));
    setBold();
    addPreferenceListener();
  }
  
  private void addPreferenceListener(){
    ApplicationPreferences.getInstance().addApplicationPreferenceListener( this );
  }
  
  private void setBold(){
    setFont( new Font("Arial", ApplicationPreferences.getInstance().hasEnumProperty(Settings.ReceiveEnveloppe.INFO_PANEL) ? Font.BOLD : Font.PLAIN, 12 ) );
    
    if(ApplicationPreferences.getInstance().hasEnumProperty(Settings.ReceiveEnveloppe.INFO_PANEL)){
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
