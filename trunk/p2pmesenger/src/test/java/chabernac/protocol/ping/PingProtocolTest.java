/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.ping;

import junit.framework.TestCase;
import chabernac.protocol.ping.PingProtocol;
import chabernac.protocol.ping.PingProtocol.Command;
import chabernac.protocol.ping.PingProtocol.Response;

public class PingProtocolTest extends TestCase {
  public void testPingProtocol(){
    PingProtocol theProtocol = new PingProtocol();
    
    assertEquals( Response.PONG.name(), new String(theProtocol.handleCommand( 0, Command.PING.name()) ));
    assertEquals( Response.UNKNOWN_COMMAND.name(), new String(theProtocol.handleCommand( 0, "somtehing") ));
    
    assertEquals( "Ping protocol" , theProtocol.getDescription());
  }
}
