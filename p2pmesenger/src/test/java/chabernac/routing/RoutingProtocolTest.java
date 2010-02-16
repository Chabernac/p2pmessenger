/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.routing;

import org.apache.log4j.BasicConfigurator;

import junit.framework.TestCase;
import chabernac.protocol.MasterProtocol;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;

public class RoutingProtocolTest extends TestCase {
  
  public void setUp(){
    BasicConfigurator.configure();
  }
  
  public void testRoutingProtocol() throws InterruptedException{
    long theLocalPeerId = 1;
    
    long theExchangeDelay = 5;

    long thet1 = System.currentTimeMillis();
    RoutingTable theRoutingTable = new RoutingTable(theLocalPeerId);
    MasterProtocol theProtocol = new MasterProtocol();
    RoutingProtocol theRoutingProtocol1 = new RoutingProtocol(theLocalPeerId, theRoutingTable, theExchangeDelay);
    theProtocol.addSubProtocol( theRoutingProtocol1 );
    ProtocolServer theServer = new ProtocolServer(theProtocol, RoutingProtocol.START_PORT, 5);
    
    long theLocalPeerId2 = 2;
    RoutingTable theRoutingTable2 = new RoutingTable(theLocalPeerId2);
    MasterProtocol theProtocol2 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol2 = new RoutingProtocol(theLocalPeerId2, theRoutingTable2, theExchangeDelay) ; 
    theProtocol2.addSubProtocol( theRoutingProtocol2 );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    try{
      theServer.start();
      theServer2.start();

      theRoutingProtocol1.scanLocalSystem();
      
      long theFirstSleepTime = 4000;
      Thread.sleep( theFirstSleepTime );
           
      assertEquals( 2, theRoutingTable.getEntries().size());
      
      RoutingTableEntry theEntry = theRoutingTable.getEntries().get( 0 );
      assertEquals( RoutingProtocol.START_PORT, theEntry.getPeer().getPort());
      assertTrue( theEntry.getPeer().getHosts().size() > 0);
      assertEquals( theLocalPeerId, theEntry.getPeer().getPeerId());
      assertEquals( 1, theEntry.getHopDistance());
      
      theEntry = theRoutingTable.getEntries().get( 1 );
      assertEquals( RoutingProtocol.START_PORT + 1, theEntry.getPeer().getPort());
      assertTrue(  theEntry.getPeer().getHosts().size() > 0);
      assertEquals( theLocalPeerId2, theEntry.getPeer().getPeerId());
      assertEquals( 1, theEntry.getHopDistance());
      
      
      //the routing protocol starts exchanging routing information after 2 seconds
      //and updates its routing table after 5 seconds.
      //so after x seconds it should have run Math.floor((x - 2) / 5) times.
//      theRoutingProtocol1.exchangeRoutingTable();
      
      long theSleepTime = 20000;
      
      Thread.sleep( theSleepTime );
      
      long theEffectiveDeltaT = System.currentTimeMillis() - thet1;
      long theTimesRun = (long)Math.floor((theEffectiveDeltaT - 2000) / (1000 * theExchangeDelay)); 
      
      
      assertTrue( Math.abs(theTimesRun - theRoutingProtocol1.getExchangeCounter()) < 2 );
      assertTrue( Math.abs(theTimesRun - theRoutingProtocol2.getExchangeCounter()) < 2 );
    } finally {
      theServer.stop();
    }
  }
}
