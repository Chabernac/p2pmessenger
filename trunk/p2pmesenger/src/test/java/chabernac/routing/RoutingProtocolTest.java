/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.routing;

import junit.framework.TestCase;
import chabernac.protocol.MasterProtocol;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;

public class RoutingProtocolTest extends TestCase {
  public void testRoutingProtocol() throws InterruptedException{
    long theLocalPeerId = 1;

    RoutingTable theRoutingTable = new RoutingTable();
    MasterProtocol theProtocol = new MasterProtocol();
    RoutingProtocol theRoutingProtocol1 = new RoutingProtocol(theLocalPeerId, theRoutingTable);
    theProtocol.addSubProtocol( theRoutingProtocol1 );
    ProtocolServer theServer = new ProtocolServer(theProtocol, RoutingProtocol.START_PORT, 5);
    
    long theLocalPeerId2 = 2;
    RoutingTable theRoutingTable2 = new RoutingTable();
    MasterProtocol theProtocol2 = new MasterProtocol();
    theProtocol2.addSubProtocol( new RoutingProtocol(theLocalPeerId2, theRoutingTable2) );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    try{
      theServer.start();
      theServer2.start();

      theRoutingProtocol1.scanLocalSystem();
      
      Thread.sleep( 4000 );
           
      assertEquals( 2, theRoutingTable.getEntries().size());
      
      RoutingTableEntry theEntry = theRoutingTable.getEntries().get( 0 );
      assertEquals( RoutingProtocol.START_PORT, theEntry.getPeer().getPort());
      assertEquals( "localhost", theEntry.getPeer().getHost());
      assertEquals( theLocalPeerId, theEntry.getPeer().getPeerId());
      assertEquals( 1, theEntry.getHopDistance());
      
      theEntry = theRoutingTable.getEntries().get( 1 );
      assertEquals( RoutingProtocol.START_PORT + 1, theEntry.getPeer().getPort());
      assertEquals( "localhost", theEntry.getPeer().getHost());
      assertEquals( theLocalPeerId2, theEntry.getPeer().getPeerId());
      assertEquals( 1, theEntry.getHopDistance());
    } finally {
      theServer.stop();
    }
  }
}
