/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.util.Properties;

import junit.framework.TestCase;

public abstract class AbstractProtocolTest extends TestCase {
  public ProtocolContainer getProtocolContainer(long anExchangeDelay, boolean isPersist, String aPeerId){
    Properties theProperties = new Properties();
    theProperties.setProperty( "routingprotocol.exchangedelay", Long.toString( anExchangeDelay));
    theProperties.setProperty("routingprotocol.persist", Boolean.toString( isPersist));
    theProperties.setProperty("peerid", aPeerId);
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    return new ProtocolContainer(theFactory);
  }
  
  public ProtocolContainer getProtocolContainer(long anExchangeDelay, boolean isPersist, String aPeerId, String aKeyDir, String aUserName){
    Properties theProperties = new Properties();
    theProperties.setProperty( "routingprotocol.exchangedelay", Long.toString( anExchangeDelay));
    theProperties.setProperty("routingprotocol.persist", Boolean.toString( isPersist));
    theProperties.setProperty("peerid", aPeerId);
    theProperties.setProperty("key.location", aKeyDir);
    theProperties.setProperty("key.user", aUserName);
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    return new ProtocolContainer(theFactory);
  }
}
