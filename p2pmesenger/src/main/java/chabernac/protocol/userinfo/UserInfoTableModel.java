/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class UserInfoTableModel implements TableModel {
  private UserInfoProtocol myUserInfoProtocol = null;
  private List< TableModelListener > myListeners = new ArrayList< TableModelListener >();

  public UserInfoTableModel ( UserInfoProtocol aUserInfoProtocol ) {
    super();
    myUserInfoProtocol = aUserInfoProtocol;
    myUserInfoProtocol.addUserInfoListener( new MyTableModelListener() );
  }

  @Override
  public void addTableModelListener( TableModelListener anListener ) {
    myListeners.add( anListener );
  }

  @Override
  public Class< ? > getColumnClass( int anColumnIndex ) {
    return String.class;
  }

  @Override
  public int getColumnCount() {
    return 7;
  }

  @Override
  public String getColumnName( int anColumnIndex ) {
    if(anColumnIndex == 0) return "Peer Id";
    if(anColumnIndex == 1) return "Status";
    if(anColumnIndex == 2) return "Id";
    if(anColumnIndex == 3) return "Name";
    if(anColumnIndex == 4) return "Email";
    if(anColumnIndex == 5) return "Telnr";
    if(anColumnIndex == 6) return "Location";
    return ""; 
  }

  @Override
  public int getRowCount() {
    return myUserInfoProtocol.getUserInfo().size();
  }

  @Override
  public Object getValueAt( int anRowIndex, int anColumnIndex ) {
    ArrayList< String> thePeerIds = new ArrayList< String >(myUserInfoProtocol.getUserInfo().keySet());
    String thePeerId = thePeerIds.get(anRowIndex);

    UserInfo theUserInfo = myUserInfoProtocol.getUserInfo().get(thePeerId);

    if(anColumnIndex == 0) return thePeerId;
    if(anColumnIndex == 1) return theUserInfo.getStatus().name();
    if(anColumnIndex == 2) return theUserInfo.getId();
    if(anColumnIndex == 3) return theUserInfo.getName();
    if(anColumnIndex == 4) return theUserInfo.getEMail();
    if(anColumnIndex == 5) return theUserInfo.getTelNr();
    if(anColumnIndex == 6) return theUserInfo.getLocation();
    return "";
  }

  @Override
  public boolean isCellEditable( int anRowIndex, int anColumnIndex ) {
    return false;
  }

  @Override
  public void removeTableModelListener( TableModelListener anListener ) {
    myListeners.remove( anListener );
  }

  @Override
  public void setValueAt( Object aValue, int anRowIndex, int anColumnIndex ) {

  }

  private class MyTableModelListener implements iUserInfoListener {
    @Override
    public void userInfoChanged( UserInfo aUserInfo, Map< String, UserInfo > aFullUserInfoList ) {
      for(TableModelListener theListener : myListeners){
        theListener.tableChanged( new TableModelEvent(UserInfoTableModel.this) );
      }
    }
  }
}
