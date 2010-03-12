/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import org.apache.log4j.BasicConfigurator;

import junit.framework.TestCase;
import chabernac.protocol.MasterProtocol;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;

/**
 //TODO test remote port scan if all peers on the same host are not online
 */
public class RoutingProtocolTest extends TestCase {

  public void setUp(){
    BasicConfigurator.configure();
  }

  public void testRoutingProtocol() throws InterruptedException{
    String theLocalPeerId = "1";

    long theExchangeDelay = 5;

    long thet1 = System.currentTimeMillis();
    RoutingTable theRoutingTable = new RoutingTable(theLocalPeerId);
    MasterProtocol theProtocol = new MasterProtocol();
    RoutingProtocol theRoutingProtocol1 = new RoutingProtocol(theRoutingTable, theExchangeDelay, false);
    theProtocol.addSubProtocol( theRoutingProtocol1 );
    ProtocolServer theServer = new ProtocolServer(theProtocol, RoutingProtocol.START_PORT, 5);

    String theLocalPeerId2 = "2";
    RoutingTable theRoutingTable2 = new RoutingTable(theLocalPeerId2);
    MasterProtocol theProtocol2 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol2 = new RoutingProtocol(theRoutingTable2, theExchangeDelay, false) ; 
    theProtocol2.addSubProtocol( theRoutingProtocol2 );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    try{
      theServer.start();
      theServer2.start();

      theRoutingProtocol1.scanLocalSystem();

      long theFirstSleepTime = 4000;
      Thread.sleep( theFirstSleepTime );

      assertEquals( 2, theRoutingTable.getEntries().size());

      RoutingTableEntry theEntry = theRoutingTable.getEntryForPeer( "1" );
      assertEquals( RoutingProtocol.START_PORT, theEntry.getPeer().getPort());
      assertTrue( theEntry.getPeer().getHosts().size() > 0);
      assertEquals( theLocalPeerId, theEntry.getPeer().getPeerId());
      assertEquals( 0, theEntry.getHopDistance());

      theEntry = theRoutingTable.getEntryForPeer( "2" );
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
      theServer2.stop();
    }
  }

