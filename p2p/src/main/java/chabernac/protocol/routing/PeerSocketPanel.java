/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.awt.BorderLayout;
import java.net.Socket;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;

public class PeerSocketPanel extends JPanel implements iPeerSocketListener {
  private static final long serialVersionUID = 2297825614322988606L;
  private JTable myTable = null;
  
  public PeerSocketPanel(){
    buildGUI();
  }
  
  private void buildGUI(){
    setLayout(  new BorderLayout() );
    
    myTable = new JTable(new PeerSocketTableModel(PeerSocketFactory.getInstance()));
    add(new JScrollPane(myTable));
    PeerSocketFactory.getInstance().addListener( this );
    setBorder( new TitledBorder("Cached peer sockets") );
  }

  @Override
  public void peerSocketsChanged( Map< String, Socket > aSocketMap ) {
    myTable.tableChanged( new TableModelEvent(myTable.getModel()) );
  }
  
}
