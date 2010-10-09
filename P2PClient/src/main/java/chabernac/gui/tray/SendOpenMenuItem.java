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

public class SendOpenMenuItem extends MenuItem implements ActionListener {
  private static final long serialVersionUID = 1224284495962766775L;
  
  public SendOpenMenuItem(){
    super("Verzend met open enveloppe");
    addActionListener(this);
    setBold();
  }

  public void actionPerformed(ActionEvent evt){
    if(evt.getSource() == this ) ApplicationPreferences.getInstance().setEnumProperty(SendEnveloppe.OPEN);
    setBold();
  }

  public void setBold(){
    setFont( new Font("Arial", ApplicationPreferences.getInstance().hasEnumProperty(SendEnveloppe.OPEN) ? Font.BOLD : Font.PLAIN, 12 ) );
  }
}
