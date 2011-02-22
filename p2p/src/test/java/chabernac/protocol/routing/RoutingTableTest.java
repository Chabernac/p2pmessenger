/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class RoutingTableTest extends TestCase {

  public void testRoutingTable() throws SocketException, NoAvailableNetworkAdapterException, UnknownPeerException{
    RoutingTable theTable = new RoutingTable("1");

    SocketPeer thePeer = new SocketPeer("2", "localhost", 1002);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer, System.currentTimeMillis());
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer, 2, thePeer, System.currentTimeMillis());

    theTable.addRoutingTableEntry( theEntry2 );
    theTable.addRoutingTableEntry( theEntry );

    assertEquals( 1,  theTable.getEntries().size());

    assertEquals( theEntry, theTable.getEntries().get( 0 ) );

    RoutingTable theTable2 = new RoutingTable("3");
    SocketPeer thePeer4 = new SocketPeer("4", "x20d1148", 1004);
    RoutingTableEntry theEntry4 = new RoutingTableEntry(thePeer4, 1, thePeer4, System.currentTimeMillis());
    theTable2.addRoutingTableEntry( theEntry4 );
    SocketPeer thePeer3 = new SocketPeer("3", "x20d1148", 1003);
    RoutingTableEntry theEntry3 = new RoutingTableEntry(thePeer3, 0, thePeer3, System.currentTimeMillis());
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

    SocketPeer thePeer = new SocketPeer("2", "localhost", 1002);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer, System.currentTimeMillis());

    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer, RoutingTableEntry.MAX_HOP_DISTANCE, thePeer, System.currentTimeMillis());

    assertFalse( theEntry2.isResponding() );

    theTable.addRoutingTableEntry(theEntry);
    theTable.addRoutingTableEntry(theEntry2);

    assertEquals(1, theTable.getEntries().size());

    //entry 2 will be kept because it contains the same gateway as the entry that is already there and so it is assumed that this is
    //the real situation
    assertEquals(theEntry2, theTable.getEntries().get(0));


    SocketPeer thePeer3 = new SocketPeer("3", "localhost", 1002);

    RoutingTableEntry theEntry3 = new RoutingTableEntry(thePeer, 3, thePeer3, System.currentTimeMillis());

    theTable.addRoutingTableEntry(theEntry3);

    assertEquals(theEntry3, theTable.getEntryForPeer( "2" ));
  }


  public void testSameEntryDifferentPort() throws SocketException, NoAvailableNetworkAdapterException{
    RoutingTable theTable = new RoutingTable("1");

    SocketPeer thePeer = new SocketPeer("2", 1002);
    RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, RoutingTableEntry.MAX_HOP_DISTANCE, thePeer, System.currentTimeMillis());

    SocketPeer thePeer2 = new SocketPeer("2", 1003);
    RoutingTableEntry theEntry2 = new RoutingTableEntry(thePeer2, 2, thePeer, System.currentTimeMillis());

    theTable.addRoutingTableEntry(theEntry);
    theTable.addRoutingTableEntry(theEntry2);

    assertEquals(1, theTable.getEntries().size());

    assertEquals(theEntry2, theTable.getEntries().get(0));
    assertEquals(1003, ((SocketPeer)theTable.getEntries().get(0).getPeer()).getPort());
  }

  public void testCopyWithoutUnreachablePeers() throws NoAvailableNetworkAdapterException{
    RoutingTable theRoutingTable = new RoutingTable("1");
    theRoutingTable.addEntry( new RoutingTableEntry(new SocketPeer("1",12800), 0, new SocketPeer("1",12800), System.currentTimeMillis() ));
    theRoutingTable.addEntry( new RoutingTableEntry(new SocketPeer("2",12801), 1, new SocketPeer("2",12801), System.currentTimeMillis() ));
    theRoutingTable.addEntry( new RoutingTableEntry(new SocketPeer("3",12802), 2, new SocketPeer("3",12802), System.currentTimeMillis() ));
    theRoutingTable.addEntry( new RoutingTableEntry(new SocketPeer("4",12803), 3, new SocketPeer("4",12803), System.currentTimeMillis() ));
    theRoutingTable.addEntry( new RoutingTableEntry(new SocketPeer("5",12804), 4, new SocketPeer("5",12804), System.currentTimeMillis() ));
    theRoutingTable.addEntry( new RoutingTableEntry(new SocketPeer("6",12805), 5, new SocketPeer("6",12805), System.currentTimeMillis() ));
    theRoutingTable.addEntry( new RoutingTableEntry(new SocketPeer("7",12806), 6, new SocketPeer("7",12806), System.currentTimeMillis() ));
    theRoutingTable.addEntry( new RoutingTableEntry(new SocketPeer("8",12807), 7, new SocketPeer("8",12807), System.currentTimeMillis() ));
    theRoutingTable.addEntry( new RoutingTableEntry(new SocketPeer("9",12808), 8, new SocketPeer("9",12808), System.currentTimeMillis() ));
    theRoutingTable.addEntry( new RoutingTableEntry(new SocketPeer("10",12809), 9, new SocketPeer("10",12809), System.currentTimeMillis() ));
    assertEquals( 10, theRoutingTable.getEntries().size());
    RoutingTable theCopyWithoutUnreachablePeers = theRoutingTable.copyWithoutUnreachablePeers(); 
    assertEquals( 6, theCopyWithoutUnreachablePeers.getEntries().size());

    for(int i=1;i<=6;i++){
      assertTrue( theCopyWithoutUnreachablePeers.containsEntryForPeer( Integer.toString( i ) ) );
    }
    for(int i=7;i<=10;i++){
      assertFalse( theCopyWithoutUnreachablePeers.containsEntryForPeer( Integer.toString( i ) ) );
    }
  }

  public void testGatewayNotReachable() throws NoAvailableNetworkAdapterException, UnknownPeerException{
    RoutingTable theRoutingTable = new RoutingTable("1");
    theRoutingTable.addRoutingTableEntry( new RoutingTableEntry(new SocketPeer("1",12800), 0, new SocketPeer("1",12800), System.currentTimeMillis() ));
    theRoutingTable.addRoutingTableEntry( new RoutingTableEntry(new SocketPeer("2",12801), 2, new SocketPeer("3",12801), System.currentTimeMillis() ));
    theRoutingTable.addRoutingTableEntry( new RoutingTableEntry(new SocketPeer("3",12802), 1, new SocketPeer("3",12802), System.currentTimeMillis() ));

    assertTrue( theRoutingTable.getEntryForPeer( "1" ).isReachable() );
    assertTrue( theRoutingTable.getEntryForPeer( "2" ).isReachable() );
    assertTrue( theRoutingTable.getEntryForPeer( "3" ).isReachable() );

    theRoutingTable.addRoutingTableEntry( new RoutingTableEntry(new SocketPeer("3",12802), RoutingTableEntry.MAX_HOP_DISTANCE, new SocketPeer("3",12802), System.currentTimeMillis() ));

    assertTrue( theRoutingTable.getEntryForPeer( "1" ).isReachable() );
    //entry 2 must not be available because entry 3 is the gateway for entry 2 and entry 3 is not reachable
    assertFalse( theRoutingTable.getEntryForPeer( "2" ).isReachable() );
    assertFalse( theRoutingTable.getEntryForPeer( "3" ).isReachable() );
  }

  public void testRemoveEntriesOlderThan() throws NoAvailableNetworkAdapterException{
    RoutingTable theRoutingTable = new RoutingTable("1");
    SimpleDateFormat theFormat = new SimpleDateFormat("dd/MM/yyyy");

    for(int i=0;i<5;i++){
      GregorianCalendar theCalendar = new GregorianCalendar();
      theCalendar.add( GregorianCalendar.DAY_OF_MONTH, -i );
      System.out.println("Date: " + theFormat.format( theCalendar.getTime() ));
      

      theRoutingTable.addEntry(  new RoutingTableEntry(new SocketPeer(Integer.toString( i ),12800 + i), RoutingTableEntry.MAX_HOP_DISTANCE, new SocketPeer(Integer.toString(i),12800 + i), theCalendar.getTimeInMillis() ));
    }
    
    //the are 5 items in the routingtable
    assertEquals( 5, theRoutingTable.getEntries().size() );
    
    //5 item must remain
    theRoutingTable.removeEntriesOlderThan( 5, TimeUnit.DAYS );
    assertEquals( 5, theRoutingTable.getEntries().size() );
    
    
    theRoutingTable.removeEntriesOlderThan( 4, TimeUnit.DAYS );
    assertEquals( 4, theRoutingTable.getEntries().size() );
    
    theRoutingTable.removeEntriesOlderThan( 3, TimeUnit.DAYS );
    assertEquals( 3, theRoutingTable.getEntries().size() );
    
    theRoutingTable.removeEntriesOlderThan( 2, TimeUnit.DAYS );
    assertEquals( 2, theRoutingTable.getEntries().size() );
    
    theRoutingTable.removeEntriesOlderThan( 1, TimeUnit.DAYS );
    assertEquals( 1, theRoutingTable.getEntries().size() );
    
    theRoutingTable.removeEntriesOlderThan( 0, TimeUnit.DAYS );
    assertEquals( 0, theRoutingTable.getEntries().size() );
  }
}
