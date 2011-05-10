/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class WebRoutingTableInspecterTest extends TestCase {
  public void testWebRoutingTablenispecter() throws UnknownPeerException, MalformedURLException{

    SessionData theSessionData = new SessionData();
    Map<String, String> thePeerExternalIpLink = new HashMap<String, String>();


    WebRoutingTableInspecter theInspector = new WebRoutingTableInspecter( theSessionData, thePeerExternalIpLink);

    RoutingTable theRoutingTable = new RoutingTable( "1" );

    theRoutingTable.addRoutingTableEntry( new RoutingTableEntry( new WebPeer( "1", new URL("http://localhost:80/p2p") ), 0, new WebPeer( "1", new URL("http://localhost:80/p2p") ), System.currentTimeMillis() ) );
    theRoutingTable.addRoutingTableEntry( new RoutingTableEntry( new SocketPeer( "2" ), 1, new SocketPeer( "2" ), System.currentTimeMillis() ) );
    theRoutingTable.addRoutingTableEntry( new RoutingTableEntry( new SocketPeer( "3" ), 1, new SocketPeer( "3" ), System.currentTimeMillis() ) );
    theRoutingTable.addRoutingTableEntry( new RoutingTableEntry( new SocketPeer( "4" ), 1, new SocketPeer( "4" ), System.currentTimeMillis() ) );
    theRoutingTable.addRoutingTableEntry( new RoutingTableEntry( new SocketPeer( "5" ), 2, new SocketPeer( "5" ), System.currentTimeMillis() ) );
    theRoutingTable.addRoutingTableEntry( new RoutingTableEntry( new SocketPeer( "6" ), 2, new SocketPeer( "4" ), System.currentTimeMillis() ) );

    thePeerExternalIpLink.put( "1", "192.168.0.1" );
    thePeerExternalIpLink.put( "2", "192.168.0.2" );
    thePeerExternalIpLink.put( "3", "192.168.0.2" );
    thePeerExternalIpLink.put( "4", "192.168.0.4" );

    theSessionData.putProperty( "session-1", "requestor.ip", "192.168.0.1" );
    theSessionData.putProperty( "session-2", "requestor.ip", "192.168.0.2" );
    theSessionData.putProperty( "session-3", "requestor.ip", "192.168.0.2" );
    theSessionData.putProperty( "session-4", "requestor.ip", "192.168.0.4" );


    RoutingTable theNewRoutingTable = theInspector.inspectRoutingTable( "session-2", theRoutingTable );

    assertTrue( theNewRoutingTable.containsEntryForPeer( "1" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "2" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "3" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "4" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "5" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "6" ) );

    assertTrue(  theNewRoutingTable.getEntryForPeer( "1" ).getPeer() instanceof WebPeer );
    assertTrue(  theNewRoutingTable.getEntryForPeer( "2" ).getPeer() instanceof SocketPeer);
    assertTrue(  theNewRoutingTable.getEntryForPeer( "3" ).getPeer() instanceof SocketPeer);
    assertTrue(  theNewRoutingTable.getEntryForPeer( "4" ).getPeer() instanceof IndirectReachablePeer);
    assertTrue(  theNewRoutingTable.getEntryForPeer( "5" ).getPeer() instanceof IndirectReachablePeer);
    assertTrue(  theNewRoutingTable.getEntryForPeer( "6" ).getPeer() instanceof IndirectReachablePeer);

    theNewRoutingTable = theInspector.inspectRoutingTable( "session-3", theRoutingTable );

    assertTrue( theNewRoutingTable.containsEntryForPeer( "1" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "2" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "3" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "4" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "5" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "6" ) );

    assertTrue(  theNewRoutingTable.getEntryForPeer( "1" ).getPeer() instanceof WebPeer );
    assertTrue(  theNewRoutingTable.getEntryForPeer( "2" ).getPeer() instanceof SocketPeer);
    assertTrue(  theNewRoutingTable.getEntryForPeer( "3" ).getPeer() instanceof SocketPeer);
    assertTrue(  theNewRoutingTable.getEntryForPeer( "4" ).getPeer() instanceof IndirectReachablePeer);
    assertTrue(  theNewRoutingTable.getEntryForPeer( "5" ).getPeer() instanceof IndirectReachablePeer);
    assertTrue(  theNewRoutingTable.getEntryForPeer( "6" ).getPeer() instanceof IndirectReachablePeer);

    theNewRoutingTable = theInspector.inspectRoutingTable( "session-4", theRoutingTable );

    assertTrue( theNewRoutingTable.containsEntryForPeer( "1" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "2" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "3" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "4" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "5" ) );
    assertTrue( theNewRoutingTable.containsEntryForPeer( "6" ) );

    assertTrue(  theNewRoutingTable.getEntryForPeer( "1" ).getPeer() instanceof WebPeer );
    assertTrue(  theNewRoutingTable.getEntryForPeer( "2" ).getPeer() instanceof IndirectReachablePeer);
    assertTrue(  theNewRoutingTable.getEntryForPeer( "3" ).getPeer() instanceof IndirectReachablePeer);
    assertTrue(  theNewRoutingTable.getEntryForPeer( "4" ).getPeer() instanceof SocketPeer);
    assertTrue(  theNewRoutingTable.getEntryForPeer( "5" ).getPeer() instanceof IndirectReachablePeer);
    assertTrue(  theNewRoutingTable.getEntryForPeer( "6" ).getPeer() instanceof IndirectReachablePeer);
  }
}
