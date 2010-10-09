/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import chabernac.p2pclient.settings.Settings;
import chabernac.preference.ApplicationPreferences;

public class ReceiveAsMessageIndicatesMenuItem extends MenuItem implements ActionListener {
  private static final long serialVersionUID = -5224352709920368154L;

  public ReceiveAsMessageIndicatesMenuItem(){
    super("Ontvang zoals bericht aangeeft");
    addActionListener( this );
    setBold();
  }
  
  public void actionPerformed(ActionEvent evt){
    if(evt.getSource() == this) ApplicationPreferences.getInstance().setEnumProperty(Settings.ReceiveEnveloppe.AS_MESSAGE_INDICATES);
    setBold();
  }

  private void setBold(){
    setFont( new Font("Arial", ApplicationPreferences.getInstance().hasEnumProperty(Settings.ReceiveEnveloppe.AS_MESSAGE_INDICATES) ? Font.BOLD : Font.PLAIN, 12 ) );
  }
}
