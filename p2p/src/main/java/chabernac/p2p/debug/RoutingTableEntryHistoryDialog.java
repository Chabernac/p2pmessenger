/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.table.TableCellRenderer;

import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.RoutingTableEntryHistory;

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
    setSize( 1200, 600 );
    myRoutingTable.setKeepHistory( true );
  }

  private void addListeners(){
    addWindowListener( new MyWindowListener() );
  }

  private void buildGUI(){
    setTitle( "Routing table entry history" );
    setLayout( new BorderLayout() );

    myTable.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
    myTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 50 );
    myTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 100 );
    myTable.getColumnModel().getColumn( 2 ).setPreferredWidth( 500 );
    myTable.getColumnModel().getColumn( 3 ).setPreferredWidth( 100 );
    myTable.getColumnModel().getColumn( 4 ).setPreferredWidth( 500 );
    myTable.setDefaultRenderer(String.class, new ColorRenderer());

    add(new JScrollPane(myTable), BorderLayout.CENTER);
    add(buildButtonPanel(), BorderLayout.SOUTH);

    ToolTipManager.sharedInstance().setDismissDelay(10000);
    ToolTipManager.sharedInstance().setReshowDelay(0);
    ToolTipManager.sharedInstance().setInitialDelay(0);
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

  private class ColorRenderer extends JLabel implements TableCellRenderer{

    private static final long serialVersionUID = 7571899561399741995L;

    @Override
    public Component getTableCellRendererComponent(JTable anTable,
        Object anValue, boolean isSelected, boolean anHasFocus, int anRow,
        int anColumn) {
      setOpaque( true );
      setForeground(Color.darkGray);
      RoutingTableEntryHistory theHistoryEntry = myRoutingTable.getHistory().get(anRow);
      RoutingTableEntry theEntry = theHistoryEntry.getRoutingTableEntry();
      if(listContainsObject(myRoutingTable.getEntries(),theEntry)){
        if(theEntry.getHopDistance() < RoutingTableEntry.MAX_HOP_DISTANCE){
          setForeground(Color.blue);
        } else {
          setForeground(Color.orange);
        }
      } else if(theHistoryEntry.getAction() == RoutingTableEntryHistory.Action.DELETE){
        setForeground(Color.RED);
      }
      if(theHistoryEntry.isResultedInUpdate()){
        setBackground( Color.LIGHT_GRAY );
      }
      setText(anValue.toString());
      setToolTipText(parseToHtml(theHistoryEntry.getStackTrace()));
      return this;
    }

    private String parseToHtml(String aString){
      aString = aString.replaceAll("java.lang.Exception\r\n", "");
      StringBuilder theBuilder = new StringBuilder();
      theBuilder.append("<html>");
      theBuilder.append(aString.replaceAll("\r\n", "<br>"));
      theBuilder.append("</html>");
      return theBuilder.toString();
    }

    private boolean listContainsObject(List<? extends Object> aList, Object anObject){
      for(Object theObject : aList){
        if(theObject == anObject) return true;
      }
      return false;
    }
  }
}
