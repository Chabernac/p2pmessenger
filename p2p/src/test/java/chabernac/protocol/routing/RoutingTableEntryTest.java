/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.FileNotFoundException;
import java.net.SocketException;

import junit.framework.TestCase;
import chabernac.io.DummyNetworkInterface;
import chabernac.protocol.routing.SocketPeer;
import chabernac.protocol.routing.RoutingTableEntry;

public class RoutingTableEntryTest extends TestCase {
  public void testRoutingTableEntry() throws FileNotFoundException, SocketException, NoAvailableNetworkAdapterException{
    SocketPeer thePeer = new SocketPeer("1", 1000);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer, System.currentTimeMillis(), 0, new DummyNetworkInterface());
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer, 2, thePeer, System.currentTimeMillis(), 0, new DummyNetworkInterface());
    
    assertTrue( theEntry.closerThen( theEntry2 ) );
  }
  
  public void testEquals(){
    DummyPeer thePeer1 = new DummyPeer( "1" );
    DummyPeer thePeer2 = new DummyPeer( "2" );
    DummyNetworkInterface theInterface1 = new DummyNetworkInterface();
    DummyNetworkInterface theInterface2 = new DummyNetworkInterface();
    RoutingTableEntry theEntry1 = new RoutingTableEntry( thePeer1, 1, thePeer1, System.currentTimeMillis(), 0, theInterface1); 
    
    assertEquals( theEntry1, new RoutingTableEntry( thePeer1, 1, thePeer1, System.currentTimeMillis(), 0, theInterface1) );
    assertFalse( theEntry1.equals( new RoutingTableEntry( thePeer1, 1, thePeer1, System.currentTimeMillis(), 0, theInterface2) ) );
    assertFalse( theEntry1.equals( new RoutingTableEntry( thePeer1, 2, thePeer1, System.currentTimeMillis(), 0, theInterface1) ) );
    assertFalse( theEntry1.equals( new RoutingTableEntry( thePeer2, 1, thePeer2, System.currentTimeMillis(), 0, theInterface1) ) );
  }
}

