/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.filetransfer.FileTransferException;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.UnknownPeerException;

public class AsyncFileTransferProtocolTest extends AbstractProtocolTest {
  private static Logger LOGGER = Logger.getLogger(AsyncFileTransferProtocol.class);

  private ProtocolServer myServer1 = null;
  private ProtocolContainer myProtocolContainer1 = null;
  private AsyncFileTransferProtocol myAFP1 = null;
  
  private ProtocolServer myServer2 = null;
  private ProtocolContainer myProtocolContainer2 = null;
  
  private ProtocolServer myServer3 = null;
  private ProtocolContainer myProtocolContainer3 = null;
  private AsyncFileTransferProtocol myAFP3 = null;

  private File myTempFile = null;
  private File myFileToWrite = null;
  
  private TestFileHandler myFileHandler = null;
  
  private String thePeerId1 = null;
  private String thePeerId3 = null;
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void setUp() throws Exception{
    super.setUp();
    PacketSender.SEND_SLEEP = 10;
    
    myTempFile = createTempFile();

    //p1 <--> p2 <--> p3 peer 1 cannot reach peer 3
    myProtocolContainer1 = getProtocolContainer( -1, false, "1");
    myServer1 = new ProtocolServer(myProtocolContainer1, RoutingProtocol.START_PORT, 5);
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)myProtocolContainer1.getProtocol( RoutingProtocol.ID );
    myAFP1 = ((AsyncFileTransferProtocol)myProtocolContainer1.getProtocol( AsyncFileTransferProtocol.ID ));
    myAFP1.setPacketSize( 1 );
    //we set the retry ratio pretty high so that we might almost be 100% sure that all packets will finally be delivered
    myAFP1.setMaxRetries( 50 );


