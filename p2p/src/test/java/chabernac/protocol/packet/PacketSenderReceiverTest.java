/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.io.ClassPathResource;
import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.iP2PServer;
import chabernac.protocol.packet.PacketTransferState.State;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;

public class PacketSenderReceiverTest extends AbstractProtocolTest {
  private static Logger LOGGER = Logger.getLogger(PacketSenderReceiverTest.class);
  
  private ProtocolContainer myProtocolContainer1;
  private iP2PServer myServer1;
  private PacketProtocol myPacketProtocl1;

  private ProtocolContainer myProtocolContainer2;
  private iP2PServer myServer2;
  private PacketProtocol myPacketProtocol2;
  private String myPeerId2;
  
//  static{
//    BasicConfigurator.resetConfiguration();
//    BasicConfigurator.configure();
//  }

  public void setUp() throws Exception{
    super.setUp();
    //p1 <--> p2 <--> p3 peer 1 cannot reach peer 3
    myProtocolContainer1 = getProtocolContainer( -1, false, "1");
    myServer1 = getP2PServer( myProtocolContainer1, RoutingProtocol.START_PORT);
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)myProtocolContainer1.getProtocol( RoutingProtocol.ID );
    myPacketProtocl1 = ((PacketProtocol)myProtocolContainer1.getProtocol( PacketProtocol.ID ));


    myProtocolContainer2 = getProtocolContainer( -1, false, "2");
    myServer2 = getP2PServer(myProtocolContainer2, RoutingProtocol.START_PORT + 1);
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)myProtocolContainer2.getProtocol( RoutingProtocol.ID );
    myPacketProtocol2 = ((PacketProtocol)myProtocolContainer2.getProtocol( PacketProtocol.ID ));


    assertTrue( myServer1.start() );
    assertTrue( myServer2.start() );

    theRoutingProtocol1.scanLocalSystem();
    theRoutingProtocol2.scanLocalSystem();

    Thread.sleep( SLEEP_AFTER_SCAN );

    for(int i=0;i<5;i++){
      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
    }

    RoutingTable theRoutingTable1 = ((RoutingProtocol)myProtocolContainer1.getProtocol( RoutingProtocol.ID )).getRoutingTable();
    RoutingTable theRoutingTable2 = ((RoutingProtocol)myProtocolContainer2.getProtocol( RoutingProtocol.ID )).getRoutingTable();

    RoutingTableEntry thePeer2 = theRoutingTable1.getEntryForPeer( theRoutingTable2.getLocalPeerId() );
    assertNotNull( thePeer2.getPeer() );
    assertTrue(thePeer2.isReachable());

    myPeerId2 = theRoutingTable2.getLocalPeerId();
  }

  public void tearDown(){
    if(myServer1 != null) myServer1.stop();
    if(myServer2 != null) myServer2.stop();
  }

  public void testPacketSenderReceiver() throws IOException, InterruptedException{
    File theTempFile = new File("temp.jpg");
    InputStream theInput1 = null;
    InputStream theInput2 = null;

    try{
      ClassPathResource theFile = new ClassPathResource( "chabernac/protocol/asyncfiletransfer/mars_1k_color.jpg" );
      FileDataPacketProvider theProvider = new FileDataPacketProvider( theFile.getFile(), 1024 );

      PacketSender thePacketSender = new PacketSender( theProvider, myPeerId2, myPacketProtocl1, "file-transfer-1", 5 );
      CountDownLatch theLatch = new CountDownLatch( 1 );
      PacketTransferListener theListener = new PacketTransferListener(theLatch);
      thePacketSender.addPacketTransferListener( theListener );
      
      CountDownLatch theReceiverLatch = new CountDownLatch( 1 );
      PacketTransferListener theReceiverListener = new PacketTransferListener(theReceiverLatch);
      FileDataPacketPersister thePersister = new FileDataPacketPersister( theTempFile, theProvider.getPacketSize(),  theProvider.getNrOfPackets());
      PacketReceiver theReceiver = new PacketReceiver( myPacketProtocol2, "file-transfer-1", thePersister );
      theReceiver.addPacketTransferListener( theReceiverListener );
      theReceiver.start();

      thePacketSender.start();

      theLatch.await( 10, TimeUnit.SECONDS );
      theReceiverLatch.await(1, TimeUnit.SECONDS);
      
      assertEquals( 1, theListener.getStates().size() );
      assertEquals( 1, theReceiverListener.getStates().size() );

      theInput1 = new FileInputStream( theFile.getFile() );
      theInput2 = new FileInputStream( theTempFile );

      int theByte;
      while((theByte = theInput1.read()) != -1){
        assertEquals( theByte, theInput2.read() );
      }
    }finally{
      theTempFile.delete();
    }
  }

  private class PacketTransferListener implements iPacketTransferListener {
    private final List< PacketTransferState > myStates =new ArrayList< PacketTransferState >();
    private final CountDownLatch myLatch;

    public PacketTransferListener(CountDownLatch aLatch){
      myLatch = aLatch;
    }

    @Override
    public void transferUpdated( PacketTransferState aPacketTransferState ) {
      LOGGER.debug("packet transfer state: " + aPacketTransferState);
      if(aPacketTransferState.getState() == State.DONE){
        myStates.add(aPacketTransferState);
        myLatch.countDown();
      }
    }

    public List< PacketTransferState > getStates() {
      return myStates;
    }
  }

}
