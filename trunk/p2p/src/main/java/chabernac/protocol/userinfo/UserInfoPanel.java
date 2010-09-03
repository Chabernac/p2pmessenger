/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import chabernac.protocol.userinfo.UserInfo.Status;

public class UserInfoPanel extends JPanel {
  private static final long serialVersionUID = 7636689235996045348L;

  private static Logger LOGGER = Logger.getLogger(UserInfoPanel.class);
  
  private UserInfoProtocol myUserInfoProtocol = null;
  private UserInfoTableModel myModel = null;

  public UserInfoPanel ( UserInfoProtocol anUserInfoProtocol ) {
    super();
    myUserInfoProtocol = anUserInfoProtocol;
    buildGUI();
//    addListeners();
  }
  
  private void buildGUI(){
    setLayout( new BorderLayout() );

    myModel = new UserInfoTableModel(myUserInfoProtocol);
    add(new JScrollPane(new JTable(myModel)), BorderLayout.CENTER);

    JPanel theSouthPanel = new JPanel();
    theSouthPanel.setLayout( new GridLayout(-1,3) );
    theSouthPanel.add(new JButton(new RefreshUserInfo()));
    theSouthPanel.add(new JButton(new AnnounceMe()));
    
    JComboBox theList = new JComboBox(UserInfo.Status.values());
    theList.addItemListener(new MyComboBoxListener());
    theSouthPanel.add(theList);
    
    add(theSouthPanel, BorderLayout.SOUTH);
    setBorder( new TitledBorder("User information") );
  }
  
  private void addListeners(){
    addComponentListener( new ComponentAdapter(){
      public void componentHidden(ComponentEvent e){
        myModel.detachListeners();
      }
    });
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
  
  private class AnnounceMe extends AbstractAction {
    public AnnounceMe(){
      putValue( Action.NAME, "Announce Me" );
    }
    
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myUserInfoProtocol.announceMe();
    }
  }
  
  private class MyComboBoxListener implements ItemListener {

    @Override
    public void itemStateChanged(ItemEvent anItem) {
      try {
        myUserInfoProtocol.getPersonalInfo().setStatus(Status.valueOf(anItem.getItem().toString()));
      } catch (UserInfoException e) {
        LOGGER.error("Could change user status", e);
      }
    }
  }

}
