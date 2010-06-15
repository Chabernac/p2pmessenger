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

import org.apache.log4j.PropertyConfigurator;

import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolFactory;
import chabernac.protocol.ProtocolServer;
import chabernac.tools.PropertyMap;

public class RoutingFrame extends JFrame {

  private RoutingProtocol myRoutingProtocol = null;
  private ProtocolServer myProtocolServer = null;
  
  public RoutingFrame(ProtocolServer aServer, RoutingProtocol aRoutingProtocol){
    myRoutingProtocol = aRoutingProtocol;
    myProtocolServer = aServer;
    init();
    addListeners();
    buildGUI();
  }
  
  private void init(){
//    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
  }
  
  private void addListeners(){
    addWindowListener( new MyWindowListener() );
  }

  private void buildGUI() {
    setTitle( myRoutingProtocol.getLocalPeerId() );
    
    getContentPane().setLayout( new BorderLayout() );
    getContentPane().add(new RoutingPanel(myRoutingProtocol));
    setSize( 400, 400 );
  }
  
  public class MyWindowListener extends WindowAdapter {
    @Override
    public void windowClosing( WindowEvent anE ) {
      myProtocolServer.stop();
      System.exit( 0 );
    }
  }
  
  public static void main(String args[]) throws ProtocolException{
    PropertyConfigurator.configure( "log4j.properties" );
//    BasicConfigurator.configure();
    PropertyMap theProperties = new PropertyMap();
    theProperties.setProperty( "routingprotocol.exchangedelay", "60");
    theProperties.setProperty("routingprotocol.persist", "true");
    theProperties.setProperty("peerid", UUID.randomUUID().toString());
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    ProtocolContainer theContainer = new ProtocolContainer(theFactory);
    ProtocolServer theServer1 = new ProtocolServer(theContainer, RoutingProtocol.START_PORT, 5, true);
    theServer1.start();
    RoutingFrame theFrame = new RoutingFrame(theServer1, (RoutingProtocol)theContainer.getProtocol( RoutingProtocol.ID ));
    theFrame.setVisible( true );

  }
  
  
}
