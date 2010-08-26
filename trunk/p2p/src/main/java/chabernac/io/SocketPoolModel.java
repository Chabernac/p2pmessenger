/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.net.Socket;
import java.text.SimpleDateFormat;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SocketPoolModel implements TableModel {
  private final iSocketPool mySocketPool;
  private SimpleDateFormat myFormat = new SimpleDateFormat("HH:mm:ss");

  public SocketPoolModel ( iSocketPool anSocketPool ) {
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
    return mySocketPool.getCheckedInPool().size() + mySocketPool.getCheckedOutPool().size() + mySocketPool.getConnectingPool().size();
  }

  public Object getSocketProxyAtRow(int anRowIndex){
    Object theSocket;
    try{
      if(anRowIndex < mySocketPool.getCheckedInPool().size()){
        theSocket = mySocketPool.getCheckedInPool().get( anRowIndex );
      } else if(anRowIndex < mySocketPool.getCheckedInPool().size() + mySocketPool.getCheckedOutPool().size()){
        theSocket = mySocketPool.getCheckedOutPool().get(anRowIndex - mySocketPool.getCheckedInPool().size());
      } else {
        theSocket = mySocketPool.getConnectingPool().get(anRowIndex - mySocketPool.getCheckedInPool().size() - mySocketPool.getCheckedOutPool().size());
      }
    }catch(Exception e){
      return null;
    }
    return theSocket;
  }

  public String getPool(Object aProxy){
    if(mySocketPool.getCheckedInPool().contains(aProxy)) return "IN";
    if(mySocketPool.getCheckedOutPool().contains(aProxy)) return "OUT";
    if(mySocketPool.getConnectingPool().contains(aProxy)) return "CONNECT";
    return "NO POOL";
  }

  @Override
  public Object getValueAt( int anRowIndex, int anColumnIndex ) {
    Object theSocket = getSocketProxyAtRow(anRowIndex);
    if(theSocket == null) return "NO SOCKET";
    String thePool = getPool(theSocket);

    if(theSocket instanceof SocketProxy){
      SocketProxy theSocketProxy = (SocketProxy)theSocket;
      if(anColumnIndex == 0) return theSocketProxy.getSocketAddress();
      if(anColumnIndex == 1) {
        if(theSocketProxy.isConnected()) {
          return theSocketProxy.getSocket().getLocalSocketAddress();
        }
        return "NOT CONNECTED";
      }
      if(anColumnIndex == 2) return thePool;

      if(anColumnIndex == 3) {
        if(theSocketProxy.getConnectTime() != null){
          return myFormat.format( theSocketProxy.getConnectTime() );
        }
        return "";
      }
    } else if(theSocket instanceof Socket){
      Socket theS = (Socket)theSocket;
      if(anColumnIndex == 0) return theS.getRemoteSocketAddress();
      if(anColumnIndex == 1) return theS.getLocalAddress();
      if(anColumnIndex == 2) return thePool;

      if(anColumnIndex == 3) return "UNKNOWN";
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
