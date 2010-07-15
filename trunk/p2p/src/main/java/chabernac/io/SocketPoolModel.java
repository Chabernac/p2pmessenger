/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.text.SimpleDateFormat;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SocketPoolModel implements TableModel {
  private final SocketPool mySocketPool;
  private SimpleDateFormat myFormat = new SimpleDateFormat("HH:mm:ss");

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
    return 4;
  }

  @Override
  public String getColumnName( int anColumnIndex ) {
    if(anColumnIndex == 0) return "Host";
    if(anColumnIndex == 1) return "Local";
    if(anColumnIndex == 2) return "Pool";
    if(anColumnIndex == 3) return "Connect time";
    return "";
  }

  @Override
  public int getRowCount() {
    return mySocketPool.getCheckInPool().size() + mySocketPool.getCheckOutPool().size() + mySocketPool.getConnectingPool().size();
  }

  @Override
  public Object getValueAt( int anRowIndex, int anColumnIndex ) {
    SocketProxy theSocket;
    String thePool;
    if(anRowIndex < mySocketPool.getCheckInPool().size()){
      theSocket = mySocketPool.getCheckInPool().get( anRowIndex );
      thePool = "IN";
    } else if(anRowIndex < mySocketPool.getCheckInPool().size() + mySocketPool.getCheckOutPool().size()){
      theSocket = mySocketPool.getCheckOutPool().get(anRowIndex - mySocketPool.getCheckInPool().size());
      thePool = "OUT";
    } else {
      theSocket = mySocketPool.getConnectingPool().get(anRowIndex - mySocketPool.getCheckInPool().size() - mySocketPool.getCheckOutPool().size());
      thePool = "CONNECT";
    }
    if(anColumnIndex == 0) return theSocket.getSocketAddress();
    if(anColumnIndex == 1) {
      if(theSocket.isConnected()) return theSocket.getSocket().getLocalSocketAddress();
    }
    if(anColumnIndex == 2) return thePool;

    if(anColumnIndex == 3) {
      if(theSocket.getConnectTime() != null){
        return myFormat.format( theSocket.getConnectTime() );
      }
    }
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
