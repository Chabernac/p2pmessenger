/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class RoutingTableHistoryModel implements TableModel {
  private final RoutingTable myRoutingTable;
  private List< TableModelListener > myListeners = new ArrayList< TableModelListener >();
  private SimpleDateFormat myFormat = new SimpleDateFormat("HH:mm:ss SSS");

  public RoutingTableHistoryModel ( RoutingTable anRoutingTable ) {
    super();
    myRoutingTable = anRoutingTable;
    myRoutingTable.addRoutingTableListener( new MyRoutingTableListner() );
  }

  @Override
  public void addTableModelListener( TableModelListener anL ) {
    myListeners.add(anL);

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
    if(anColumnIndex == 0) return "Time";
    if(anColumnIndex == 1) return "Peer";
    if(anColumnIndex == 2) return "Hop distance";
    if(anColumnIndex == 3) return "Gateway";
    return "";
  }

  @Override
  public int getRowCount() {
    return myRoutingTable.getHistory().size();
  }

  @Override
  public Object getValueAt( int anRowIndex, int anColumnIndex ) {
    RoutingTableEntryHistory theEntry = myRoutingTable.getHistory().get(anRowIndex);
    if(anColumnIndex == 0){
      Date theDate = new Date();
      theDate.setTime( theEntry.getRoutingTableEntry().getCreationTime() );
      return myFormat.format(  theDate );
    }
    if(anColumnIndex == 1) return theEntry.getRoutingTableEntry().getPeer().toString();
    if(anColumnIndex == 2) return theEntry.getRoutingTableEntry().getHopDistance();
    if(anColumnIndex == 3) return theEntry.getRoutingTableEntry().getGateway().toString();
    return null;
  }

  @Override
  public boolean isCellEditable( int anRowIndex, int anColumnIndex ) {
    return false;
  }

  @Override
  public void removeTableModelListener( TableModelListener anL ) {
    myListeners.remove( anL );
  }

  @Override
  public void setValueAt( Object aValue, int anRowIndex, int anColumnIndex ) {

  }

  public void refresh(){
    for(TableModelListener theListener : myListeners){
      theListener.tableChanged( new TableModelEvent(this) );
    }
  }
  
  private class MyRoutingTableListner implements IRoutingTableListener{
    @Override
    public void routingTableEntryChanged( RoutingTableEntry anEntry ) {
      refresh();
    }
  }

}
