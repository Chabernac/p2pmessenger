/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.routing;

import java.net.SocketException;
import java.util.List;

import junit.framework.TestCase;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;

public class RoutingTableTest extends TestCase {

  public void testRoutingTable(){
    Peer thePeer0 = new Peer(1, "localhost", 1001);
    RoutingTable theTable = new RoutingTable(thePeer0);

    Peer thePeer = new Peer(2, "localhost", 1002);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer);
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer, 2, thePeer);

    theTable.addRoutingTableEntry( theEntry2 );
    theTable.addRoutingTableEntry( theEntry );

    assertEquals( 1,  theTable.getEntries().size());

    assertEquals( theEntry, theTable.getEntries().get( 0 ) );

    Peer thePeer00 = new Peer(3, "localhost", 1003);
    RoutingTable theTable2 = new RoutingTable(thePeer00);
    Peer thePeer4 = new Peer(4, "x20d1148", 1004);
    RoutingTableEntry theEntry3 = new RoutingTableEntry(thePeer4, 1, thePeer4);
    theTable2.addRoutingTableEntry( theEntry3 );

    theTable.merge( theTable2 );


    assertEquals( 2,  theTable.getEntries().size());

    List< RoutingTableEntry > theEntries = theTable.getEntries();

    assertEquals( thePeer, theEntries.get(0).getGateway()); 
    assertEquals( 1, theEntries.get(0).getHopDistance());
    assertEquals( thePeer00, theEntries.get(1).getGateway());
    assertEquals( 2, theEntries.get(1).getHopDistance());
  }
  
  public void testRespondingEntry(){
    Peer thePeer0 = new Peer(1, "localhost", 1001);
    RoutingTable theTable = new RoutingTable(thePeer0);

    Peer thePeer = new Peer(2, "localhost", 1002);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer);
    theEntry.setResponding(false);
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer, 2, thePeer);
    theEntry2.setResponding(true);
    
    theTable.addRoutingTableEntry(theEntry);
    theTable.addRoutingTableEntry(theEntry2);
    
    assertEquals(1, theTable.getEntries().size());
    
    assertEquals(theEntry2, theTable.getEntries().get(0));
  }
  
  
  public void testSameEntryDifferentPort() throws SocketException{
    Peer thePeer0 = new Peer(1, 1001);
    RoutingTable theTable = new RoutingTable(thePeer0);

    Peer thePeer = new Peer(2, 1002);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer);
    theEntry.setResponding(false);
    
    Peer thePeer2 = new Peer(2, 1003);
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer2, 2, thePeer);
    theEntry2.setResponding(true);
    
    theTable.addRoutingTableEntry(theEntry);
    theTable.addRoutingTableEntry(theEntry2);
    
    assertEquals(1, theTable.getEntries().size());
    
    assertEquals(theEntry2, theTable.getEntries().get(0));
    assertEquals(1003, theTable.getEntries().get(0).getPeer().getPort());
  }
}
