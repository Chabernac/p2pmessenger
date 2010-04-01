/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.awt.BorderLayout;
import java.util.Properties;
import java.util.UUID;

import javax.swing.JFrame;

import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolFactory;
import chabernac.protocol.ProtocolServer;

public class RoutingFrame extends JFrame {
  private RoutingProtocol myRoutingProtocol = null;
  
  public RoutingFrame(RoutingProtocol aRoutingProtocol){
    myRoutingProtocol = aRoutingProtocol;
    init();
    buildGUI();
  }
  
  private void init(){
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
  }

  private void buildGUI() {
    setTitle( myRoutingProtocol.getLocalPeerId() );
    
    getContentPane().setLayout( new BorderLayout() );
    getContentPane().add(new RoutingPanel(myRoutingProtocol));
    setSize( 400, 400 );
  }
  
  public static void main(String args[]) throws ProtocolException{
    Properties theProperties = new Properties();
    theProperties.setProperty( "routingprotocol.exchangedelay", "10");
    theProperties.setProperty("routingprotocol.persist", "true");
    theProperties.setProperty("peerid", UUID.randomUUID().toString());
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    ProtocolContainer theContainer = new ProtocolContainer(theFactory);
    ProtocolServer theServer1 = new ProtocolServer(theContainer, RoutingProtocol.START_PORT, 5, true);
    theServer1.start();
    RoutingFrame theFrame = new RoutingFrame((RoutingProtocol)theContainer.getProtocol( RoutingProtocol.ID ));
    theFrame.setVisible( true );

  }
  
  
}
