/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;

import chabernac.p2pclient.gui.ChatFrame;

public class SendOpenMenuItem extends MenuItem {
  private static final long serialVersionUID = 1224284495962766775L;
  private final ChatFrame myChatFrame;
  
  public SendOpenMenuItem(ChatFrame aChatFrame){
    super("Verzend met open enveloppe");
    myChatFrame = aChatFrame;
  }

  public void actionPerformed(ActionEvent evt){
    setBold();
    myChatFrame.getMediator().setSendWithClosedEnveloppe( false );
  }

  public void setBold(){
    setFont( new Font("Arial", myChatFrame.getMediator().isSendWithClosedEnveloppe() ? Font.PLAIN : Font.BOLD, 12 ) );
  }
}
