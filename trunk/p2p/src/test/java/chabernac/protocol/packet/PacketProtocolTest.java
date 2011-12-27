/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;


import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.message.MessageException;
import chabernac.protocol.routing.NoAvailableNetworkAdapterException;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.UnknownPeerException;

public class PacketProtocolTest extends AbstractProtocolTest {
  private static Logger LOGGER = Logger.getLogger(PacketProtocolTest.class);
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  
  public void testSendPacket() throws ProtocolException, InterruptedException, MessageException, UnknownPeerException, PacketProtocolException{
    LOGGER.debug("Begin of testMessageProtocol");
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    PacketProtocol thePacketProtocol1 = (PacketProtocol)theProtocol1.getProtocol( PacketProtocol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    PacketProtocol thePacketProtocol2 = (PacketProtocol)theProtocol2.getProtocol( PacketProtocol.ID );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      
      //scanning the local system might take a small time
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      assertTrue( theRoutingTable1.containsEntryForPeer( "2" ) );
      assertTrue( theRoutingTable2.containsEntryForPeer( "1" ) );

      
      PacketListener theSenderListener = new PacketListener();
      PacketListener theReceiverListener = new PacketListener();
      
      thePacketProtocol1.addPacketListenr( "TEST", theSenderListener );
      thePacketProtocol2.addPacketListenr( "TEST", theReceiverListener );
      Packet thePacket = new Packet( "2", "testid", "TEST", "testbytes", 2, true );
      thePacketProtocol1.sendPacket( thePacket );
      
      Thread.sleep( 2000 );
      
      assertEquals( 1, theReceiverListener.getReceivedPackets().size() );
      assertEquals( "testid", theReceiverListener.getReceivedPackets().get( 0 ).getId());
      
      assertEquals( 0, theSenderListener.getFailedPackets().size() );
      assertEquals( 1, theSenderListener.getDeliveredPackets().size() );
      assertEquals( "testid", theSenderListener.getDeliveredPackets().get( 0 ));
      
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }
  
  public void testSendPacketNoReply() throws ProtocolException, InterruptedException, MessageException, UnknownPeerException, PacketProtocolException{
    LOGGER.debug("Begin of testMessageProtocol");
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    PacketProtocol thePacketProtocol1 = (PacketProtocol)theProtocol1.getProtocol( PacketProtocol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    PacketProtocol thePacketProtocol2 = (PacketProtocol)theProtocol2.getProtocol( PacketProtocol.ID );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();

      //scanning the local system might take a small time
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      PacketListener theSenderListener = new PacketListener();
      PacketListener theReceiverListener = new PacketListener();
      
      thePacketProtocol1.addPacketListenr( "TEST", theSenderListener );
      thePacketProtocol2.addPacketListenr( "TEST", theReceiverListener );
      Packet thePacket = new Packet( "2", "testid", "TEST", "testbytes", 2, false );
      thePacketProtocol1.sendPacket( thePacket );
      
      Thread.sleep( 1000 );
      
      assertEquals( 1, theReceiverListener.getReceivedPackets().size() );
      assertEquals( "testid", theReceiverListener.getReceivedPackets().get( 0 ).getId());
      
      assertEquals( 0, theSenderListener.getFailedPackets().size() );
      assertEquals( 0, theSenderListener.getDeliveredPackets().size() );
      
    } finally {
      theServer1.stop();
      theServer2.stop();
    }
  }
  
  public void testSendPacketMultipeer() throws ProtocolException, InterruptedException, SocketException, MessageException, UnknownPeerException, NoAvailableNetworkAdapterException, PacketProtocolException{
    LOGGER.debug("Begin of testMessageProtocol");
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    ProtocolContainer theProtocol3 = getProtocolContainer( -1, false, "3" );
    ProtocolServer theServer3 = new ProtocolServer(theProtocol3, RoutingProtocol.START_PORT + 2, 5);


    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    PacketProtocol thePacketProtocol1 = (PacketProtocol)theProtocol1.getProtocol( PacketProtocol.ID );

    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();

    RoutingProtocol theRoutingProtocol3 = (RoutingProtocol)theProtocol3.getProtocol( RoutingProtocol.ID );
    PacketProtocol thePacketProtocol3 = (PacketProtocol)theProtocol3.getProtocol( PacketProtocol.ID );
    RoutingTable theRoutingTable3 = theRoutingProtocol3.getRoutingTable();

    theRoutingProtocol1.getLocalUnreachablePeerIds().add( "3" );
    theRoutingProtocol3.getLocalUnreachablePeerIds().add( "1" );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      assertTrue( theServer3.start() );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol3.scanLocalSystem();

      //scanning the local system might take a small time
      Thread.sleep( SLEEP_AFTER_SCAN );

      //after a local system scan we must at least know our selfs
      assertNotNull( theRoutingTable1.getEntryForLocalPeer() );
      assertNotNull( theRoutingTable2.getEntryForLocalPeer() );
      assertNotNull( theRoutingTable3.getEntryForLocalPeer() );

      for(int i=0;i<5;i++){
        theRoutingProtocol1.exchangeRoutingTable();
        theRoutingProtocol2.exchangeRoutingTable();
        theRoutingProtocol3.exchangeRoutingTable();
      }

      RoutingTableEntry theRoutingTableEntry = theRoutingTable1.getEntryForPeer( "3" );

      assertEquals( 2, theRoutingTableEntry.getHopDistance() );
      
      PacketListener theSenderListener = new PacketListener();
      PacketListener theReceiverListener = new PacketListener();
      
      thePacketProtocol1.addPacketListenr( "TEST", theSenderListener );
      thePacketProtocol3.addPacketListenr( "TEST", theReceiverListener );
      Packet thePacket = new Packet( "3", "testid", "TEST", "testbytes", 3, true );
      thePacketProtocol1.sendPacket( thePacket );
      
      Thread.sleep( 2000 );
      
      assertEquals( 1, theReceiverListener.getReceivedPackets().size() );
      assertEquals( "testid", theReceiverListener.getReceivedPackets().get( 0 ).getId());
      
      assertEquals( 0, theSenderListener.getFailedPackets().size() );
      assertEquals( 1, theSenderListener.getDeliveredPackets().size() );
      assertEquals( "testid", theSenderListener.getDeliveredPackets().get( 0 ));

    }finally{
      theServer1.stop();
      theServer2.stop();
      theServer3.stop();
    }
  }

  private class PacketListener implements iPacketListener {
    private List<String> myDeliveredPackets = new ArrayList<String>();
    private List<String> myFailedPackets = new ArrayList<String>();
    private List<Packet> myReceivedPackets = new ArrayList<Packet>();

    @Override
    public void packetDelivered( String aPacketId ) {
      myDeliveredPackets.add( aPacketId );
    }

    @Override
    public void packetDeliveryFailed( String aPacketId ) {
      myFailedPackets.add(aPacketId);
    }

    @Override
    public void packetReceived( Packet aPacket ) {
      myReceivedPackets.add(aPacket);
    }
    
    public List<String> getDeliveredPackets(){
      return Collections.unmodifiableList( myDeliveredPackets );
    }
    
    public List<String> getFailedPackets(){
      return Collections.unmodifiableList( myFailedPackets );
    }
    
    public List<Packet> getReceivedPackets(){
      return Collections.unmodifiableList( myReceivedPackets );
    }

  }
  
}
