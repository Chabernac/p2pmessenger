/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import chabernac.p2pclient.gui.ChatFrame;
import chabernac.p2pclient.gui.NewMessageDialog5;

public class ReceiveAsMessageIndicatesMenuItem extends MenuItem implements ActionListener {
  private static final long serialVersionUID = -5224352709920368154L;
  private final ChatFrame myChatFrame;

  public ReceiveAsMessageIndicatesMenuItem(ChatFrame aChatFrame){
    super("Ontvang zoals bericht aangeeft");
    myChatFrame = aChatFrame;
    addActionListener( this );
  }
  
  public void actionPerformed(ActionEvent evt){
    setBold();
    NewMessageDialog5.getInstance( myChatFrame.getMediator() ).setEnveloppeAlwaysClosed( false );
  }

  private void setBold(){
    NewMessageDialog5 theDialog = NewMessageDialog5.getInstance( myChatFrame.getMediator() );
    setFont( new Font("Arial", theDialog.isEnveloppeAlwaysClosed() ? Font.BOLD : Font.PLAIN, 12 ) );
  }
}
