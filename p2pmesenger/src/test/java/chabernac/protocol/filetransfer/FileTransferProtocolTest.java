/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.filetransfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.MasterProtocol;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.pipe.PipeProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

public class FileTransferProtocolTest extends TestCase {
  static{
    BasicConfigurator.configure();
  }
  
  public void testFileTransfer() throws InterruptedException, UnknownHostException, IOException, FileTransferException{
    
    //p1 <--> p2 <--> p3 peer 1 cannot reach peer 3

    RoutingTable theRoutingTable1 = new RoutingTable("1");
    MasterProtocol theProtocol1 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol1 = new RoutingProtocol(theRoutingTable1, 10, false);
    theProtocol1.addSubProtocol( theRoutingProtocol1 );
    PipeProtocol thePipeProtocol1 = new PipeProtocol(theRoutingTable1, 5);
    theProtocol1.addSubProtocol( thePipeProtocol1 );
    MessageProtocol theMessageProtocol1 = new MessageProtocol(theRoutingTable1);
    FileTransferProtocol theFileTransferProtocol1 = new FileTransferProtocol(thePipeProtocol1);
    theMessageProtocol1.addSubProtocol( theFileTransferProtocol1 );
    theProtocol1.addSubProtocol( theMessageProtocol1 );
    
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);


    RoutingTable theRoutingTable2 = new RoutingTable("2");
    MasterProtocol theProtocol2 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol2 = new RoutingProtocol(theRoutingTable2, 10, false);
    theProtocol2.addSubProtocol( theRoutingProtocol2 );
    PipeProtocol thePipeProtocol2 = new PipeProtocol(theRoutingTable2, 5);
    theProtocol2.addSubProtocol( thePipeProtocol2 );
    MessageProtocol theMessageProtocol2 = new MessageProtocol(theRoutingTable2);
    theProtocol2.addSubProtocol( theMessageProtocol2 );

    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    RoutingTable theRoutingTable3 = new RoutingTable("3");
    MasterProtocol theProtocol3 = new MasterProtocol();
    RoutingProtocol theRoutingProtocol3 = new RoutingProtocol(theRoutingTable3, 10, false);
    theProtocol3.addSubProtocol( theRoutingProtocol3 );
    PipeProtocol thePipeProtocol3 = new PipeProtocol(theRoutingTable3, 5);
    //add an echo pipe listener to this pipe protocol
    theProtocol3.addSubProtocol( thePipeProtocol3 );
    MessageProtocol theMessageProtocol3 = new MessageProtocol(theRoutingTable3);
    FileTransferProtocol theFileTransferProtocol3 = new FileTransferProtocol(thePipeProtocol3);
    File theFileToWrite = new File("in.temp");
    TestFileHandler theFileHandler = new TestFileHandler(theFileToWrite);
    theFileTransferProtocol3.setFileHandler( theFileHandler );
    theMessageProtocol3.addSubProtocol( theFileTransferProtocol3 );
    theProtocol3.addSubProtocol( theMessageProtocol3 );

    ProtocolServer theServer3 = new ProtocolServer(theProtocol3, RoutingProtocol.START_PORT + 2, 5);

    theRoutingProtocol1.getLocalUnreachablePeerIds().add( "3" );
    theRoutingProtocol3.getLocalUnreachablePeerIds().add( "1" );
    
    File theTempFile = createTempFile();
    
    assertNotNull( theTempFile );
    assertTrue( theTempFile.length() > 0 );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      assertTrue( theServer3.start() );

      Thread.sleep( 5000 );
      
      theFileTransferProtocol1.sendFile( theTempFile, theRoutingTable1.getEntryForPeer( "3" ).getPeer() );
      
//      Thread.sleep( 10000 );
      
      assertTrue( theFileToWrite.exists() );
      assertEquals( theTempFile.length(), theFileToWrite.length());
      
      assertEquals( theTempFile.getName(), theFileHandler.getAcceptedFile());
      assertEquals( theTempFile.length(), theFileHandler.getTotalBytes());
      assertEquals( theTempFile.length(), theFileHandler.getTotalBytes());
      assertNull(  theFileHandler.getInterruptedFile() );
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
  
  private class TestFileHandler implements iFileHandler{
    private File myFileToWrite = null;
    private File mySavedFile = null;
    
    private String myAcceptedFile = null;
    private long myTranferredBytes = 0;
    private long myTotalBytes = 0;
    
    private File myInterruptedFile = null;
    
    public TestFileHandler(File aFileToWrite){
      myFileToWrite = aFileToWrite;
    }

    @Override
    public File acceptFile( String aFileName ) {
      myAcceptedFile = aFileName;
      return myFileToWrite;
    }

    @Override
    public void fileSaved( File aFile ) {
      mySavedFile = aFile;
    }

    @Override
    public void fileTransfer( File anAfile, long aBytesReceived, long aTotalBytes ) {
     myTranferredBytes = aBytesReceived;
     myTotalBytes = aTotalBytes;
    }

    @Override
    public void fileTransferInterrupted( File aFile ) {
      myInterruptedFile = aFile;
    }

    public File getSavedFile() {
      return mySavedFile;
    }

    public String getAcceptedFile() {
      return myAcceptedFile;
    }

    public long getTranferredBytes() {
      return myTranferredBytes;
    }

    public long getTotalBytes() {
      return myTotalBytes;
    }

    public File getInterruptedFile() {
      return myInterruptedFile;
    }
  }
  
}
