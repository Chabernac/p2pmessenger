/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import chabernac.p2pclient.settings.Settings.SendEnveloppe;
import chabernac.preference.ApplicationPreferences;

public class SendClosedMenuItem extends MenuItem implements ActionListener {
  private static final long serialVersionUID = 8511412696194375795L;
  
  public SendClosedMenuItem(){
    super("Verzend met gesloten enveloppe");
    addActionListener(this);
    setBold();
  }

  public void actionPerformed(ActionEvent evt){
    if(evt.getSource() == this ) ApplicationPreferences.getInstance().setEnumProperty(SendEnveloppe.CLOSED);
    setBold();
  }

  public void setBold(){
    setFont( new Font("Arial", ApplicationPreferences.getInstance().hasEnumProperty(SendEnveloppe.CLOSED) ?  Font.BOLD : Font.PLAIN, 12 ) );
  }
}
