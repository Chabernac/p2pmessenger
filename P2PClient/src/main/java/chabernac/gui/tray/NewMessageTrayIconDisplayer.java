/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import javax.swing.JFrame;

import chabernac.p2pclient.gui.ChatMediator;
import chabernac.p2pclient.settings.Settings;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iMultiPeerMessageListener;

public class NewMessageTrayIconDisplayer extends TrayIconAnimator{
  public NewMessageTrayIconDisplayer ( ChatMediator anMediator ) throws Exception {
    super(anMediator, "images/message_new.png", "images/message_open.png", -1);
  }

  public class MyMessageListener implements iMultiPeerMessageListener {

    @Override
    public void messageReceived( MultiPeerMessage aMessage ) {
      if(ApplicationPreferences.getInstance().hasEnumProperty( Settings.ReceiveEnveloppe.NO_POPUP ) && !((JFrame)myMediator.getTitleProvider()).hasFocus()){
        animate();
      }
    }
  }
  

  @Override
  protected void addListeners() throws Exception {
    myMediator.getP2PFacade().addMessageListener( new MyMessageListener() );
  }
}
