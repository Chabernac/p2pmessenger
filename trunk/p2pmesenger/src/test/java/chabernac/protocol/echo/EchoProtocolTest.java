package chabernac.protocol.echo;
import java.util.Properties;

import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolFactory;
import chabernac.protocol.echo.EchoProtocol;
import chabernac.tools.PropertyMap;
import junit.framework.TestCase;

/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */

public class EchoProtocolTest extends TestCase {
    public void testEchoProtocol(){
      EchoProtocol theProtocol = new EchoProtocol();
      assertEquals( "test", theProtocol.handleCommand( 0, "test" ));
    }
    
    public void testEchoProtocolInContainer(){
      ProtocolContainer theContainer = new ProtocolContainer(new ProtocolFactory(new PropertyMap()));
      
      assertEquals( "test", theContainer.handleCommand( 0, "ECOtest" ));
    }
    
}
