/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.SocketException;

import junit.framework.TestCase;

public class RoutingTableTest extends TestCase {

  public void testRoutingTable() throws SocketException, NoAvailableNetworkAdapterException, UnknownPeerException{
    RoutingTable theTable = new RoutingTable("1");

    Peer thePeer = new Peer("2", "localhost", 1002);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer);
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer, 2, thePeer);

    theTable.addRoutingTableEntry( theEntry2 );
    theTable.addRoutingTableEntry( theEntry );

    assertEquals( 1,  theTable.getEntries().size());

    assertEquals( theEntry, theTable.getEntries().get( 0 ) );

    RoutingTable theTable2 = new RoutingTable("3");
    Peer thePeer4 = new Peer("4", "x20d1148", 1004);
    RoutingTableEntry theEntry4 = new RoutingTableEntry(thePeer4, 1, thePeer4);
    theTable2.addRoutingTableEntry( theEntry4 );
    Peer thePeer3 = new Peer("3", "x20d1148", 1003);
    RoutingTableEntry theEntry3 = new RoutingTableEntry(thePeer3, 0, thePeer3);
    theTable2.addRoutingTableEntry( theEntry3 );

    theTable.merge( theTable2 );


    assertEquals( 3,  theTable.getEntries().size());

    assertEquals( thePeer, theTable.getEntryForPeer( "2" ).getPeer()); 
    assertEquals( 1, theTable.getEntryForPeer( "2" ).getHopDistance());
    assertEquals( "3", theTable.getEntryForPeer( "4" ).getGateway().getPeerId());
    assertEquals( 2, theTable.getEntryForPeer( "4" ).getHopDistance());
  }
  
  public void testRespondingEntry() throws UnknownPeerException{
    RoutingTable theTable = new RoutingTable("1");

    Peer thePeer = new Peer("2", "localhost", 1002);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer);
    
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer, RoutingTableEntry.MAX_HOP_DISTANCE, thePeer);
    
    assertFalse( theEntry2.isResponding() );
    
    theTable.addRoutingTableEntry(theEntry);
    theTable.addRoutingTableEntry(theEntry2);
    
    assertEquals(1, theTable.getEntries().size());
    
    //entry 2 will be kept because it contains the same gateway as the entry that is already there and so it is assumed that this is
    //the real situation
    assertEquals(theEntry2, theTable.getEntries().get(0));
    
    
    Peer thePeer3 = new Peer("3", "localhost", 1002);
    
    RoutingTableEntry theEntry3 = new RoutingTableEntry(thePeer, 3, thePeer3);
    
    theTable.addRoutingTableEntry(theEntry3);
    
    assertEquals(theEntry3, theTable.getEntryForPeer( "2" ));
  }
  
  
  public void testSameEntryDifferentPort() throws SocketException, NoAvailableNetworkAdapterException{
    RoutingTable theTable = new RoutingTable("1");

    Peer thePeer = new Peer("2", 1002);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, RoutingTableEntry.MAX_HOP_DISTANCE, thePeer);
    
    Peer thePeer2 = new Peer("2", 1003);
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer2, 2, thePeer);
    
    theTable.addRoutingTableEntry(theEntry);
    theTable.addRoutingTableEntry(theEntry2);
    
    assertEquals(1, theTable.getEntries().size());
    
    assertEquals(theEntry2, theTable.getEntries().get(0));
    assertEquals(1003, theTable.getEntries().get(0).getPeer().getPort());
  }
}
