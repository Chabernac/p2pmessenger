/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

public class UserInfoPanel extends JPanel {
  private UserInfoProtocol myUserInfoProtocol = null;

  public UserInfoPanel ( UserInfoProtocol anUserInfoProtocol ) {
    super();
    myUserInfoProtocol = anUserInfoProtocol;
    buildGUI();
  }

  private void buildGUI(){
    setLayout( new BorderLayout() );

    add(new JScrollPane(new JTable(new UserInfoTableModel(myUserInfoProtocol))), BorderLayout.CENTER);

    JPanel theSouthPanel = new JPanel();
    theSouthPanel.setLayout( new GridLayout(-1,3) );
    theSouthPanel.add(new JButton(new RefreshUserInfo()));
    add(theSouthPanel, BorderLayout.SOUTH);
    setBorder( new TitledBorder("User information") );
  }


  private class RefreshUserInfo extends AbstractAction {
    public RefreshUserInfo(){
      putValue( Action.NAME, "Refresh user info" );
    }
    
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myUserInfoProtocol.fullRetrieval();
    }
  }

}
