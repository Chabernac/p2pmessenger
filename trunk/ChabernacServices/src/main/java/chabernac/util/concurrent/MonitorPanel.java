/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.util.concurrent;

import java.awt.BorderLayout;
import java.util.LinkedHashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;

import chabernac.util.concurrent.MonitorrableRunnable.Status;

public class MonitorPanel extends JPanel implements iRunnableListener{
  private static final long serialVersionUID = 2356339381341382204L;
  private LinkedHashMap< Thread, String > myStatusMap = new LinkedHashMap< Thread, String >();
  MonitorTableModel myTableModel = new MonitorTableModel(myStatusMap);
  private JTable myTable = new JTable(myTableModel);
  
  public MonitorPanel(){
    buildGUI();
  }
  
  private void buildGUI(){
    setLayout(  new BorderLayout() );
    add(new JScrollPane(myTable), BorderLayout.CENTER);
    setBorder( new TitledBorder("Thread information") );
  }

  @Override
  public void statusChanged( Status aStatus, String anExtraInfo ) {
    myStatusMap.put( Thread.currentThread(), aStatus + " " + anExtraInfo);
    myTable.tableChanged( new TableModelEvent(myTableModel) );
  }

}
