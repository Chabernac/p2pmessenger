/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.routing;

import java.net.SocketException;
import java.util.List;

import junit.framework.TestCase;
import chabernac.protocol.routing.Peer;

public class PeerTest extends TestCase {
  public void testDetectLocalHost() throws SocketException{
    Peer thePeer = new Peer();
    thePeer.detectLocalInterfaces();
    assertTrue( 0 < thePeer.getHosts().size());
    
    List<String> theHosts = thePeer.getHosts();
    for(String theHost: theHosts){
      assertFalse( "localhost".equals( theHost ));
    }
  }
}
