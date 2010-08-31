/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.log4j.BasicConfigurator;

import chabernac.io.CachingSocketPool;
import chabernac.io.SocketPoolFactory;
import chabernac.io.SocketPoolPanel;
import chabernac.io.SocketProxy;
import chabernac.io.iSocketPool;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolFactory;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.message.MessagePanel;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.userinfo.UserInfoPanel;
import chabernac.protocol.userinfo.UserInfoProtocol;
import chabernac.tools.PropertyMap;
import chabernac.util.concurrent.MonitorPanel;

public class RoutingFrame extends JFrame {
  private static final long serialVersionUID = 6198311092838546976L;
  
  private ProtocolContainer myProtocolContainer = null;
  private ProtocolServer myProtocolServer = null;
  private boolean isStopOnClose;

  public RoutingFrame(ProtocolServer aServer, ProtocolContainer aContainer, boolean isStopOnClose) throws ProtocolException{
    myProtocolContainer = aContainer;
    myProtocolServer = aServer;
    this.isStopOnClose = isStopOnClose;
    init();
    addListeners();
    buildGUI();
  }

  private void init() throws ProtocolException{
    setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE);
    getRoutingTable().setKeepHistory(true);
    SocketProxy.setTraceEnabled(true);
    ((MessageProtocol)myProtocolContainer.getProtocol( MessageProtocol.ID )).setKeepHistory( true );
  }

  private void addListeners(){
    addWindowListener( new MyWindowListener() );
  }

  private void buildGUI() throws ProtocolException {
    RoutingProtocol theRoutingProtocl = (RoutingProtocol)myProtocolContainer.getProtocol( RoutingProtocol.ID );
    setTitle( theRoutingProtocl.getLocalPeerId() );
    
    JTabbedPane thePane = new JTabbedPane();
    thePane.add("Routing Table",  new RoutingPanel(theRoutingProtocl) );
    thePane.add("Sockets", new SocketPoolPanel() );

    thePane.add("User Info", new UserInfoPanel((UserInfoProtocol)myProtocolContainer.getProtocol( UserInfoProtocol.ID )));
    MonitorPanel theMonitorPanel = new MonitorPanel();
    myProtocolServer.setRunnableListener( theMonitorPanel );
    thePane.add("Threads", theMonitorPanel);
    
    MessageProtocol theMessageProtocol = (MessageProtocol)myProtocolContainer.getProtocol( MessageProtocol.ID );
    thePane.add("Messages", new MessagePanel(theMessageProtocol));
    

    getContentPane().setLayout( new BorderLayout() );
    getContentPane().add( thePane, BorderLayout.CENTER );
    

    setSize( 1200, 700 );
  }

  private RoutingTable getRoutingTable(){
    try {
      return ((RoutingProtocol)myProtocolContainer.getProtocol( RoutingProtocol.ID )).getRoutingTable();
    } catch (ProtocolException e) {
      return null;
    }
  }

  public class MyWindowListener extends WindowAdapter {
    @Override
    public void windowClosing( WindowEvent anE ) {
      getRoutingTable().setKeepHistory(false);
      getRoutingTable().clearHistory();
      SocketProxy.setTraceEnabled(false);
      try {
        ((MessageProtocol)myProtocolContainer.getProtocol( MessageProtocol.ID )).setKeepHistory( false );
      } catch ( ProtocolException e ) {
      }

      if(isStopOnClose){
        myProtocolServer.stop();
        System.exit( 0 );
      }
    }
  }

  public static void main(String args[]) throws ProtocolException{
    //    PropertyConfigurator.configure( "log4j.properties" );
    BasicConfigurator.configure();
    PropertyMap theProperties = new PropertyMap();
    theProperties.setProperty( "routingprotocol.exchangedelay", "60");
    theProperties.setProperty("routingprotocol.persist", "true");
    theProperties.setProperty("peerid", UUID.randomUUID().toString());
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    ProtocolContainer theContainer = new ProtocolContainer(theFactory);
    ProtocolServer theServer1 = new ProtocolServer(theContainer, RoutingProtocol.START_PORT, 20, true);
    RoutingFrame theFrame = new RoutingFrame(theServer1, theContainer, true);
    theServer1.start();
    theFrame.setVisible( true );
    
    
    iSocketPool<SocketProxy> theSocketPool = SocketPoolFactory.getSocketPool();
    if(theSocketPool instanceof CachingSocketPool){
      ((CachingSocketPool)theSocketPool).setCleanUpTimeInSeconds( 30 );
    }
  }


}
