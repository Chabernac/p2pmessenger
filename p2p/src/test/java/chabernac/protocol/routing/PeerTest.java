/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import chabernac.protocol.routing.SocketPeer;

public class PeerTest extends TestCase {
  public void testDetectLocalHost() throws SocketException, NoAvailableNetworkAdapterException{
    SocketPeer thePeer = new SocketPeer();
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
    SocketPeer thePeer1 = new SocketPeer("1", theHosts, 500);
    
    List<String> theHosts2 = new ArrayList< String >();
    theHosts2.add("192.168.1.3");
    theHosts2.add("192.168.1.4");
    SocketPeer thePeer2 = new SocketPeer("2", theHosts2, 500);
    
    assertTrue( thePeer1.isSameEndPointAs( thePeer2 ) );
    
    List<String> theHosts3 = new ArrayList< String >();
    theHosts3.add("192.168.1.3");
    theHosts3.add("192.168.1.4");
    SocketPeer thePeer3 = new SocketPeer("3", theHosts3, 501);
    
    assertFalse( thePeer3.isSameEndPointAs( thePeer2 ) );
    
    List<String> theHosts4 = new ArrayList< String >();
    theHosts4.add("192.168.1.5");
    theHosts4.add("192.168.1.6");
    SocketPeer thePeer4 = new SocketPeer("3", theHosts3, 500);
    
    assertFalse( thePeer4.isSameEndPointAs( thePeer3 ) );
    
  }
}
