/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import chabernac.p2pclient.gui.ChatFrame;

public class OnTopMenuItem extends MenuItem implements ActionListener {
  private static final long serialVersionUID = -8774276645215426532L;
  private final ChatFrame myChatFrame;
  
  public OnTopMenuItem(ChatFrame aChatFrame){
    super("Altijd op de voorgrond");
    myChatFrame = aChatFrame;
    addActionListener( this );
  }

  public void actionPerformed(ActionEvent evt){
    if(myChatFrame != null){
      if(myChatFrame.isAlwaysOnTop()){
        myChatFrame.setAlwaysOnTop( false);
        setLabel( "Altijd op de voorgrond" );
      } else {
        myChatFrame.setAlwaysOnTop( true );
        setLabel( "Niet op de voorgrond" );
      }
    }
  }
}
