/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import junit.framework.TestCase;

public class PingProtocolTest extends TestCase {
  public void testPingProtocol(){
    PingProtocol theProtocol = new PingProtocol();
    
    assertEquals( "pong", new String(theProtocol.handle( 0, "ping") ));
    assertEquals( "unknwown command", new String(theProtocol.handle( 0, "somtehing") ));
    
    assertEquals( "Ping protocol" , theProtocol.getDescription());
  }
}
