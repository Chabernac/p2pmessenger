/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class PeerSocketTableModel implements TableModel {
  
  private final PeerSocketFactory myFactory;
  
  public PeerSocketTableModel ( PeerSocketFactory anFactory ) {
    super();
    myFactory = anFactory;
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
    if(anColumnIndex == 0) return "Peer";
    if(anColumnIndex == 1) return "Socket";
    if(anColumnIndex == 2) return "Connected";
    if(anColumnIndex == 3) return "Local port";
    return ""; 
  }

  @Override
  public int getRowCount() {
   return myFactory.getSockets().size();
  }

  @Override
  public Object getValueAt( int anRowIndex, int anColumnIndex ) {
    List< String > thePeers = new ArrayList< String >(myFactory.getSockets().keySet());
    String thePeer = thePeers.get(anRowIndex);
    Socket theSocket = myFactory.getSockets().get( thePeer );
    if(anColumnIndex == 0) return thePeer;
    if(anColumnIndex == 1) return theSocket.getInetAddress().getHostAddress() + ":" + theSocket.getPort();
    if(anColumnIndex == 2) return theSocket.isConnected();
    if(anColumnIndex == 3) return theSocket.getLocalPort();
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
