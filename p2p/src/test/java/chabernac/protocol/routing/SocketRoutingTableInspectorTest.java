/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import junit.framework.TestCase;
import chabernac.protocol.ProtocolServer;
import chabernac.tools.SimpleNetworkInterface;

public class SocketRoutingTableInspectorTest extends TestCase {
  public void testSocketRoutingTableInspector() throws UnknownPeerException{
    SessionData theSessionData = new SessionData();
    
    RoutingTable theRoutingTable = new RoutingTable( "0" );
    theRoutingTable.addEntry( new RoutingTableEntry( new SocketPeer( "1", SimpleNetworkInterface.createFromIpList( "192.168.1.1" ), 8000) ) );
    theRoutingTable.addEntry( new RoutingTableEntry( new SocketPeer( "2", SimpleNetworkInterface.createFromIpList( "192.168.1.2", "10.0.0.1" ), 8000) ) );
    theRoutingTable.addEntry( new RoutingTableEntry( new SocketPeer( "3", SimpleNetworkInterface.createFromIpList( "192.168.1.3" ), 8000) ) );
    
    SocketRoutingTableInspector theSocketRoutingTableInspector = new SocketRoutingTableInspector( theSessionData );
    
    theSessionData.putProperty( "session1", ProtocolServer.REMOTE_IP, "10.0.0.2" );
    
    RoutingTable theNewRoutingTable = theSocketRoutingTableInspector.inspectRoutingTable( "session1", theRoutingTable );
    
    assertEquals( 3, theNewRoutingTable.getEntries().size() );
    
    assertTrue( theNewRoutingTable.getEntryForPeer( "1" ).getPeer() instanceof IndirectReachablePeer );
    assertTrue( theNewRoutingTable.getEntryForPeer( "2" ).getPeer() instanceof SocketPeer );
    assertTrue( theNewRoutingTable.getEntryForPeer( "3" ).getPeer() instanceof IndirectReachablePeer );
    
    SocketPeer thePeer2 = (SocketPeer)theNewRoutingTable.getEntryForPeer( "2" ).getPeer();
    assertEquals( 1, thePeer2.getHosts().size() );
    assertEquals( 1, thePeer2.getHosts().get(0).getIp().length);
    assertEquals( "10.0.0.1", thePeer2.getHosts().get(0).getIp()[0]);
  }
  
}
