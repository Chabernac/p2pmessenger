/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.FileNotFoundException;
import java.net.SocketException;

import junit.framework.TestCase;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingTableEntry;

public class RoutingTableEntryTest extends TestCase {
  public void testRoutingTableEntry() throws FileNotFoundException, SocketException{
    Peer thePeer = new Peer(1, 1000);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer);
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer, 2, thePeer);
    
    assertTrue( theEntry.closerThen( theEntry2 ) );
  }
}

