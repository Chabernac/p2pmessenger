/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import org.apache.log4j.BasicConfigurator;

import junit.framework.TestCase;

public class MasterProtocolTest extends TestCase {
  public void setUp(){
    BasicConfigurator.configure();
  }
  
  
  public void testMasterProtocol(){
    MasterProtocol theMasterProtocol = new MasterProtocol();
    PingProtocol thePingProtocol = new PingProtocol();
    theMasterProtocol.addSubProtocol( thePingProtocol );
    
    assertEquals( "pong", new String(theMasterProtocol.handle( 0, thePingProtocol.getId() + "ping") ));
    assertEquals( "unknwown command", new String(theMasterProtocol.handle( 0, thePingProtocol.getId() + "somtehing") ));
    
    assertEquals( "Master protocol" , theMasterProtocol.getDescription());
   
    String theResult = new String(theMasterProtocol.handle( 0, "protocols" ));
    assertEquals( "PIN:Ping protocol|", theResult );
        
  }
}
