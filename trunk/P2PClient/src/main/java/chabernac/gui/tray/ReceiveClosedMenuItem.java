/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import chabernac.io.ClassPathResource;
import chabernac.p2pclient.settings.Settings;
import chabernac.preference.ApplicationPreferences;

public class ReceiveClosedMenuItem extends MenuItem implements ActionListener {
  private static final long serialVersionUID = -5224352709920368154L;
  private final TrayIcon myTrayIcon;
  private final BufferedImage myImage;

  public ReceiveClosedMenuItem(TrayIcon aTrayIcon) throws IOException{
    super("Ontvang met gesloten enveloppe");
    addActionListener( this );
    myTrayIcon = aTrayIcon;
    myImage = ImageIO.read( new ClassPathResource("images/message.png").getInputStream());
    setBold();
  }
  
  public void actionPerformed(ActionEvent evt){
    if(evt.getSource() == this) ApplicationPreferences.getInstance().setEnumProperty(Settings.ReceiveEnveloppe.CLOSED);
    setBold();
  }

  private void setBold(){
    setFont( new Font("Arial", ApplicationPreferences.getInstance().hasEnumProperty(Settings.ReceiveEnveloppe.CLOSED) ? Font.BOLD : Font.PLAIN, 12 ) );
    if(ApplicationPreferences.getInstance().hasEnumProperty(Settings.ReceiveEnveloppe.CLOSED)){
      myTrayIcon.setImage( myImage );
    }
  }
}
