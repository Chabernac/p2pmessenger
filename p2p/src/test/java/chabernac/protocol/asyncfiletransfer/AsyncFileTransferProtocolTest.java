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
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testAsyncFileTransferProtocol() throws InterruptedException, ProtocolException, FileNotFoundException, UnknownPeerException, AsyncFileTransferException, ExecutionException{
    //p1 <--> p2 <--> p3 peer 1 cannot reach peer 3
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1");
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    AsyncFileTransferProtocol theAFP1 = ((AsyncFileTransferProtocol)theProtocol1.getProtocol( AsyncFileTransferProtocol.ID ));
    theAFP1.setPacketSize( 24 );
    //we set the retry ratio pretty high so that we might almost be 100% sure that all packets will finally be delivered
    theAFP1.setMaxRetries( 50 );


    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2");
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );

    ProtocolContainer theProtocol3 = getProtocolContainer( -1, false, "3");
    File theFileToWrite = new File("in.temp");
    if(theFileToWrite.exists()) theFileToWrite.delete();
    TestFileHandler theFileHandler = new TestFileHandler(theFileToWrite);
    ProtocolServer theServer3 = new ProtocolServer(theProtocol3, RoutingProtocol.START_PORT + 2, 5);
    AsyncFileTransferProtocol theAFP3 = ((AsyncFileTransferProtocol)theProtocol3.getProtocol( AsyncFileTransferProtocol.ID ));
    theAFP3.setFileHandler( theFileHandler );
    theAFP3.setIsIgnorePacketRatio( 5 );
    RoutingProtocol theRoutingProtocol3 = (RoutingProtocol)theProtocol3.getProtocol( RoutingProtocol.ID );

    ((RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID )).getLocalUnreachablePeerIds().add( "3" );
    ((RoutingProtocol)theProtocol3.getProtocol( RoutingProtocol.ID )).getLocalUnreachablePeerIds().add( "1" );

    File theTempFile = createTempFile();

    assertNotNull( theTempFile );
    assertTrue( theTempFile.length() > 0 );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      assertTrue( theServer3.start() );
      
      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol3.scanLocalSystem();
      
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      for(int i=0;i<5;i++){
        theRoutingProtocol1.exchangeRoutingTable();
        theRoutingProtocol2.exchangeRoutingTable();
        theRoutingProtocol3.exchangeRoutingTable();
      }

      
      LOGGER.debug( "Sleeping" );
      Thread.sleep( SLEEP_AFTER_SCAN );
      LOGGER.debug( "Done Sleeping" );

      RoutingTable theRoutingTable1 = ((RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID )).getRoutingTable();
      RoutingTable theRoutingTable3 = ((RoutingProtocol)theProtocol3.getProtocol( RoutingProtocol.ID )).getRoutingTable();

      AsyncFileTransferProtocol theFileTransferProtocol = (AsyncFileTransferProtocol)theProtocol1.getProtocol( AsyncFileTransferProtocol.ID );
      RoutingTableEntry thePeer3 = theRoutingTable1.getEntryForPeer( theRoutingTable3.getLocalPeerId() );
      assertNotNull( thePeer3.getPeer() );
      assertTrue(thePeer3.isReachable());
      LOGGER.debug( "Sending file" );
      assertTrue(theFileTransferProtocol.sendFile( theTempFile, theRoutingTable3.getLocalPeerId() ).waitForTransferred());
      LOGGER.debug( "Done Sending file" );

      assertTrue( theFileToWrite.exists() );
      assertEquals( theTempFile.length(), theFileToWrite.length());
      
      assertEquals( 1D, theFileHandler.getLastPercentage() );
      assertTrue( (int)Math.ceil( theTempFile.length() / theAFP1.getPacketSize() ) <= theFileHandler.getNumberOfPercentages() );

      assertEquals( theFileToWrite, theFileHandler.getFile());
      assertEquals( theFileToWrite, theFileHandler.getSavedFile());

    } finally {
      theServer1.stop();
      theServer2.stop();
      theServer3.stop();
      if(theTempFile.exists()){
        theTempFile.delete();
      }
      if(theFileToWrite.exists()){
        theFileToWrite.delete();
      }
    }
  }
  
  public void testRefuseFile(){
    
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
    private double myLastPercentage;
    private double myNumberOfPercentages = 0;

    public TestFileHandler( File aFile ) {
      super();
      myFile = aFile;
    }

    public File getFile() {
      return myFile;
    }

    @Override
    public File acceptFile( String aFileName, String aFileId ) throws FileTransferException {
      return myFile;
    }

    @Override
    public void fileTransfer( String aFile, String aFileId, double aPercentageComplete ) {
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

    protected double getLastPercentage() {
      return myLastPercentage;
    }

    protected void setLastPercentage( double aLastPercentage ) {
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
