/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.FileNotFoundException;
import java.net.SocketException;

import junit.framework.TestCase;
import chabernac.protocol.routing.SocketPeer;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.tools.DummyNetworkInterface;

public class RoutingTableEntryTest extends TestCase {
  public void testRoutingTableEntry() throws FileNotFoundException, SocketException, NoAvailableNetworkAdapterException{
    SocketPeer thePeer = new SocketPeer("1", 1000);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer, System.currentTimeMillis(), 0, new DummyNetworkInterface());
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer, 2, thePeer, System.currentTimeMillis(), 0, new DummyNetworkInterface());
    
    assertTrue( theEntry.closerThen( theEntry2 ) );
  }
}

