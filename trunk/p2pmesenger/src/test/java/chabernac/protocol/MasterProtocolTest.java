/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.ping.PingProtocol;

public class MasterProtocolTest extends TestCase {
  public void setUp(){
    BasicConfigurator.configure();
  }
  
  
  public void testMasterProtocol(){
    ProtocolContainer theMasterProtocol = new ProtocolContainer();
    PingProtocol thePingProtocol = new PingProtocol();
    theMasterProtocol.addProtocol(  thePingProtocol );
    
    assertEquals( PingProtocol.Response.PONG.name(), new String(theMasterProtocol.handleCommand( 0, thePingProtocol.getId() + "ping") ));
    assertEquals( PingProtocol.Response.UNKNOWN_COMMAND.name(), new String(theMasterProtocol.handleCommand( 0, thePingProtocol.getId() + "somtehing") ));
    
//    assertEquals( "Master protocol" , theMasterProtocol.getDescription());
   
    String theResult = new String(theMasterProtocol.handleCommand( 0, "protocols" ));
    assertEquals( "PPG;MAS;", theResult );
        
  }
}
