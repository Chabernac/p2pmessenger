/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.File;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import chabernac.comet.CometServlet;
import chabernac.p2p.web.ProtocolServlet;
import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.P2PServerFactoryException;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolWebServer;
import chabernac.protocol.iP2PServer;
import chabernac.tools.SimpleNetworkInterface;

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
  
  public void testLocalPeer() throws InterruptedException, SocketException, NoAvailableNetworkAdapterException, ProtocolException, UnknownPeerException, P2PServerFactoryException{

    ProtocolContainer theProtocol = getProtocolContainer( -1, true, "1" );
    iP2PServer theServer = getP2PServer( theProtocol, RoutingProtocol.START_PORT);
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol.getProtocol( RoutingProtocol.ID );

    try{
      assertTrue( theServer.start() );
      
      Thread.sleep( 1000 );
      
      RoutingTableEntry theEntry = theRoutingProtocol1.getRoutingTable().getEntryForLocalPeer();
      assertNotNull( theEntry );
      assertTrue(((SocketPeer)theEntry.getPeer()).getPort() > 0 );
      assertEquals( 0, theEntry.getHopDistance());
      
      theRoutingProtocol1.scanLocalSystem();
      
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      theEntry = theRoutingProtocol1.getRoutingTable().getEntryForLocalPeer();
      assertNotNull( theEntry );
      assertTrue( ((SocketPeer)theEntry.getPeer()).getPort() > 0 );
      assertEquals( 0, theEntry.getHopDistance());
      
      
    } finally {
      theServer.stop();
    }

  }

  public void testRoutingProtocol() throws InterruptedException, ProtocolException, UnknownPeerException, P2PServerFactoryException{

    //delete routing table files is they exist
    for(int i=1;i<10;i++){
      File theFile = new File("RoutingTable_" + i + ".bin");
      if(theFile.exists()){
        assertTrue( theFile.delete() );
      }
    }
    
    int theExchangeDelayInSeconds = 3;

    //server 1
    ProtocolContainer theProtocol = getProtocolContainer( theExchangeDelayInSeconds, true, "1" );
    iP2PServer theServer = getP2PServer( theProtocol, RoutingProtocol.START_PORT);

    //server 2
    ProtocolContainer theProtocol2 = getProtocolContainer( theExchangeDelayInSeconds, true, "2" );
    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);
    
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    
    try{
      assertTrue( theServer.start() );
      assertTrue( theServer2.start() );
      
      long thet1 = System.currentTimeMillis();

      //sleep for 5 seconds so that the peers can exchange their routing tables
      long theFirstSleepTime = 5000;
      Thread.sleep( theFirstSleepTime );

      assertEquals( 2, theRoutingTable1.getEntries().size());

      //make sure routing table of peer 1 contains both peers
      RoutingTableEntry theEntry = theRoutingTable1.getEntryForLocalPeer();
      assertEquals( RoutingProtocol.START_PORT, ((SocketPeer)theEntry.getPeer()).getPort());
      assertTrue( ((SocketPeer)theEntry.getPeer()).getHosts().size() > 0);
      assertEquals( "1", theEntry.getPeer().getPeerId());
      assertEquals( 0, theEntry.getHopDistance());

      theEntry = theRoutingTable1.getEntryForPeer( "2" );
      assertEquals( RoutingProtocol.START_PORT + 1, ((SocketPeer)theEntry.getPeer()).getPort());
      assertTrue(  ((SocketPeer)theEntry.getPeer()).getHosts().size() > 0);
      assertEquals( theRoutingTable2.getLocalPeerId(), theEntry.getPeer().getPeerId());
      assertEquals( 1, theEntry.getHopDistance());

      //the routing protocol starts exchanging routing information after 2 seconds
      //and updates its routing table after 5 seconds.
      //so after x seconds it should have run Math.floor((x - 2) / 5) times.
//      theRoutingProtocol1.exchangeRoutingTable();
    
      long theSleepTime = 20000;

      Thread.sleep( theSleepTime );

      long theEffectiveDeltaT = System.currentTimeMillis() - thet1;
      long theTimesRun = (long)Math.floor((theEffectiveDeltaT - 2000) / (1000 * theExchangeDelayInSeconds)); 

      assertTrue( Math.abs(theTimesRun - theRoutingProtocol1.getExchangeCounter()) < 2 );
      assertTrue( Math.abs(theTimesRun - theRoutingProtocol2.getExchangeCounter()) < 2 );
    } finally {
      theServer.stop();
      theServer2.stop();
    }
  }

  public void testReachableSituation1() throws InterruptedException, ProtocolException, UnknownPeerException, P2PServerFactoryException{
    //p1 <--> p2 <--> p3 peer 1 cannot reach peer 3
//    Thread.sleep( 10000 );

    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);

    ProtocolContainer theProtocol3 = getProtocolContainer( -1, false, "3" );
    iP2PServer theServer3 = getP2PServer( theProtocol3, RoutingProtocol.START_PORT + 2);
    
    ProtocolContainer theProtocol4 = getProtocolContainer( -1, false, "4" );
    iP2PServer theServer4 = getP2PServer( theProtocol4, RoutingProtocol.START_PORT + 3);

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

      
      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol3.scanLocalSystem();
      
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      for(int i=0;i<2;i++){
        theRoutingProtocol2.exchangeRoutingTable();
        theRoutingProtocol1.exchangeRoutingTable();
        theRoutingProtocol3.exchangeRoutingTable();
      }
      
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
      
      theRoutingProtocol4.scanLocalSystem();
      theRoutingProtocol3.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol1.scanLocalSystem();
      
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      theRoutingProtocol3.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol4.exchangeRoutingTable();
      
      theRoutingProtocol3.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol4.exchangeRoutingTable();
      
 //test the situation again
      
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

      
      theRoutingTable1.setKeepHistory( true );
      
      for(int i=0;i<5;i++){
        theRoutingProtocol1.exchangeRoutingTable();  
        theRoutingProtocol2.exchangeRoutingTable();
        theRoutingProtocol3.exchangeRoutingTable();
        theRoutingProtocol4.exchangeRoutingTable();
      }
      
      theRoutingTable1.setKeepHistory( false );
      
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
      LOGGER.debug( "Shutting down servers" );
      if(theServer1 != null) theServer1.stop();
      if(theServer2 != null) theServer2.stop();
      if(theServer3 != null) theServer3.stop();
      if(theServer4 != null) theServer4.stop();
      LOGGER.debug( "Shutting down servers ended" );
    }
  }
  
  /**
   * this test method tests if the remote system scan of the routing table works.
   * unfurtunattelly this is not yet a perfect simulation of what will happen in the real world.
   * but at least a part of the code is tested.
   * 
   * @throws InterruptedException
   * @throws ProtocolException 
   * @throws UnknownPeerException 
   * @throws P2PServerFactoryException 
   */
  public void testScanRemoteSystem() throws InterruptedException, ProtocolException, UnknownPeerException, P2PServerFactoryException{
    
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    
//    theRoutingProtocol1.getLocalUnreachablePeerIds().add( "2" );
//    theRoutingProtocol2.getLocalUnreachablePeerIds().add( "1" );
    
    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      Thread.sleep( SLEEP_AFTER_SCAN );

      //since the routing table is reset and we have an exchange delay of -1 the peers will not be able to reach each other
      //lets test this!
      
      assertNotNull( theRoutingTable1.getEntryForPeer( "2" ) );
      assertTrue( theRoutingTable1.getEntryForPeer( "2" ).isReachable() );
      assertNotNull( theRoutingTable2.getEntryForPeer( "1" ) );
      assertTrue( theRoutingTable2.getEntryForPeer( "1" ).isReachable() );
      
      theRoutingProtocol1.getLocalUnreachablePeerIds().add( "2" );
      theRoutingProtocol2.getLocalUnreachablePeerIds().add( "1" );
      
      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
      Thread.sleep( SLEEP_AFTER_SCAN );
      
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
      
      Thread.sleep( SLEEP_AFTER_SCAN );
      
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
  
  /*
   * in this test we test if a change to a routing table will travel trough the entire network
   */
  public void testChangePropagation() throws ProtocolException, InterruptedException, UnknownPeerException, P2PServerFactoryException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    
    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      
      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      
      Thread.sleep(SLEEP_AFTER_SCAN);
      
      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
      
      SocketPeer theDummyPeer = new SocketPeer("dummy");
      List<SimpleNetworkInterface> theHosts = new ArrayList< SimpleNetworkInterface >();
      theHosts.add( SimpleNetworkInterface.createFromIpList( "10.240.111.22") );
      theDummyPeer.setHosts( theHosts );
      theDummyPeer.setPort( 12808 );
      
      RoutingTableEntry theDummyEntry = new RoutingTableEntry(theDummyPeer, 1, theDummyPeer, System.currentTimeMillis());
      
      theRoutingTable1.addRoutingTableEntry( theDummyEntry );
      
      //the dummy entry should now immediately be propagated to peer 2
      Thread.sleep( 2000 );
      
      RoutingTableEntry theEntry = theRoutingTable2.getEntryForPeer( "dummy" ); 
      assertNotNull( theEntry );
//      assertEquals( 2, theEntry.getHopDistance() );
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }
  
  @Ignore
  public void testUDPAnnouncement() throws InterruptedException, ProtocolException, UnknownPeerException, P2PServerFactoryException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);
    
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    
    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      
      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      theRoutingTable1.removeAllButLocalPeer();
      theRoutingTable2.removeAllButLocalPeer();
      
      //make sure the peers do not know each other
      assertFalse(theRoutingTable1.containsEntryForPeer( "2" ) );
      assertFalse(theRoutingTable2.containsEntryForPeer( "1" ) );
      
      //now send an udp announcement packet, it should be detected by peer 2
      theRoutingProtocol1.sendUDPAnnouncement(false);
      
      
      Thread.sleep( 1000 );
      //peer 2 now has peer 1 in its routing table
      assertTrue(theRoutingTable2.containsEntryForPeer( "1" ) );
      
      //now send an udp announcement packet, it should be detected by peer 2
      theRoutingProtocol2.sendUDPAnnouncement(false);
      
      Thread.sleep( 1000 );
      
      //peer 1 should now know peer 2
      assertTrue(theRoutingTable1.containsEntryForPeer( "2" ) );
      
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }
  
  public void testPersistRoutingTableWithFixedPeerId() throws ProtocolException, InterruptedException, P2PServerFactoryException{
    File theRoutingTableFile = new File( "RoutingTable_1.bin" );
    if(theRoutingTableFile.exists()) theRoutingTableFile.delete();
    
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, true, "1" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);
    
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    try{
      assertTrue( theServer1.start() );
      
      theRoutingProtocol1.scanLocalSystem();
      
      Thread.sleep( SLEEP_AFTER_SCAN );

      assertEquals( 1, theRoutingTable1.getEntries().size() );
      
      theServer1.stop();
      
      assertTrue( theRoutingTableFile.exists() );
      
      theProtocol1 = getProtocolContainer( -1, true, "1" );
      theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);
      
      theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
      theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
      
      assertTrue( theServer1.start() );
      
      theRoutingProtocol1.scanLocalSystem();
      
      Thread.sleep( SLEEP_AFTER_SCAN );

      assertEquals( 1, theRoutingTable1.getEntries().size() );
      
      theServer1.stop();
      
      assertTrue( theRoutingTableFile.exists() );
    }finally{
      theServer1.stop();
    }
  }
  
  public void testPersistRoutingTableWithNewPeerId() throws ProtocolException, InterruptedException, P2PServerFactoryException{
    File theRoutingTableFile = new File( "RoutingTable.bin" );
    if(theRoutingTableFile.exists()) theRoutingTableFile.delete();
    
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, true, null );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);
    
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    try{
      assertTrue( theServer1.start() );
      
      theRoutingProtocol1.scanLocalSystem();
      Thread.sleep( SLEEP_AFTER_SCAN);

      assertEquals( 1, theRoutingTable1.getEntries().size() );
      
      String thePeerId = theRoutingTable1.getLocalPeerId();
      
      theServer1.stop();
      
      assertTrue( theRoutingTableFile.exists() );
      
      theProtocol1 = getProtocolContainer( -1, true, null );
      theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);
      
      theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
      theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
      
      assertTrue( theServer1.start() );
      
      theRoutingProtocol1.scanLocalSystem();
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      assertEquals( thePeerId, theRoutingTable1.getLocalPeerId() );

      assertEquals( 1, theRoutingTable1.getEntries().size() );
      
      theServer1.stop();
      
      assertTrue( theRoutingTableFile.exists() );
    }finally{
      theServer1.stop();
    }
  }
  
