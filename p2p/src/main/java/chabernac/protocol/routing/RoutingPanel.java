/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

public class RoutingPanel extends JPanel {
  private static final long serialVersionUID = 8719080293187156681L;
  private RoutingProtocol myRoutingProtocol = null;
  private ExecutorService myExecutorService = Executors.newFixedThreadPool( 10 );
  private JTextArea myInfoArea = new JTextArea();
  
  public RoutingPanel(RoutingProtocol aRoutingProtocol){
    myRoutingProtocol = aRoutingProtocol;
    buildGUI();
  }
  
  private void buildGUI(){
    setLayout( new BorderLayout() );
    buildNorthPanel();
    buildCenterPanel();
    buildSouthPanel();
    addListeners();
    setBorder( new TitledBorder("Routing Table") );
  }
  
  private void addListeners(){
    myRoutingProtocol.setRoutingProtocolMonitor( new RoutingProtocolMonitor() );
  }

  private void buildSouthPanel() {
    JScrollPane theScrollPane = new JScrollPane(myInfoArea);
    myInfoArea.setRows( 10 );
    add(theScrollPane, BorderLayout.SOUTH);
  }

  private void buildCenterPanel() {
    JScrollPane theScrollPane = new JScrollPane(new JTable(new RoutingTableModel(myRoutingProtocol.getRoutingTable())));
    add(theScrollPane, BorderLayout.CENTER);
  }

  private void buildNorthPanel() {
    JPanel theButtonPanel = new JPanel();
    theButtonPanel.setLayout( new GridLayout(-1,3) );
    
    theButtonPanel.add( new JButton(new StartLocalSystemScan()) );
    theButtonPanel.add( new JButton(new StartRemoteSystemScan()) );
    theButtonPanel.add( new JButton(new DetectRemoteSystem()) );
    theButtonPanel.add( new JButton(new ExchangeRoutingTable()) );
    theButtonPanel.add( new JButton(new SendUDPAnnouncement()) );
    theButtonPanel.add( new JButton(new StopAction()) );
    theButtonPanel.add( new JButton(new StartAction()) );
    theButtonPanel.add( new JButton(new ScanSuperNodesAction()) );
    
    add(theButtonPanel, BorderLayout.NORTH);
  }
  
  public class StartLocalSystemScan  extends AbstractAction{
    public StartLocalSystemScan(){
      putValue( Action.NAME, "Local scan" );
    }
    
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myExecutorService.execute( new Runnable(){
        public void run(){
          myRoutingProtocol.scanLocalSystem();
        }
      });
    }
  }  
  
  public class StartRemoteSystemScan  extends AbstractAction{
    public StartRemoteSystemScan(){
      putValue( Action.NAME, "remote scan" );
    }
    
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myExecutorService.execute( new Runnable(){
        public void run(){
          myRoutingProtocol.scanRemoteSystem( true );
        }
      });
    }
  }
  
  public class SendUDPAnnouncement  extends AbstractAction{
    public SendUDPAnnouncement(){
      putValue( Action.NAME, "send UDP announcement" );
    }
    
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myExecutorService.execute( new Runnable(){
        public void run(){
          myRoutingProtocol.sendUDPAnnouncement();
        }
      });
    }
  }
  
  public class ExchangeRoutingTable  extends AbstractAction{
    public ExchangeRoutingTable(){
      putValue( Action.NAME, "Exchange routing table" );
    }
    
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myExecutorService.execute( new Runnable(){
        public void run(){
          myRoutingProtocol.exchangeRoutingTable();
        }
      });
    }
  }
  
  public class DetectRemoteSystem  extends AbstractAction{
    public DetectRemoteSystem(){
      putValue( Action.NAME, "Detect remote system" );
    }
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myExecutorService.execute( new Runnable(){
        public void run(){
          myRoutingProtocol.detectRemoteSystem();
        }
      });
    }
  }
  
  public class StopAction  extends AbstractAction{
    public StopAction(){
      putValue( Action.NAME, "Stop" );
    }
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myExecutorService.execute( new Runnable(){
        public void run(){
          myRoutingProtocol.stop();
        }
      });
    }
  }
  
  public class StartAction  extends AbstractAction{
    public StartAction(){
      putValue( Action.NAME, "Start" );
    }
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myExecutorService.execute( new Runnable(){
        public void run(){
          myRoutingProtocol.start();
        }
      });
    }
  }
  
  public class ScanSuperNodesAction  extends AbstractAction{
    public ScanSuperNodesAction(){
      putValue( Action.NAME, "Scan super nodes" );
    }
    @Override
    public void actionPerformed( ActionEvent anE ) {
      myExecutorService.execute( new Runnable(){
        public void run(){
          myRoutingProtocol.scanSuperNodes();
        }
      });
    }
  }
  
  private class RoutingProtocolMonitor implements IRoutingProtocolMonitor{
    private StringBuilder myStringBuilder = new StringBuilder();

    @Override
    public void detectingRemoteSystemStarted() {
      myStringBuilder.insert(0, "Detecting remote system\r\n" );
      myInfoArea.setText( myStringBuilder.toString() );
    }

    @Override
    public void exchangingRoutingTables() {
      myStringBuilder.insert(0, "Exchanging routing tables\r\n" );
      myInfoArea.setText( myStringBuilder.toString() );
    }

    @Override
    public void localSystemScanStarted() {
      myStringBuilder.insert(0, "Scanning local system\r\n" );
      myInfoArea.setText( myStringBuilder.toString() );
    }

    @Override
    public void remoteSystemScanStarted() {
      myStringBuilder.insert(0, "Scanning remote system\r\n" );
      myInfoArea.setText( myStringBuilder.toString() );
    }

    @Override
    public void scanStarted( Peer aPeer ) {
//      myStringBuilder.append( "Scan started '" + aPeer.getHosts() + ":" + aPeer.getPort() + "\r\n" );
//      myInfoArea.setText( myStringBuilder.toString() );
    }

    @Override
    public void peerFoundWithScan( Peer aPeer ) {
      myStringBuilder.insert(0, "Peer found after scan '" + aPeer.getHosts() + ":" + aPeer.getPort() + "\r\n" );
      myInfoArea.setText( myStringBuilder.toString() );
    }

    @Override
    public void sendingUDPAnnouncement() {
      myStringBuilder.insert(0, "Sending udp announcement\r\n" );
      myInfoArea.setText( myStringBuilder.toString() );
      
    }

    @Override
    public void scanningSuperNodes() {
      myStringBuilder.insert(0, "Scanning super nodes\r\n" );
      myInfoArea.setText( myStringBuilder.toString() );

    }
    
  }

}
