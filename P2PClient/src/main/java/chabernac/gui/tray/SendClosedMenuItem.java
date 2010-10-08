/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;

import chabernac.p2pclient.gui.ChatFrame;

public class SendClosedMenuItem extends MenuItem {
  private static final long serialVersionUID = 8511412696194375795L;
  private final ChatFrame myChatFrame;
  
  public SendClosedMenuItem(ChatFrame aChatFrame){
    super("Verzend met gesloten enveloppe");
    myChatFrame = aChatFrame;
  }

  public void actionPerformed(ActionEvent evt){
    setBold();
    myChatFrame.getMediator().setSendWithClosedEnveloppe( true );
  }

  public void setBold(){
    setFont( new Font("Arial", myChatFrame.getMediator().isSendWithClosedEnveloppe() ? Font.BOLD : Font.PLAIN, 12 ) );
  }
}