//  public void testDetectRemoteSystem() throws InterruptedException, ProtocolException{
//    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
//    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);
//
//    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
//    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);
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
  
    public void testLocalPeerEntry() throws ProtocolException, UnknownPeerException, InterruptedException, P2PServerFactoryException{
      ProtocolContainer theProtocol1 = getProtocolContainer( -1, true, "1");
      iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);
      
      RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
      RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
      try{
        assertTrue( theServer1.start() );
        
        Thread.sleep( 6000 );
        
        RoutingTableEntry theEntry = theRoutingTable1.getEntryForLocalPeer();
        
        assertEquals( 0,theEntry.getHopDistance());
        assertTrue( theRoutingTable1.getEntries().size() > 0);
        assertEquals( RoutingProtocol.START_PORT,((SocketPeer)theEntry.getPeer()).getPort());
      } finally {
        theServer1.stop();
      }
    }
    
//    public void testRemoveEntry() throws ProtocolException, InterruptedException, UnknownPeerException{
//      ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
//      iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);
//
//      ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
//      iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);
//      
//      RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
//      RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
//      RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
//      RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
//      
//
//      try{
//        assertTrue( theServer1.start() );
//        assertTrue( theServer2.start() );
//        
//        theRoutingProtocol1.scanLocalSystem();
//        theRoutingProtocol2.scanLocalSystem();
//        Thread.sleep(SLEEP_AFTER_SCAN);
//        
//        //the peers should now each other now
//        
//        assertTrue(theRoutingTable1.containsEntryForPeer("2"));
//        assertTrue(theRoutingTable2.containsEntryForPeer("1"));
//        
//        //now add the peer which does not exist for real but which is reachable trough peer 2
//        Peer thePeer3 = new Peer("3", "localhost", RoutingProtocol.START_PORT + 2);
//        RoutingTableEntry theEntry = new RoutingTableEntry(thePeer3, 2, theRoutingTable2.getEntryForLocalPeer().getPeer(), System.currentTimeMillis());
//        theRoutingTable1.addRoutingTableEntry(theEntry);
//        
//        //check that the entry is really added
//        assertTrue(theRoutingTable1.containsEntryForPeer("3"));
//        
//        //after exchanging routing tables the peer 3 should be removed
//        theRoutingProtocol1.exchangeRoutingTable();
//        
//        Thread.sleep(SLEEP_AFTER_SCAN);
//        
//        assertFalse(theRoutingTable1.containsEntryForPeer("3"));
//        
//        //do it again but now without routing table listeners
//        theRoutingTable1.removeAllRoutingTableListeners();
//        theRoutingTable1.addRoutingTableEntry(theEntry);
//        //check that the entry is really added
//        assertTrue(theRoutingTable1.containsEntryForPeer("3"));
//        //check that the entry is not added to routing table of peer 2 by propagation, should not happen because of the removed routing table listeners
//        Thread.sleep( SLEEP_AFTER_SCAN );
//        assertFalse(theRoutingTable2.containsEntryForPeer("3"));
//        theRoutingProtocol1.exchangeRoutingTable();
//        Thread.sleep( SLEEP_AFTER_SCAN );
//        assertFalse(theRoutingTable1.containsEntryForPeer("3"));
//        
//        
//      }finally{
//        if(theServer1 != null) theServer1.stop();
//        if(theServer2 != null) theServer2.stop();
//      }
//    }
    
    public void testDoNotExchangeRoutingTableEntriesWithMaxHopDistance() throws ProtocolException, NoAvailableNetworkAdapterException, InterruptedException, P2PServerFactoryException{
      ProtocolContainer theProtocol1 = getProtocolContainer( 1, false, "1" );
      iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

      ProtocolContainer theProtocol2 = getProtocolContainer( 1, false, "2" );
      iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);
      
      RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
      RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
      RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
      RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
      
      SocketPeer thePeer3 = new SocketPeer("3", RoutingProtocol.START_PORT + 2);
      theRoutingTable1.addEntry( new RoutingTableEntry(thePeer3, RoutingTableEntry.MAX_HOP_DISTANCE,  thePeer3, System.currentTimeMillis()));
      
      try{
        assertTrue( theServer1.start() );
        assertTrue( theServer2.start() );
        
        Thread.sleep( 5000 );
        
        assertTrue( theRoutingTable1.containsEntryForPeer( "1" ) );
        assertTrue( theRoutingTable1.containsEntryForPeer( "2" ) );
        assertTrue( theRoutingTable1.containsEntryForPeer( "3" ) );
        
        assertTrue( theRoutingTable2.containsEntryForPeer( "1" ) );
        assertTrue( theRoutingTable2.containsEntryForPeer( "2" ) );
        //the entry with hop distance 6 should not be distributed to peer 2
        assertFalse( theRoutingTable2.containsEntryForPeer( "3" ) );
      } finally{
        LOGGER.debug( "Stopping servers" );
        if(theServer1 != null) theServer1.stop();
        if(theServer2 != null) theServer2.stop();
      }
    }
    
    public void testScanSuperNodes() throws Exception{
      ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1", "localhost", "http://localhost:9090/") ;
      iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

      ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2", "localhost", "http://localhost:9090/") ;
      iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);
      
      RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
      RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
      RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
      RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
      theRoutingTable2.setKeepHistory( true );
      
      Server theWebServer = new Server(9090);

      try{
        assertTrue( theServer1.start() );

        Context root = new Context(theWebServer,ProtocolWebServer.CONTEXT,Context.SESSIONS);
        CometServlet theCometServlet= new CometServlet();
        ServletHolder theCometHolder = new ServletHolder(theCometServlet);
        theCometHolder.setInitOrder(1);
        root.addServlet(theCometHolder, ProtocolWebServer.COMET);
        ProtocolServlet theProtocolServlet = new ProtocolServlet();
        ServletHolder theProtocolHolder = new ServletHolder(theProtocolServlet);
        theProtocolHolder.setInitParameter( "serverurl", "http://localhost:9090" + ProtocolWebServer.CONTEXT );
        theProtocolHolder.setInitOrder(2);
        root.addServlet(theProtocolHolder, ProtocolWebServer.PROTOCOL);

        theWebServer.start();
        assertTrue(theWebServer.isStarted());

        assertTrue(theServer1.start());
        assertTrue(theServer2.start());
        Thread.sleep( 2000 );
        
        assertEquals( 1, theRoutingTable1.getEntries().size() );
        assertEquals( 1, theRoutingTable2.getEntries().size() );
        
        theRoutingProtocol1.scanSuperNodes();
        theRoutingProtocol2.scanSuperNodes();
        
        Thread.sleep( SLEEP_AFTER_SCAN );
        assertEquals( 3, theRoutingTable1.getEntries().size() );
        assertEquals( 3, theRoutingTable2.getEntries().size() );
        
      } finally{
        theServer1.stop();
        theServer2.stop();
        theWebServer.stop();
      }
    }
}
