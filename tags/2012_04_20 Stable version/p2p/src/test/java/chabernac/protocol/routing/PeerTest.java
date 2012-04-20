/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.SocketException;
import java.util.List;

import junit.framework.TestCase;
import chabernac.tools.SimpleNetworkInterface;

public class PeerTest extends TestCase {
  public void testDetectLocalHost() throws SocketException, NoAvailableNetworkAdapterException{
    SocketPeer thePeer = new SocketPeer();
    thePeer.detectLocalInterfaces();
    assertTrue( 0 < thePeer.getHosts().size());
    
    List<SimpleNetworkInterface> theHosts = thePeer.getHosts();
    for(SimpleNetworkInterface theHost: theHosts){
      assertFalse( "localhost".equals( theHost.getIp().get(0)));
    }
  }
  
  public void testSameHosts(){
    SocketPeer thePeer1 = new SocketPeer("1", 500, "192.168.1.2", "192.168.1.3");
    SocketPeer thePeer2 = new SocketPeer("2", 500, "192.168.1.3", "192.168.1.4");
    assertTrue( thePeer1.isSameEndPointAs( thePeer2 ) );
    
    SocketPeer thePeer3 = new SocketPeer("3", 501, "192.168.1.3", "192.168.1.4");
    assertFalse( thePeer3.isSameEndPointAs( thePeer2 ) );
    
    SocketPeer thePeer4 = new SocketPeer("3", 500, "192.168.1.5", "192.168.1.6");
    assertFalse( thePeer4.isSameEndPointAs( thePeer3 ) );
  }
  
  public void testSameHostWithMAC(){
    SocketPeer thePeer1 = new SocketPeer("1", new SimpleNetworkInterface(new byte[]{0,1,2,3}, "192.168.1.2", "192.168.1.3"), 500);
    SocketPeer thePeer2 = new SocketPeer("2", new SimpleNetworkInterface(new byte[]{0,1,2,3}, "192.168.1.4", "192.168.1.5" ), 500);
    SocketPeer thePeer3 = new SocketPeer("3", new SimpleNetworkInterface(new byte[]{4,1,2,3}, "192.168.1.2"), 500);
    
    assertFalse( thePeer1.isSameEndPointAs( thePeer3 ) );
    assertFalse( thePeer2.isSameEndPointAs( thePeer3 ) );
  }
}