    myProtocolContainer2 = getProtocolContainer( -1, false, "2");
    myServer2 = new ProtocolServer(myProtocolContainer2, RoutingProtocol.START_PORT + 1, 5);
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)myProtocolContainer2.getProtocol( RoutingProtocol.ID );

    myProtocolContainer3 = getProtocolContainer( -1, false, "3");
    myFileToWrite = new File("in.temp");
    if(myFileToWrite.exists()) myFileToWrite.delete();
    myFileHandler = new TestFileHandler(myFileToWrite);
    myServer3 = new ProtocolServer(myProtocolContainer3, RoutingProtocol.START_PORT + 2, 5);
    myAFP3 = ((AsyncFileTransferProtocol)myProtocolContainer3.getProtocol( AsyncFileTransferProtocol.ID ));
    myAFP3.setFileHandler( myFileHandler );
    myAFP3.setIsIgnorePacketRatio( 5 );
    RoutingProtocol theRoutingProtocol3 = (RoutingProtocol)myProtocolContainer3.getProtocol( RoutingProtocol.ID );

    ((RoutingProtocol)myProtocolContainer1.getProtocol( RoutingProtocol.ID )).getLocalUnreachablePeerIds().add( "3" );
    ((RoutingProtocol)myProtocolContainer3.getProtocol( RoutingProtocol.ID )).getLocalUnreachablePeerIds().add( "1" );

    File theTempFile = createTempFile();

    assertNotNull( theTempFile );
    assertTrue( theTempFile.length() > 0 );

    assertTrue( myServer1.start() );
    assertTrue( myServer2.start() );
    assertTrue( myServer3.start() );

    theRoutingProtocol1.scanLocalSystem();
    theRoutingProtocol2.scanLocalSystem();
    theRoutingProtocol3.scanLocalSystem();

    Thread.sleep( SLEEP_AFTER_SCAN );

    for(int i=0;i<5;i++){
      theRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
      theRoutingProtocol3.exchangeRoutingTable();
    }
    
    RoutingTable theRoutingTable1 = ((RoutingProtocol)myProtocolContainer1.getProtocol( RoutingProtocol.ID )).getRoutingTable();
    RoutingTable theRoutingTable3 = ((RoutingProtocol)myProtocolContainer3.getProtocol( RoutingProtocol.ID )).getRoutingTable();

    RoutingTableEntry thePeer3 = theRoutingTable1.getEntryForPeer( theRoutingTable3.getLocalPeerId() );
    assertNotNull( thePeer3.getPeer() );
    assertTrue(thePeer3.isReachable());
    
    thePeerId1 = theRoutingTable1.getLocalPeerId();
    thePeerId3 = theRoutingTable3.getLocalPeerId();
  }

  public void tearDown(){
    PacketSender.SEND_SLEEP = -1;
    
    if(myServer1 != null) myServer1.stop();
    if(myServer2 != null) myServer2.stop();
    if(myServer3 != null) myServer3.stop();
    
    if(myTempFile != null && myTempFile.exists()) myTempFile.delete();
    if(myFileToWrite != null && myFileToWrite.exists()) myFileToWrite.delete();
  }

  public void testAsyncFileTransferProtocol() throws InterruptedException, ProtocolException, FileNotFoundException, UnknownPeerException, AsyncFileTransferException, ExecutionException{
    FileTransferHandler theHandler = myAFP1.sendFile( myTempFile, thePeerId3 );
    new FilePacketVisualizerFrame(theHandler);

    Thread.sleep(1000);
    assertEquals(1, myAFP3.getReceivingTransfers().size());
    assertEquals(1, myAFP1.getSendingTransfers().size());
    new FilePacketVisualizerFrame(myAFP3.getTransferHandler(myAFP3.getReceivingTransfers().iterator().next()));

    theHandler.waitUntillDone();
    assertEquals(FileTransferState.State.DONE,  theHandler.getState().getState());

    compareFiles();
  }
  
  private void compareFiles(){
    assertTrue( myFileToWrite.exists() );
    assertEquals( myTempFile.length(), myFileToWrite.length());

    assertEquals( 1D, myFileHandler.getLastPercentage().getPercentage() );
    assertTrue( (int)Math.ceil( myTempFile.length() / myAFP1.getPacketSize() ) <= myFileHandler.getNumberOfPercentages() );

    assertEquals( myFileToWrite, myFileHandler.getFile());
    assertEquals( myFileToWrite, myFileHandler.getSavedFile());
  }

  public void testRefuseFile() throws UnknownPeerException, ProtocolException, InterruptedException, AsyncFileTransferException{
    //by setting the file to null, it will be refused
    myFileHandler.setFile( null );
    FileTransferHandler theHandler = myAFP1.sendFile( myTempFile, thePeerId3 );

    Thread.sleep(1000);
    assertEquals(0, myAFP3.getReceivingTransfers().size());

    theHandler.waitUntillDone();
    assertEquals(FileTransferState.State.REFUSED,  theHandler.getState().getState());
    LOGGER.debug( "Done Sending file" );
  }

  public void testStopSenderResumeSender() throws InterruptedException, AsyncFileTransferException{
    //bigger packet size for faster test
    myAFP1.setPacketSize( 24 );
    FileTransferHandler theSenderHandler = myAFP1.sendFile( myTempFile, thePeerId3 );
    
    Thread.sleep(500);
    
    assertEquals( FileTransferState.State.RUNNING, theSenderHandler.getState().getState() );
    assertEquals(1, myAFP3.getReceivingTransfers().size());
    FileTransferHandler theReceivingHandler = myAFP3.getTransferHandler( myAFP3.getReceivingTransfers().iterator().next() );
    assertEquals( FileTransferState.State.RUNNING, theReceivingHandler.getState().getState() );
    
    //pauze the sender
    theSenderHandler.pause();
    Thread.yield();
    //check if it's effectively paused
    assertEquals( FileTransferState.State.PAUSED, theSenderHandler.getState().getState() );
    //make sure the receiver is paused as well
    assertEquals( FileTransferState.State.PAUSED, theReceivingHandler.getState().getState() );
    
    //resume transfer
    theSenderHandler.resume();
    Thread.sleep(500);
    //check if it's effectively resumed
    assertEquals( FileTransferState.State.RUNNING, theSenderHandler.getState().getState() );
    //make sure the receiver is resumed as well
    assertEquals( FileTransferState.State.RUNNING, theReceivingHandler.getState().getState() );
    
    theSenderHandler.waitUntillDone();
    assertEquals(FileTransferState.State.DONE,  theSenderHandler.getState().getState());

    compareFiles();
  }
  
  public void testStopReceiverResumeReceiver() throws InterruptedException, AsyncFileTransferException{
    //bigger packet size for faster test
    myAFP1.setPacketSize( 24 );

    FileTransferHandler theSenderHandler = myAFP1.sendFile( myTempFile, thePeerId3 );
    
    Thread.sleep(500);
    
    assertEquals( FileTransferState.State.RUNNING, theSenderHandler.getState().getState() );
    assertEquals(1, myAFP3.getReceivingTransfers().size());
    FileTransferHandler theReceivingHandler = myAFP3.getTransferHandler( myAFP3.getReceivingTransfers().iterator().next() );
    assertEquals( FileTransferState.State.RUNNING, theReceivingHandler.getState().getState() );
    
    //pauze the sender
    theReceivingHandler.pause();
    Thread.yield();
    //check if it's effectively paused
    assertEquals( FileTransferState.State.PAUSED, theSenderHandler.getState().getState() );
    //make sure the receiver is paused as well
    assertEquals( FileTransferState.State.PAUSED, theReceivingHandler.getState().getState() );
    
    //resume transfer
    theReceivingHandler.resume();
    Thread.sleep(500);
    //check if it's effectively resumed
    assertEquals( FileTransferState.State.RUNNING, theSenderHandler.getState().getState() );
    //make sure the receiver is resumed as well
    assertEquals( FileTransferState.State.RUNNING, theReceivingHandler.getState().getState() );
    
    theSenderHandler.waitUntillDone();
    assertEquals(FileTransferState.State.DONE,  theSenderHandler.getState().getState());

    compareFiles();
  }
  
  public void testStopSenderResumeReceiver() throws InterruptedException, AsyncFileTransferException{
    //bigger packet size for faster test
    myAFP1.setPacketSize( 24 );

    FileTransferHandler theSenderHandler = myAFP1.sendFile( myTempFile, thePeerId3 );
    
    Thread.sleep(500);
    
    assertEquals( FileTransferState.State.RUNNING, theSenderHandler.getState().getState() );
    assertEquals(1, myAFP3.getReceivingTransfers().size());
    FileTransferHandler theReceivingHandler = myAFP3.getTransferHandler( myAFP3.getReceivingTransfers().iterator().next() );
    assertEquals( FileTransferState.State.RUNNING, theReceivingHandler.getState().getState() );
    
    //pauze the sender
    theSenderHandler.pause();
    Thread.yield();
    //check if it's effectively paused
    assertEquals( FileTransferState.State.PAUSED, theSenderHandler.getState().getState() );
    //make sure the receiver is paused as well
    assertEquals( FileTransferState.State.PAUSED, theReceivingHandler.getState().getState() );
    
    //resume transfer
    theReceivingHandler.resume();
    Thread.sleep(500);
    //check if it's effectively resumed
    assertEquals( FileTransferState.State.RUNNING, theSenderHandler.getState().getState() );
    //make sure the receiver is resumed as well
    assertEquals( FileTransferState.State.RUNNING, theReceivingHandler.getState().getState() );
    
    theSenderHandler.waitUntillDone();
    assertEquals(FileTransferState.State.DONE,  theSenderHandler.getState().getState());

    compareFiles();
  }
  
  public void testStopReceiverResumeSender() throws InterruptedException, AsyncFileTransferException{
    //bigger packet size for faster test
    myAFP1.setPacketSize( 24 );

    FileTransferHandler theSenderHandler = myAFP1.sendFile( myTempFile, thePeerId3 );
    
    Thread.sleep(500);
    
    assertEquals( FileTransferState.State.RUNNING, theSenderHandler.getState().getState() );
    assertEquals(1, myAFP3.getReceivingTransfers().size());
    FileTransferHandler theReceivingHandler = myAFP3.getTransferHandler( myAFP3.getReceivingTransfers().iterator().next() );
    assertEquals( FileTransferState.State.RUNNING, theReceivingHandler.getState().getState() );
    
    //pauze the sender
    theReceivingHandler.pause();
    Thread.yield();
    //check if it's effectively paused
    assertEquals( FileTransferState.State.PAUSED, theSenderHandler.getState().getState() );
    //make sure the receiver is paused as well
    assertEquals( FileTransferState.State.PAUSED, theReceivingHandler.getState().getState() );
    
    //resume transfer
    theSenderHandler.resume();
    Thread.sleep(500);
    //check if it's effectively resumed
    assertEquals( FileTransferState.State.RUNNING, theSenderHandler.getState().getState() );
    //make sure the receiver is resumed as well
    assertEquals( FileTransferState.State.RUNNING, theReceivingHandler.getState().getState() );
    
    theSenderHandler.waitUntillDone();
    assertEquals(FileTransferState.State.DONE,  theSenderHandler.getState().getState());

    compareFiles();
  }
  
  public void testCancelTransfer() throws InterruptedException, AsyncFileTransferException{
    FileTransferHandler theSenderHandler = myAFP1.sendFile( myTempFile, thePeerId3 );
    Thread.sleep(500);
    
    assertEquals( FileTransferState.State.RUNNING, theSenderHandler.getState().getState() );
    assertEquals(1, myAFP3.getReceivingTransfers().size());
    FileTransferHandler theReceivingHandler = myAFP3.getTransferHandler( myAFP3.getReceivingTransfers().iterator().next() );
    assertEquals( FileTransferState.State.RUNNING, theReceivingHandler.getState().getState() );
    
    theSenderHandler.cancel();
    Thread.sleep(500);
    assertEquals( FileTransferState.State.CANCELLED_OR_REMOVED, theSenderHandler.getState().getState() );
    assertEquals( FileTransferState.State.CANCELLED_OR_REMOVED, theReceivingHandler.getState().getState() );
    
    assertEquals(0, myAFP1.getSendingTransfers().size());
    assertEquals(0, myAFP3.getReceivingTransfers().size());
  }
  
  public void testRemoveFinishedTransfers() throws InterruptedException, AsyncFileTransferException{
    //bigger packet size for faster test
    myAFP1.setPacketSize( 24 );

    FileTransferHandler theHandler = myAFP1.sendFile( myTempFile, thePeerId3 );

    Thread.sleep(1000);
    assertEquals(1, myAFP3.getReceivingTransfers().size());
    assertEquals(1, myAFP1.getSendingTransfers().size());

    theHandler.waitUntillDone();
    assertEquals(FileTransferState.State.DONE,  theHandler.getState().getState());

    //the transfers are still not removed
    assertEquals(1, myAFP3.getReceivingTransfers().size());
    assertEquals(1, myAFP1.getSendingTransfers().size());

    myAFP1.removeFinished();
    myAFP3.removeFinished();

    assertEquals(0, myAFP3.getReceivingTransfers().size());
    assertEquals(0, myAFP1.getSendingTransfers().size());
    compareFiles();
  }

  private File createTempFile() throws FileNotFoundException{
    File theFile = new File("test.temp");
    PrintWriter theWriter = null;
    theWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(theFile)));
    for(int i=0;i<100;i++){
      theWriter.println("Line: " + i);
    }
    theWriter.flush();
    theWriter.close();
    return theFile;
  }

  private class TestFileHandler implements iAsyncFileTransferHandler{
    private File myFile;
    private File mySavedFile;
    private Percentage myLastPercentage;
    private double myNumberOfPercentages = 0;

    public TestFileHandler( File aFile ) {
      super();
      myFile = aFile;
    }

    public File getFile() {
      return myFile;
    }
    
    public void setFile(File aFile){
      myFile = aFile;
    }

    @Override
    public File acceptFile( String aFileName, String aFileId ){
      return myFile;
    }

    @Override
    public void fileTransfer( String aFile, String aFileId, Percentage aPercentageComplete ) {
      myNumberOfPercentages++;
      myLastPercentage = aPercentageComplete;
    }

    @Override
    public void fileSaved( File aFile ) throws FileTransferException {
      mySavedFile = aFile;
    }

    public File getSavedFile(){
      return mySavedFile;
    }

    protected Percentage getLastPercentage() {
      return myLastPercentage;
    }

    protected void setLastPercentage( Percentage aLastPercentage ) {
      myLastPercentage = aLastPercentage;
    }

    protected double getNumberOfPercentages() {
      return myNumberOfPercentages;
    }

    protected void setNumberOfPercentages( double aNumberOfPercentages ) {
      myNumberOfPercentages = aNumberOfPercentages;
    }


  }
}
