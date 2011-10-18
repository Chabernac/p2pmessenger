/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.io.ClassPathResource;
import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.packet.AbstractTransferState.State;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;

public class TestAsyncTransferProtocolTest extends AbstractProtocolTest {
  private static Logger LOGGER = Logger.getLogger(TestAsyncTransferProtocolTest.class);
  private ProtocolContainer myProtocolContainer1;
  private ProtocolServer myServer1;
  private AsyncTransferProtocol myTransferProtocl1;

  private ProtocolContainer myProtocolContainer2;
  private ProtocolServer myServer2;
  private AsyncTransferProtocol myTransferProtocol2;

  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void setUp() throws Exception{
    super.setUp();
    //p1 <--> p2 <--> p3 peer 1 cannot reach peer 3
    myProtocolContainer1 = getProtocolContainer( -1, false, "1");
    myServer1 = new ProtocolServer(myProtocolContainer1, RoutingProtocol.START_PORT, 5);
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)myProtocolContainer1.getProtocol( RoutingProtocol.ID );
    myTransferProtocl1 = ((AsyncTransferProtocol)myProtocolContainer1.getProtocol( AsyncTransferProtocol.ID ));


    myProtocolContainer2 = getProtocolContainer( -1, false, "2");
    myServer2 = new ProtocolServer(myProtocolContainer2, RoutingProtocol.START_PORT + 1, 5);
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)myProtocolContainer2.getProtocol( RoutingProtocol.ID );
    myTransferProtocol2 = ((AsyncTransferProtocol)myProtocolContainer2.getProtocol( AsyncTransferProtocol.ID ));


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

  public void testAsyncProtocolFileTransferHappyPath() throws AsyncTransferException, InterruptedException, IOException{
    ClassPathResource theFile = new ClassPathResource( "chabernac/protocol/asyncfiletransfer/mars_1k_color.jpg" );

    File theReceivingFile = new File("test.jpg");

    if(theReceivingFile.exists()) theReceivingFile.delete();

    InputStream theInput1 = null;
    InputStream theInput2 = null;

    try{
      AcceptTransferListener theAcceptListener =  new AcceptTransferListener(theReceivingFile) ; 
      myTransferProtocol2.addTransferListener( theAcceptListener);

      AbstractTransferState theSendState = myTransferProtocl1.startFileTransfer( theFile.getFile(), "2", 256, 5 );
      theSendState.addPacketTransferListener( new PacketTransferVisualizerFrame( ) );
      assertTrue( theSendState.waitForState( State.DONE, 20, TimeUnit.SECONDS ));

      assertTrue( theAcceptListener.getTransferState().waitForState( State.DONE, 1, TimeUnit.SECONDS ));

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

  public void testAsyncProtocolFileTransferCancel() throws AsyncTransferException, InterruptedException, FileNotFoundException{
    ClassPathResource theFile = new ClassPathResource( "chabernac/protocol/asyncfiletransfer/mars_1k_color.jpg" );

    RefuseTransferListener theAcceptListener =  new RefuseTransferListener() ; 
    myTransferProtocol2.addTransferListener( theAcceptListener);

    AbstractTransferState theSendState = myTransferProtocl1.startFileTransfer( theFile.getFile(), "2", 256, 5 );
    theSendState.waitForState( State.CANCELLED, 5, TimeUnit.SECONDS );
    assertEquals( State.CANCELLED,theSendState.getState() );
    theAcceptListener.getTransferState().waitForState( State.CANCELLED, 1, TimeUnit.SECONDS );
    assertEquals( State.CANCELLED, theAcceptListener.getTransferState().getState());
  }

  private class AcceptTransferListener implements iTransferListener {
    private File myFile;
    private CountDownLatch myLatch = new CountDownLatch( 1 );
    private AbstractTransferState myTransferState;

    private AcceptTransferListener(File aFile){
      myFile = aFile;
    }

    @Override
    public void newTransfer( AbstractTransferState aTransfer, boolean isIncoming ) {
      try {
        myTransferState = aTransfer;
        myLatch.countDown();
        ((FileTransferState)aTransfer).start( myFile );
      } catch ( StateChangeException e ) {
        LOGGER.error("An error occured while starting transfer", e);
      }
    }

    public AbstractTransferState getTransferState() throws InterruptedException{
      myLatch.await(5, TimeUnit.SECONDS);
      return myTransferState;
    }

    @Override
    public void transferRemoved( AbstractTransferState aTransfer ) {
      
    }
  }
  
  private class RefuseTransferListener implements iTransferListener {
    private CountDownLatch myLatch = new CountDownLatch( 1 );
    private AbstractTransferState myTransferState;

    @Override
    public void newTransfer( AbstractTransferState aTransfer, boolean isIncoming  ) {
      try {
        myTransferState = aTransfer;
        myLatch.countDown();
        aTransfer.cancel();
      } catch ( StateChangeException e ) {
        LOGGER.error("An error occured while starting transfer", e);
      }
    }

    public AbstractTransferState getTransferState() throws InterruptedException{
      myLatch.await(5, TimeUnit.SECONDS);
      return myTransferState;
    }

    @Override
    public void transferRemoved( AbstractTransferState aTransfer ) {
      
    }
  }
}
