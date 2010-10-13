/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui.action;

import chabernac.command.Command;
import chabernac.p2pclient.settings.Settings.ReceiveEnveloppe;
import chabernac.preference.ApplicationPreferences;

public class TogglePopupCommand implements Command {

  @Override
  public void execute() {
    ApplicationPreferences thePrefs = ApplicationPreferences.getInstance();
    
    if(thePrefs.hasEnumProperty( ReceiveEnveloppe.NO_POPUP )) thePrefs.setEnumProperty( ReceiveEnveloppe.CLOSED );
    else if(thePrefs.hasEnumProperty( ReceiveEnveloppe.CLOSED )) thePrefs.setEnumProperty( ReceiveEnveloppe.AS_MESSAGE_INDICATES );
    else if(thePrefs.hasEnumProperty( ReceiveEnveloppe.AS_MESSAGE_INDICATES)) thePrefs.setEnumProperty( ReceiveEnveloppe.NO_POPUP );
  }

}
