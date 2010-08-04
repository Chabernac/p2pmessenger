/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class RoutingTableEntryHistoryDialog extends JDialog {
  
  private static final long serialVersionUID = -1976494062381300222L;
  private final RoutingTable myRoutingTable;
  private final RoutingTableHistoryModel myModel;
  private final JTable myTable;

  public RoutingTableEntryHistoryDialog ( RoutingTable anRoutingTable ) {
    super();
    myRoutingTable = anRoutingTable;
    myModel = new RoutingTableHistoryModel(myRoutingTable);
    myTable = new JTable(myModel);
    buildGUI();
    addListeners();
    setSize( 1200, 800 );
    myRoutingTable.setKeepHistory( true );
  }
  
  private void addListeners(){
    addWindowListener( new MyWindowListener() );
  }
  
  private void buildGUI(){
    setTitle( "Routing table entry history" );
    setLayout( new BorderLayout() );
    
    myTable.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
    myTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 100 );
    myTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 500 );
    myTable.getColumnModel().getColumn( 2 ).setPreferredWidth( 100 );
    myTable.getColumnModel().getColumn( 3 ).setPreferredWidth( 500 );
    
    add(new JScrollPane(myTable), BorderLayout.CENTER);
    add(buildButtonPanel(), BorderLayout.SOUTH);
  }

  private Component buildButtonPanel() {
    JPanel theButtonPanel = new JPanel();
    theButtonPanel.setLayout( new FlowLayout(FlowLayout.RIGHT) );
    
    theButtonPanel.add( new JButton(new StartTrackingHistory()) );
    theButtonPanel.add( new JButton(new StopTrackingHistory()) );
    theButtonPanel.add( new JButton(new RefreshHistory()) );
    theButtonPanel.add( new JButton(new ClearHistory()) );
    return theButtonPanel;
  }
  
  public class StartTrackingHistory extends AbstractAction {
    public StartTrackingHistory(){
      putValue( Action.NAME, "Start tracking" );
    }
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myRoutingTable.setKeepHistory( true );
    }
  }
  public class StopTrackingHistory extends AbstractAction {
    public StopTrackingHistory(){
      putValue( Action.NAME, "Stop tracking" );
    }
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myRoutingTable.setKeepHistory( false );
    }
  }
  public class RefreshHistory extends AbstractAction {
    public RefreshHistory(){
      putValue( Action.NAME, "Refresh" );
    }
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myModel.refresh();
    }
  }
  
  public class ClearHistory extends AbstractAction {
    public ClearHistory(){
      putValue( Action.NAME, "Clear" );
    }
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myRoutingTable.clearHistory();
      myModel.refresh();
    }
  }
  
  public class MyWindowListener extends WindowAdapter {
    @Override
    public void windowClosing( WindowEvent anE ) {
      myRoutingTable.setKeepHistory( false );
    }
  }
}
