/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import javax.activation.DataSource;

import junit.framework.TestCase;
import chabernac.io.SocketPoolFactory;
import chabernac.protocol.routing.PeerSender;
import chabernac.tools.PropertyMap;

public abstract class AbstractProtocolTest extends TestCase {
  protected static final int SLEEP_AFTER_SCAN = 2000;
  
  public void setUp(){
    SocketPoolFactory.getSocketPool().cleanUp();
  }
  
  public ProtocolContainer getProtocolContainer(long anExchangeDelay, boolean isPersist, String aPeerId){
    PropertyMap theProperties = new PropertyMap();
    theProperties.setProperty( "routingprotocol.exchangedelay", Long.toString( anExchangeDelay));
    theProperties.setProperty("routingprotocol.persist", Boolean.toString( isPersist));
    theProperties.setProperty("routingprotocol.peersender", new PeerSender());
    
    if(aPeerId != null) theProperties.setProperty("peerid", aPeerId);
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    return new ProtocolContainer(theFactory);
  }
  
  public ProtocolContainer getProtocolContainer(long anExchangeDelay, boolean isPersist, String aPeerId, DataSource aSuperNodesDataSource){
    PropertyMap theProperties = new PropertyMap();
    theProperties.setProperty( "routingprotocol.exchangedelay", Long.toString( anExchangeDelay));
    theProperties.setProperty("routingprotocol.persist", Boolean.toString( isPersist));
    theProperties.setProperty("routingprotocol.peersender", new PeerSender());
    theProperties.setProperty("routingprotocol.supernodes", aSuperNodesDataSource );
    
    if(aPeerId != null) theProperties.setProperty("peerid", aPeerId);
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    return new ProtocolContainer(theFactory);
  }

}