  public void testReachableSituation1() throws InterruptedException{
    //p1 <--> p2 <--> p3 peer 1 cannot reach peer 3

    RoutingTable theRoutingTable1 = new RoutingTable("1");
    MasterProtocol theProtocol1 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol1 = new RoutingProtocol(theRoutingTable1, -1, false);
    theProtocol1.addSubProtocol( theRoutingProtocol1 );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    RoutingTable theRoutingTable2 = new RoutingTable("2");
    MasterProtocol theProtocol2 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol2 = new RoutingProtocol(theRoutingTable2, -1, false);
    theProtocol2.addSubProtocol( theRoutingProtocol2 );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    RoutingTable theRoutingTable3 = new RoutingTable("3");
    MasterProtocol theProtocol3 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol3 = new RoutingProtocol(theRoutingTable3, -1, false);
    theProtocol3.addSubProtocol( theRoutingProtocol3 );
    ProtocolServer theServer3 = new ProtocolServer(theProtocol3, RoutingProtocol.START_PORT + 2, 5);
    
    RoutingTable theRoutingTable4 = new RoutingTable("4");
    MasterProtocol theProtocol4 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol4 = new RoutingProtocol(theRoutingTable4, -1, false);
    theProtocol4.addSubProtocol( theRoutingProtocol4 );
    ProtocolServer theServer4 = new ProtocolServer(theProtocol4, RoutingProtocol.START_PORT + 3, 5);

    theRoutingProtocol1.getLocalUnreachablePeerIds().add( "3" );
    theRoutingProtocol3.getLocalUnreachablePeerIds().add( "1" );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      assertTrue( theServer3.start() );

      Thread.sleep( 5000 );
      
      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol3.scanLocalSystem();
      
      theRoutingProtocol2.exchangeRoutingTable();
      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol3.exchangeRoutingTable();
      
//      theServer1.stop();
//      theServer2.stop();
//      theServer3.stop();

//      Thread.sleep( 1000 );

      //now examine the routing tables

      //p1
      //peer    hops    gateway
      //p1      0       p1
      //p2      1       p2
      //p3      2       p2

      
      testEntry( theRoutingTable1.getEntryForPeer("1"), 0, "1", true, true); 
      testEntry( theRoutingTable1.getEntryForPeer("2"), 1, "2", true, true);
      testEntry( theRoutingTable1.getEntryForPeer("3"), 2, "2", false, true);

      //p2
      //peer    hops    gateway
      //p1      1       p1
      //p2      0       p2
      //p3      1       p3

      testEntry( theRoutingTable2.getEntryForPeer("1"), 1, "1", true, true); 
      testEntry( theRoutingTable2.getEntryForPeer("2"), 0, "2", true, true);
      testEntry( theRoutingTable2.getEntryForPeer("3"), 1, "3", true, true);

      //p3
      //peer    hops    gateway
      //p1      2       p2
      //p2      1       p2
      //p3      0       p3

      testEntry( theRoutingTable3.getEntryForPeer("1"), 2, "2", false, true); 
      testEntry( theRoutingTable3.getEntryForPeer("2"), 1, "2", true, true);
      testEntry( theRoutingTable3.getEntryForPeer("3"), 0, "3", true, true);
      
      //change the network situation
      //        p2                                   
      //       /  \
      //     p1 -  p3
      
      theRoutingProtocol1.getLocalUnreachablePeerIds().clear();
      theRoutingProtocol3.getLocalUnreachablePeerIds().clear();
      
      //and now wait for a couple of seconds to make sure the routing tables are update
//      Thread.sleep( 5000 );
      
      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
      theRoutingProtocol3.exchangeRoutingTable();
      
      //test the situation again
      
      //p1
      //peer    hops    gateway
      //p1      0       p1
      //p2      1       p2
      //p3      1       p3

      testEntry( theRoutingTable1.getEntryForPeer("1"), 0, "1", true, true); 
      testEntry( theRoutingTable1.getEntryForPeer("2"), 1, "2", true, true);
      testEntry( theRoutingTable1.getEntryForPeer("3"), 1, "3", true, true);

      //p2
      //peer    hops    gateway
      //p1      1       p1
      //p2      0       p2
      //p3      1       p3

      testEntry( theRoutingTable2.getEntryForPeer("1"), 1, "1", true, true); 
      testEntry( theRoutingTable2.getEntryForPeer("2"), 0, "2", true, true);
      testEntry( theRoutingTable2.getEntryForPeer("3"), 1, "3", true, true);

      //p3
      //peer    hops    gateway
      //p1      1       p1
      //p2      1       p2
      //p3      0       p3

      testEntry( theRoutingTable3.getEntryForPeer("1"), 1, "1", true, true); 
      testEntry( theRoutingTable3.getEntryForPeer("2"), 1, "2", true, true);
      testEntry( theRoutingTable3.getEntryForPeer("3"), 0, "3", true, true);
      
      //change the network situation
      //        p2                                   
      //       /  \
      //     p1 -  p3 - p4
      
      theRoutingProtocol1.getLocalUnreachablePeerIds().add( "4" );
      theRoutingProtocol2.getLocalUnreachablePeerIds().add( "4" );
      
      theRoutingProtocol4.getLocalUnreachablePeerIds().add( "1" );
      theRoutingProtocol4.getLocalUnreachablePeerIds().add( "2" );
      
      assertTrue( theServer4.start() );
//      Thread.sleep( 5000 );
      
      theRoutingProtocol4.scanLocalSystem();
      theRoutingProtocol3.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol1.scanLocalSystem();
      
      theRoutingProtocol3.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol4.exchangeRoutingTable();
      
      theRoutingProtocol3.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol4.exchangeRoutingTable();
      
 //test the situation again
      
      //p1
      //peer    hops    gateway
      //p1      0       p1
      //p2      1       p2
      //p3      1       p3
      //p4      2       p3

      testEntry( theRoutingTable1.getEntryForPeer("1"), 0, "1", true, true); 
      testEntry( theRoutingTable1.getEntryForPeer("2"), 1, "2", true, true);
      testEntry( theRoutingTable1.getEntryForPeer("3"), 1, "3", true, true);
      testEntry( theRoutingTable1.getEntryForPeer("4"), 2, "3", false, true);

      //p2
      //peer    hops    gateway
      //p1      1       p1
      //p2      0       p2
      //p3      1       p3
      //p4      2       p3

      testEntry( theRoutingTable2.getEntryForPeer("1"), 1, "1", true, true); 
      testEntry( theRoutingTable2.getEntryForPeer("2"), 0, "2", true, true);
      testEntry( theRoutingTable2.getEntryForPeer("3"), 1, "3", true, true);
      testEntry( theRoutingTable2.getEntryForPeer("4"), 2, "3", false, true);

      //p3
      //peer    hops    gateway
      //p1      1       p1
      //p2      1       p2
      //p3      0       p3
      //p4      1       p4

      testEntry( theRoutingTable3.getEntryForPeer("1"), 1, "1", true, true); 
      testEntry( theRoutingTable3.getEntryForPeer("2"), 1, "2", true, true);
      testEntry( theRoutingTable3.getEntryForPeer("3"), 0, "3", true, true);
      testEntry( theRoutingTable3.getEntryForPeer("4"), 1, "4", true, true);
      
      //p4
      //peer    hops    gateway
      //p1      2       p3
      //p2      2       p3
      //p3      1       p3
      //p4      0       p4

      testEntry( theRoutingTable4.getEntryForPeer("1"), 2, "3", false, true); 
      testEntry( theRoutingTable4.getEntryForPeer("2"), 2, "3", false, true);
      testEntry( theRoutingTable4.getEntryForPeer("3"), 1, "3", true, true);
      testEntry( theRoutingTable4.getEntryForPeer("4"), 0, "4", true, true);
      
      //now disconnect p3
      
      //        p2                                   
      //       /   
      //     p1    p3   p4

      
      theRoutingProtocol1.getLocalUnreachablePeerIds().add( "3" );
      theRoutingProtocol2.getLocalUnreachablePeerIds().add( "3" );
      theRoutingProtocol4.getLocalUnreachablePeerIds().add( "3" );      
      theRoutingProtocol3.getLocalUnreachablePeerIds().add( "1" );
      theRoutingProtocol3.getLocalUnreachablePeerIds().add( "2" );
      theRoutingProtocol3.getLocalUnreachablePeerIds().add( "4" );
      
      //give the routing tables some time to update
//      Thread.sleep( 6000 );
      
      
      
 //test the situation again
      
      //p1
      //peer    hops    gateway
      //p1      0       p1
      //p2      1       p2
      //p3      6       p3
      //p4      6       p3

      
      for(int i=0;i<3;i++){
        theRoutingProtocol1.exchangeRoutingTable();  
        theRoutingProtocol2.exchangeRoutingTable();
        theRoutingProtocol3.exchangeRoutingTable();
        theRoutingProtocol4.exchangeRoutingTable();
      }
      
      testEntry( theRoutingTable1.getEntryForPeer("1"), 0, "1", true, true); 
      testEntry( theRoutingTable1.getEntryForPeer("2"), 1, "2", true, true);
      testEntry( theRoutingTable1.getEntryForPeer("3"), 6, "3", false, false);
      testEntry( theRoutingTable1.getEntryForPeer("4"), 6, "3", false, false);

      //p2
      //peer    hops    gateway
      //p1      1       p1
      //p2      0       p2
      //p3      6       p3
      //p4      6       p3

      theRoutingProtocol2.exchangeRoutingTable();
      testEntry( theRoutingTable2.getEntryForPeer("1"), 1, "1", true, true); 
      testEntry( theRoutingTable2.getEntryForPeer("2"), 0, "2", true, true);
      testEntry( theRoutingTable2.getEntryForPeer("3"), 6, "3", false, false);
      testEntry( theRoutingTable2.getEntryForPeer("4"), 6, "3", false, false);

      //p3
      //peer    hops    gateway
      //p1      6       p1
      //p2      6       p2
      //p3      0       p3
      //p4      6       p4

      theRoutingProtocol3.exchangeRoutingTable();
      testEntry( theRoutingTable3.getEntryForPeer("1"), 6, "1", false, false); 
      testEntry( theRoutingTable3.getEntryForPeer("2"), 6, "2", false, false);
      testEntry( theRoutingTable3.getEntryForPeer("3"), 0, "3", true, true);
      testEntry( theRoutingTable3.getEntryForPeer("4"), 6, "4", false, false);
      
      //p4
      //peer    hops    gateway
      //p1      6       p3
      //p2      6       p3
      //p3      6       p3
      //p4      0       p4

      theRoutingProtocol4.exchangeRoutingTable();
      testEntry( theRoutingTable4.getEntryForPeer("1"), 6, "3", false, false); 
      testEntry( theRoutingTable4.getEntryForPeer("2"), 6, "3", false, false);
      testEntry( theRoutingTable4.getEntryForPeer("3"), 6, "3", false, false);
      testEntry( theRoutingTable4.getEntryForPeer("4"), 0, "4", true, true);
    }finally{
      theServer1.stop();
      theServer2.stop();
      theServer3.stop();
      theServer4.stop();
    }
  }
  
  /**
   * this test method tests if the remote system scan of the routing table works.
   * unfurtunattelly this is not yet a perfect simulation of what will happen in the real world.
   * but at least a part of the code is tested.
   * 
   * @throws InterruptedException
   */
  public void testScanRemoteSystem() throws InterruptedException{

    RoutingTable theRoutingTable1 = new RoutingTable("1");
    MasterProtocol theProtocol1 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol1 = new RoutingProtocol(theRoutingTable1, -1, true);
    theProtocol1.addSubProtocol( theRoutingProtocol1 );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    RoutingTable theRoutingTable2 = new RoutingTable("2");
    MasterProtocol theProtocol2 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol2 = new RoutingProtocol(theRoutingTable2, -1, true);
    theProtocol2.addSubProtocol( theRoutingProtocol2 );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    
    theRoutingProtocol1.getLocalUnreachablePeerIds().add( "2" );
    theRoutingProtocol2.getLocalUnreachablePeerIds().add( "1" );
    
    
    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
      
      assertNotNull( theRoutingTable1.getEntryForPeer( "2" ) );
      assertFalse( theRoutingTable1.getEntryForPeer( "2" ).isReachable() );
      assertNotNull( theRoutingTable2.getEntryForPeer( "1" ) );
      assertFalse( theRoutingTable2.getEntryForPeer( "1" ).isReachable() );
      
      theRoutingProtocol1.scanRemoteSystem(true);
      theRoutingProtocol2.scanRemoteSystem(true);
      
      assertNotNull( theRoutingTable1.getEntryForPeer( "2" ) );
      assertTrue(  theRoutingTable1.getEntryForPeer( "2" ).isReachable() );
      assertNotNull( theRoutingTable2.getEntryForPeer( "1" ) );
      assertTrue( theRoutingTable2.getEntryForPeer( "1" ).isReachable() );
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }

  private void testEntry(RoutingTableEntry anEntry, int aHopDistance, String aGateway, boolean isResponding, boolean isReachable){
    assertNotNull( anEntry );
    assertEquals( "Hop distance", aHopDistance, anEntry.getHopDistance() );
    //if the hop distance is the max distance the gateway does not matter
    if(aHopDistance < RoutingTableEntry.MAX_HOP_DISTANCE){
      assertEquals( "Gateway", aGateway, anEntry.getGateway().getPeerId() );
    }
    assertEquals( "Responding", isResponding, anEntry.isResponding());
    assertEquals( "Reachable", isReachable, anEntry.isReachable());
  }
}
