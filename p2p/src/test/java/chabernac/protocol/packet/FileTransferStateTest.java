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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.io.ClassPathResource;
import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.iP2PServer;
import chabernac.protocol.packet.AbstractTransferState.Side;
import chabernac.protocol.packet.AbstractTransferState.State;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;

public class FileTransferStateTest extends AbstractProtocolTest {
  private static Logger LOGGER = Logger.getLogger(FileTransferState.class);
  private ProtocolContainer myProtocolContainer1;
  private iP2PServer myServer1;
  private PacketProtocol myPacketProtocl1;

  private ProtocolContainer myProtocolContainer2;
  private iP2PServer myServer2;
  private PacketProtocol myPacketProtocol2;
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

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
  }

  public void tearDown(){
    if(myServer1 != null) myServer1.stop();
    if(myServer2 != null) myServer2.stop();
  }

  public void testFileTransferState() throws StateChangeException, InterruptedException, IOException{
    ClassPathResource theFile = new ClassPathResource( "chabernac/protocol/asyncfiletransfer/mars_1k_color.jpg" );

    File theReceivingFile = new File("test.jpg");
    InputStream theInput1 = null;
    InputStream theInput2 = null;

    
    if(theReceivingFile.exists()) theReceivingFile.delete();

    try{
      FileTransferState theSendState = FileTransferState.createForSend( myPacketProtocl1, "transfer-1", theFile.getFile(), "2", 1024, 5, Side.INITIATOR);
      StateChangeListener theSendStateListener = new StateChangeListener();
      theSendState.addStateChangeListener( theSendStateListener );

      FileTransferState theReceiveState = FileTransferState.createForReceive( myPacketProtocol2, theSendState.getTransferId(), theReceivingFile, "1", theSendState.getNrOfPackets(), theSendState.getPacketSize(), Side.RECEIVER );
      StateChangeListener theReceiveStateListener = new StateChangeListener();
      theReceiveState.addStateChangeListener( theReceiveStateListener );


      assertEquals( State.PENDING, theSendState.getState() );
      assertEquals( State.PENDING, theReceiveState.getState() );

      theReceiveState.start();
      theSendState.start();

      assertEquals( State.RUNNING, theSendState.getState() );
      assertEquals( State.RUNNING, theSendState.getState() );

      theReceiveStateListener.await( 5, TimeUnit.SECONDS );
      theSendStateListener.await( 1, TimeUnit.SECONDS );

      LOGGER.error("Last receiving status " + theReceiveStateListener.getStates().get( 0 ));
      assertEquals( 2, theReceiveStateListener.getStates().size() );
      
      assertEquals( State.RUNNING, theReceiveStateListener.getStates().get( 0 ));
      assertEquals( State.DONE, theReceiveStateListener.getStates().get( 1 ));

      assertEquals( 2, theSendStateListener.getStates().size() );
      assertEquals( State.RUNNING, theSendStateListener.getStates().get( 0 ));
      assertEquals( State.DONE, theSendStateListener.getStates().get( 1 ));
      
      //test if the files are equal
      theInput1 = new FileInputStream( theFile.getFile() );
      theInput2 = new FileInputStream( theReceivingFile );

      int theByte;
      while((theByte = theInput1.read()) != -1){
        assertEquals( theByte, theInput2.read() );
      }
    }finally{
      if(theReceivingFile.exists()) theReceivingFile.delete();
    }
  }

  private class StateChangeListener implements iStateChangeListener{
    private List< State > myStates = new ArrayList< State >();

    CountDownLatch myLatch = new CountDownLatch( 1 );

    @Override
    public void stateChanged( String aTransferId, State anOldState, State aNewState ) {
      myStates.add(aNewState);
      if(aNewState == State.DONE){
        myLatch.countDown();
      }
    }

    public List<State> getStates(){
      return myStates;
    }

    public void await(int aTimeout, TimeUnit aTimeUnit) throws InterruptedException{
      myLatch.await( aTimeout, aTimeUnit);
    }

  }
}
