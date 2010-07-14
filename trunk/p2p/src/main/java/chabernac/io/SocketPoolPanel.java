/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.awt.BorderLayout;
import java.net.Socket;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;

public class SocketPoolPanel extends JPanel implements Observer {
  private static final long serialVersionUID = 2297825614322988606L;
  private JTable myTable = null;
  
  public SocketPoolPanel(){
    buildGUI();
  }
  
  private void buildGUI(){
    setLayout(  new BorderLayout() );
    
    myTable = new JTable(new SocketPoolModel(SocketPool.getInstance( -1 )));
    add(new JScrollPane(myTable));
    SocketPool.getInstance( -1 ).addObserver( this );
    setBorder( new TitledBorder("Cached sockets") );
  }

  @Override
  public void update( Observable anO, Object anArg ) {
    myTable.tableChanged( new TableModelEvent(myTable.getModel()) );
  }
}
