/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.list.ListProtocol;
import chabernac.protocol.ping.PingProtocol;
import chabernac.tools.PropertyMap;

public class MasterProtocolTest extends TestCase {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  
  public void testMasterProtocol() throws ProtocolException{
    ProtocolContainer theMasterProtocol = new ProtocolContainer(new ProtocolFactory(new PropertyMap()));
    
    assertEquals( PingProtocol.Response.PONG.name(), new String(theMasterProtocol.handleCommand( "0", PingProtocol.ID + "ping") ));
    assertEquals( PingProtocol.Response.UNKNOWN_COMMAND.name(), new String(theMasterProtocol.handleCommand( "0", PingProtocol.ID + "somtehing") ));
    
//    assertEquals( "Master protocol" , theMasterProtocol.getDescription());
   
    String theResult = new String(theMasterProtocol.handleCommand( "0", ListProtocol.ID + "protocols" ));
    assertEquals( "LTP;PPG;MAS;", theResult );
  }
}
