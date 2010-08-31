/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MessagePanel extends JPanel {
  
  private static final long serialVersionUID = 6219310744066274813L;

  public MessagePanel(MessageProtocol aProtocol){
    buildGUI(aProtocol);
  }
  
  private void buildGUI(MessageProtocol aProtocol){
    setLayout( new BorderLayout() );
    add(new JScrollPane(new JTable(new MessageModel(aProtocol))), BorderLayout.CENTER);
  }

}
