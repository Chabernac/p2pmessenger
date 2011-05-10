/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import chabernac.p2p.settings.P2PSettings;
import chabernac.tools.PropertyMap;

public abstract class AbstractProtocolTest extends TestCase {
  protected static final int SLEEP_AFTER_SCAN = 2000;
  
  public void setUp(){
    P2PSettings.getInstance().getSocketPool().cleanUp();
  }
  
  public ProtocolContainer getProtocolContainer(long anExchangeDelay, boolean isPersist, String aPeerId){
    PropertyMap theProperties = new PropertyMap();
    theProperties.setProperty( "routingprotocol.exchangedelay", Long.toString( anExchangeDelay));
    theProperties.setProperty("routingprotocol.persist", Boolean.toString( isPersist));
    
    if(aPeerId != null) theProperties.setProperty("peerid", aPeerId);
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    return new ProtocolContainer(theFactory);
  }
  
  public ProtocolContainer getProtocolContainer(long anExchangeDelay, boolean isPersist, String aPeerId, String... aSuperNodes){
    PropertyMap theProperties = new PropertyMap();
    theProperties.setProperty( "routingprotocol.exchangedelay", Long.toString( anExchangeDelay));
    theProperties.setProperty("routingprotocol.persist", Boolean.toString( isPersist));
    
    List<String> theSuperNodes = new ArrayList<String>();
    for(String theSuperNode : aSuperNodes) theSuperNodes.add(theSuperNode);
    theProperties.setProperty("routingprotocol.supernodes", theSuperNodes );
    
    if(aPeerId != null) theProperties.setProperty("peerid", aPeerId);
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    return new ProtocolContainer(theFactory);
  }

}
