/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import junit.framework.TestCase;
import chabernac.protocol.ProtocolServer;
import chabernac.tools.DummyNetworkInterface;
import chabernac.tools.SimpleNetworkInterface;

public class SocketRoutingTableInspectorTest extends TestCase {
  public void testSocketRoutingTableInspector() throws UnknownPeerException{
    SessionData theSessionData = new SessionData();
    
    RoutingTable theRoutingTable = new RoutingTable( "0" );
    RoutingTableEntry theLocalEntry = new RoutingTableEntry( new SocketPeer( "4", SimpleNetworkInterface.createFromIpList( "192.168.1.4" ), 8000), new DummyNetworkInterface() );
    theLocalEntry = theLocalEntry.setHopDistance( 0 );
    theRoutingTable.addEntry( theLocalEntry );
    theRoutingTable.addEntry( new RoutingTableEntry( new SocketPeer( "1", SimpleNetworkInterface.createFromIpList( "192.168.1.1" ), 8000), new DummyNetworkInterface() ) );
    theRoutingTable.addEntry( new RoutingTableEntry( new SocketPeer( "2", SimpleNetworkInterface.createFromIpList( "192.168.1.2", "10.0.0.1/23" ), 8000) , new DummyNetworkInterface() ));
    theRoutingTable.addEntry( new RoutingTableEntry( new SocketPeer( "3", SimpleNetworkInterface.createFromIpList( "192.168.1.3" ), 8000), new DummyNetworkInterface() ));
    
    SocketRoutingTableInspector theSocketRoutingTableInspector = new SocketRoutingTableInspector( theSessionData );
    
    theSessionData.putProperty( "session1", ProtocolServer.REMOTE_IP, "10.0.0.2/23" );
    
    RoutingTable theNewRoutingTable = theSocketRoutingTableInspector.inspectRoutingTable( "session1", theRoutingTable );
    
    assertEquals( 4, theNewRoutingTable.getEntries().size() );
    
    assertTrue( theNewRoutingTable.getEntryForPeer( "1" ).getPeer() instanceof IndirectReachablePeer );
    assertTrue( theNewRoutingTable.getEntryForPeer( "2" ).getPeer() instanceof SocketPeer );
    assertTrue( theNewRoutingTable.getEntryForPeer( "3" ).getPeer() instanceof IndirectReachablePeer );
    assertTrue( theNewRoutingTable.getEntryForPeer( "4" ).getPeer() instanceof SocketPeer );
    
    SocketPeer thePeer2 = (SocketPeer)theNewRoutingTable.getEntryForPeer( "2" ).getPeer();
    assertEquals( 1, thePeer2.getHosts().size() );
    assertEquals( 1, thePeer2.getHosts().get(0).getIp().size());
    assertEquals( "10.0.0.1/23", thePeer2.getHosts().get(0).getIp().get( 0 ));
  }
  
}
