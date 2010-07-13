/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.util.concurrent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class MonitorTableModel implements TableModel {
  
  private final LinkedHashMap< Thread, String > myStatusMap;
  
  public MonitorTableModel ( LinkedHashMap< Thread, String > anStatusMap ) {
    super();
    myStatusMap = anStatusMap;
  }


  @Override
  public void addTableModelListener( TableModelListener anL ) {
    // TODO Auto-generated method stub

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
    if(anColumnIndex == 0) return "Thread";
    if(anColumnIndex == 1) return "Status";
    return "";
  }

  @Override
  public int getRowCount() {
    return myStatusMap.size();
  }

  @Override
  public Object getValueAt( int anRowIndex, int anColumnIndex ) {
    List< Thread > theThreadList = new ArrayList< Thread >(myStatusMap.keySet());
    Thread theThread = theThreadList.get( anRowIndex );
    if(anColumnIndex == 0) return theThread.getName();
    if(anColumnIndex == 1) return myStatusMap.get( theThread );
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
