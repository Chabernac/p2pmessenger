/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import chabernac.protocol.routing.Peer;

public class PeerTest extends TestCase {
  public void testDetectLocalHost() throws SocketException, NoAvailableNetworkAdapterException{
    Peer thePeer = new Peer();
    thePeer.detectLocalInterfaces();
    assertTrue( 0 < thePeer.getHosts().size());
    
    List<String> theHosts = thePeer.getHosts();
    for(String theHost: theHosts){
      assertFalse( "localhost".equals( theHost ));
    }
  }
  
  public void testSameHosts(){
    List<String> theHosts = new ArrayList< String >();
    theHosts.add("192.168.1.2");
    theHosts.add("192.168.1.3");
    Peer thePeer1 = new Peer("1", theHosts, 500);
    
    List<String> theHosts2 = new ArrayList< String >();
    theHosts2.add("192.168.1.3");
    theHosts2.add("192.168.1.4");
    Peer thePeer2 = new Peer("2", theHosts2, 500);
    
    assertTrue( thePeer1.isSameHostAndPort( thePeer2 ) );
    
    List<String> theHosts3 = new ArrayList< String >();
    theHosts3.add("192.168.1.3");
    theHosts3.add("192.168.1.4");
    Peer thePeer3 = new Peer("3", theHosts3, 501);
    
    assertFalse( thePeer3.isSameHostAndPort( thePeer2 ) );
    
    List<String> theHosts4 = new ArrayList< String >();
    theHosts4.add("192.168.1.5");
    theHosts4.add("192.168.1.6");
    Peer thePeer4 = new Peer("3", theHosts3, 500);
    
    assertFalse( thePeer4.isSameHostAndPort( thePeer3 ) );
    
  }
}
