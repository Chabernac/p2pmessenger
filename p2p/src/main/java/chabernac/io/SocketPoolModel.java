/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.net.Socket;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SocketPoolModel implements TableModel {
  private final SocketPool mySocketPool;

  public SocketPoolModel ( SocketPool anSocketPool ) {
    super();
    mySocketPool = anSocketPool;
  }

  @Override
  public void addTableModelListener( TableModelListener anL ) {

  }

  @Override
  public Class< ? > getColumnClass( int anColumnIndex ) {
    return String.class;
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public String getColumnName( int anColumnIndex ) {
    if(anColumnIndex == 0) return "Host";
    if(anColumnIndex == 2) return "Pool";
    return "";
  }

  @Override
  public int getRowCount() {
    return mySocketPool.getCheckInPool().size() + mySocketPool.getCheckOutPool().size();
  }

  @Override
  public Object getValueAt( int anRowIndex, int anColumnIndex ) {
    Socket theSocket;
    String thePool;
    if(anRowIndex < mySocketPool.getCheckInPool().size()){
      theSocket = mySocketPool.getCheckInPool().get( anRowIndex );
      thePool = "IN";
    } else {
      theSocket = mySocketPool.getCheckOutPool().get(anRowIndex - mySocketPool.getCheckInPool().size());
      thePool = "OUT";
    }
    if(anColumnIndex == 0) return theSocket.getRemoteSocketAddress();
    if(anColumnIndex == 1) return thePool;
    return null;
  }

  @Override
  public boolean isCellEditable( int anRowIndex, int anColumnIndex ) {
    return false;
  }

  @Override
  public void removeTableModelListener( TableModelListener anL ) {

  }

  @Override
  public void setValueAt( Object aValue, int anRowIndex, int anColumnIndex ) {

  }

}
