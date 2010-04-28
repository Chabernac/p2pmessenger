/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolServer;

public class RoutingProtocolTest extends AbstractProtocolTest {
  private static Logger LOGGER = Logger.getLogger(RoutingProtocolTest.class);
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testProperties(){
    Properties theProperties = new Properties();
    theProperties.setProperty( "routingprotocol.exchangedelay",  "300");
    assertEquals( "300", theProperties.getProperty( "routingprotocol.exchangedelay", "10" ));
  }

  public void testRoutingProtocol() throws InterruptedException, ProtocolException{

    for(int i=1;i<10;i++){
      File theFile = new File("RoutingTable_" + i + ".csv");
      if(theFile.exists()){
        assertTrue( theFile.delete() );
      }
    }
    

    long thet1 = System.currentTimeMillis();
    
    ProtocolContainer theProtocol = getProtocolContainer( 5, true, "1" );
    ProtocolServer theServer = new ProtocolServer(theProtocol, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( 5, true, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    try{
      assertTrue( theServer.start() );
      assertTrue( theServer2.start() );

      RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol.getProtocol( RoutingProtocol.ID );
      RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
      
      RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
      RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
      

      long theFirstSleepTime = 2000;
      Thread.sleep( theFirstSleepTime );

      assertEquals( 2, theRoutingTable1.getEntries().size());

      RoutingTableEntry theEntry = theRoutingTable1.getEntryForPeer( "1" );
      assertEquals( RoutingProtocol.START_PORT, theEntry.getPeer().getPort());
      assertTrue( theEntry.getPeer().getHosts().size() > 0);
      assertEquals( "1", theEntry.getPeer().getPeerId());
      assertEquals( 0, theEntry.getHopDistance());

      theEntry = theRoutingTable1.getEntryForPeer( "2" );
      assertEquals( RoutingProtocol.START_PORT + 1, theEntry.getPeer().getPort());
      assertTrue(  theEntry.getPeer().getHosts().size() > 0);
      assertEquals( theRoutingTable2.getLocalPeerId(), theEntry.getPeer().getPeerId());
      assertEquals( 1, theEntry.getHopDistance());

      //the routing protocol starts exchanging routing information after 2 seconds
      //and updates its routing table after 5 seconds.
      //so after x seconds it should have run Math.floor((x - 2) / 5) times.
//      theRoutingProtocol1.exchangeRoutingTable();

      long theSleepTime = 20000;

      Thread.sleep( theSleepTime );

      long theEffectiveDeltaT = System.currentTimeMillis() - thet1;
      long theTimesRun = (long)Math.floor((theEffectiveDeltaT - 2000) / (1000 * 5)); 

      assertTrue( Math.abs(theTimesRun - theRoutingProtocol1.getExchangeCounter()) < 2 );
      assertTrue( Math.abs(theTimesRun - theRoutingProtocol2.getExchangeCounter()) < 2 );
    } finally {
      theServer.stop();
      theServer2.stop();
    }
  }

  public void testReachableSituation1() throws InterruptedException, ProtocolException{
    //p1 <--> p2 <--> p3 peer 1 cannot reach peer 3
    Thread.sleep( 10000 );

    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 10);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 10);

    ProtocolContainer theProtocol3 = getProtocolContainer( -1, false, "3" );
    ProtocolServer theServer3 = new ProtocolServer(theProtocol3, RoutingProtocol.START_PORT + 2, 10);
    
    ProtocolContainer theProtocol4 = getProtocolContainer( -1, false, "4" );
    ProtocolServer theServer4 = new ProtocolServer(theProtocol4, RoutingProtocol.START_PORT + 3, 5);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    RoutingProtocol theRoutingProtocol3 = (RoutingProtocol)theProtocol3.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable3 = theRoutingProtocol3.getRoutingTable();
    RoutingProtocol theRoutingProtocol4 = (RoutingProtocol)theProtocol4.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable4 = theRoutingProtocol4.getRoutingTable();
    
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
      
      LOGGER.debug("Peer 3 and 4 disconnected");
      
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
   * @throws ProtocolException 
   */
  public void testScanRemoteSystem() throws InterruptedException, ProtocolException{
    
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, true, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, true, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    
    theRoutingProtocol1.getLocalUnreachablePeerIds().add( "2" );
    theRoutingProtocol2.getLocalUnreachablePeerIds().add( "1" );
    
    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      
      Thread.sleep( 2000 );

      //since the routing table is reset and we have an exchange delay of -1 the peers will not be able to reach each other
      //lets test this!
      
      assertNotNull( theRoutingTable1.getEntryForPeer( "2" ) );
      assertFalse( theRoutingTable1.getEntryForPeer( "2" ).isReachable() );
      assertNotNull( theRoutingTable2.getEntryForPeer( "1" ) );
      assertFalse( theRoutingTable2.getEntryForPeer( "1" ).isReachable() );
      
      //even after exchanging routing tables the peers must not be able to reach each other

      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
      
      assertNotNull( theRoutingTable1.getEntryForPeer( "2" ) );
      assertFalse( theRoutingTable1.getEntryForPeer( "2" ).isReachable() );
      assertNotNull( theRoutingTable2.getEntryForPeer( "1" ) );
      assertFalse( theRoutingTable2.getEntryForPeer( "1" ).isReachable() );
      
      //now clear the unreachable peers
      
      theRoutingProtocol1.getLocalUnreachablePeerIds().clear();
      theRoutingProtocol2.getLocalUnreachablePeerIds().clear();
      
      theRoutingProtocol1.scanRemoteSystem(true);
      theRoutingProtocol2.scanRemoteSystem(true);
      
      Thread.sleep( 1000 );
      
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
  
  public void testChangePropagation() throws ProtocolException, InterruptedException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    
    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      
      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
      
      Peer theDummyPeer = new Peer("dummy");
      List<String> theHosts = new ArrayList< String >();
      theHosts.add( "10.240.111.22" );
      theDummyPeer.setHosts( theHosts );
      theDummyPeer.setPort( 54645 );
      
      RoutingTableEntry theDummyEntry = new RoutingTableEntry(theDummyPeer, 1, theDummyPeer, System.currentTimeMillis());
      
      theRoutingTable1.addRoutingTableEntry( theDummyEntry );
      
      //the dummy entry should now immediately be propagated to peer 2
      Thread.sleep( 500 );
      
      RoutingTableEntry theEntry = theRoutingTable2.getEntryForPeer( "dummy" ); 
      assertNotNull( theEntry );
      assertEquals( 2, theEntry.getHopDistance() );
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }
  
  public void testUDPAnnouncement() throws InterruptedException, ProtocolException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    
    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      
      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      
      Thread.sleep( 2000 );
      
      theRoutingTable1.removeAllButLocalPeer();
      theRoutingTable2.removeAllButLocalPeer();
      
      //make sure the peers do not know each other
      assertNull( theRoutingTable1.getEntryForPeer( "2" ) );
      assertNull( theRoutingTable2.getEntryForPeer( "1" ) );
      
      //now send an udp announcement packet, it should be detected by peer 2
      theRoutingProtocol1.sendUDPAnnouncement();
      
      Thread.sleep( 1000 );
      //peer 2 now has peer 1 in its routing table
      assertNotNull( theRoutingTable2.getEntryForPeer( "1" ) );
      
      //now send an udp announcement packet, it should be detected by peer 2
      theRoutingProtocol2.sendUDPAnnouncement();
      
      Thread.sleep( 1000 );
      
      //peer 1 should now know peer 2
      assertNotNull( theRoutingTable1.getEntryForPeer( "2" ) );
      
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }
  
//  public void testDetectRemoteSystem() throws InterruptedException, ProtocolException{
//    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
//    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);
//
//    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
//    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
//    
//    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
//    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
//    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
//    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
//    
//    try{
//      assertTrue( theServer1.start() );
//      assertTrue( theServer2.start() );
//      
//      theRoutingProtocol1.detectRemoteSystem();
//      
//      Thread.sleep( 10000 );
//      
//    } finally {
//      theServer1.stop();
//      theServer2.stop();
//    }
//  }
}
