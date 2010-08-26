/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.facade;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.io.SocketProxy;
import chabernac.protocol.AlreadyRunningException;
import chabernac.protocol.message.DeliveryReport;
import chabernac.protocol.message.MessageArchive;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.pipe.Pipe;
import chabernac.protocol.userinfo.UserInfo;
import chabernac.testingutils.DeliveryReportCollector;
import chabernac.testingutils.EchoPipeListener;
import chabernac.testingutils.FileHandler;
import chabernac.testingutils.MessageCollector;
import chabernac.testingutils.UserInfoProvider;

public class P2PFacadeTest extends TestCase {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void testP2PSendMessage() throws P2PFacadeException, InterruptedException, ExecutionException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 20 );

    DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
    theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 20 );
    
    System.out.println("testP2PSendMessage Peer id: " + theFacade1.getPeerId());
    System.out.println("testP2PSendMessage Peer id: " + theFacade2.getPeerId());

    Thread.sleep( 2000 );

    try{
      MessageCollector theMessageCollector = new MessageCollector();
      theFacade2.addMessageListener( theMessageCollector );

      MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "test message" )
      .addDestination( theFacade2.getPeerId() );

      assertNotNull(  theFacade1.sendEncryptedMessage( theMessage, Executors.newFixedThreadPool( 1 ) ).get() );

      Thread.sleep( 1000 );

      assertEquals( 2, theDeliveryReportCollector.getDeliveryReports().size() );
      assertEquals( DeliveryReport.Status.IN_PROGRESS, theDeliveryReportCollector.getDeliveryReports().get( 0 ).getDeliveryStatus());
      assertEquals( DeliveryReport.Status.DELIVERED, theDeliveryReportCollector.getDeliveryReports().get( 1 ).getDeliveryStatus());
      assertEquals( 1, theMessageCollector.getMessages().size());
    } finally{
      theFacade1.stop();
      theFacade2.stop();
    }
  }

  public void testSendMessageWhenServerNotStarted() throws P2PFacadeException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false );
    
    MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "test message" )
    .addDestination( "99" );

    try {
      theFacade1.sendEncryptedMessage( theMessage, Executors.newFixedThreadPool( 1 ) ).get() ;

      fail("We should not get here, an exception must be thrown because the server is not started");
    } catch ( Exception e ) {
    }

    try {
      theFacade1.sendMessage( theMessage, Executors.newFixedThreadPool( 1 ) ).get() ;

      fail("We should not get here, an exception must be thrown because the server is not started");
    } catch ( Exception e ) {
    }
  }

  public void testFailMessage() throws P2PFacadeException, InterruptedException, ExecutionException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 20 );
    
    System.out.println("testFailMessage Peer id: " + theFacade1.getPeerId());

    Thread.sleep( 2000 );
    try{
      DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
      theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

      MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "test message" )
      .addDestination( "99" );

      assertNotNull( theFacade1.sendEncryptedMessage( theMessage, Executors.newFixedThreadPool( 1 ) ).get() );

      Thread.sleep( 1000 );

      assertEquals( 1, theDeliveryReportCollector.getDeliveryReports().size() );
      assertEquals( DeliveryReport.Status.FAILED, theDeliveryReportCollector.getDeliveryReports().get( 0 ).getDeliveryStatus());
    }finally{
      theFacade1.stop();
    }
  }

  public void testSendFile() throws InterruptedException, P2PFacadeException, IOException, ExecutionException{
    SocketProxy.setTraceEnabled( true );
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setKeepRoutingTableHistory( true )
    .start( 20 );
    
    DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
    theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setKeepRoutingTableHistory( true )
    .start( 20 );
    
    System.out.println("testSendFile Peer id: " + theFacade1.getPeerId());
    System.out.println("testSendFile Peer id: " + theFacade2.getPeerId());

    Thread.sleep( 4000 );
    
    
    if(!theFacade1.getRoutingTable().containsEntryForPeer( theFacade2.getPeerId() ) || 
       !theFacade1.getRoutingTable().containsEntryForPeer( theFacade1.getPeerId() )){
      theFacade1.showRoutingTable();
    }
    
    if(!theFacade2.getRoutingTable().containsEntryForPeer( theFacade2.getPeerId() ) || 
        !theFacade2.getRoutingTable().containsEntryForPeer( theFacade1.getPeerId() )){
       theFacade2.showRoutingTable();
     }

    File theFile = null;
    try{
      assertNotNull( theFacade1.getRoutingTableEntry( theFacade2.getPeerId() ));
      assertNotNull( theFacade2.getRoutingTableEntry( theFacade1.getPeerId() ));

      theFile = new File("test.txt");
      theFile.createNewFile();
      
      FileHandler theFilehandler = new FileHandler();
      theFacade2.setFileHandler( theFilehandler );
      assertTrue( theFacade1.sendFile( theFile, theFacade2.getPeerId(), Executors.newFixedThreadPool( 1 )).get() );

      assertEquals( 1, theFilehandler.getReceivedFiles().size());
      assertEquals( 0, theFilehandler.getFailedFiles().size());
    } finally{
      theFacade1.stop();
      theFacade2.stop();
      if(theFile != null) theFile.delete();
    }
  }

  public void testPipe() throws P2PFacadeException, InterruptedException, IOException{
    SocketProxy.setTraceEnabled( true );
    
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setKeepRoutingTableHistory( true )
    .start( 20 );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setKeepRoutingTableHistory( true )
    .start( 20 );
    
    System.out.println("testPipe Peer id: " + theFacade1.getPeerId());
    System.out.println("testPipe Peer id: " + theFacade2.getPeerId());

    Thread.sleep( 2000 );

    try{
      if(!theFacade1.getRoutingTable().containsEntryForPeer( theFacade2.getPeerId() ) || 
          !theFacade1.getRoutingTable().containsEntryForPeer( theFacade1.getPeerId() )){
         theFacade1.showRoutingTable();
       }
       
       if(!theFacade2.getRoutingTable().containsEntryForPeer( theFacade2.getPeerId() ) || 
           !theFacade2.getRoutingTable().containsEntryForPeer( theFacade1.getPeerId() )){
          theFacade2.showRoutingTable();
        }
      
      assertTrue( theFacade1.getRoutingTable().containsEntryForPeer( theFacade2.getPeerId() ) );
      
      theFacade2.addPipeListener( new EchoPipeListener());
      
      Pipe thePipe = theFacade1.openPipe( theFacade2.getPeerId(), "test pipe" );

      OutputStream theOut = thePipe.getSocket().getOutputStream();
      InputStream thein = thePipe.getSocket().getInputStream();

      for(int i=0;i<100;i++){
        theOut.write( i );
        assertEquals( i, thein.read() );
      }
      theFacade1.closePipe( thePipe );
    }finally{
      theFacade1.stop();
      theFacade2.stop();
    }
  }

  public void testUserInfo() throws P2PFacadeException, InterruptedException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( new UserInfoProvider("Guy", "guy.chauliac@gmail.com") )
    .start( 20 );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( new UserInfoProvider("Leslie", "leslie.torreele@gmail.com") )
    .start( 20 );
    
    System.out.println("testUserInfo Peer id: " + theFacade1.getPeerId());
    System.out.println("testUserInfo Peer id: " + theFacade2.getPeerId());

    Thread.sleep( 2000 );

    try{
      assertEquals( "Guy", theFacade1.getPersonalInfo().getName());
      assertEquals( "guy.chauliac@gmail.com", theFacade1.getPersonalInfo().getEMail());

      assertEquals( "Leslie", theFacade2.getPersonalInfo().getName());
      assertEquals( "leslie.torreele@gmail.com", theFacade2.getPersonalInfo().getEMail());

      UserInfo theUserInfoOfFacade1 = theFacade2.getUserInfo().get( theFacade1.getPeerId() );
      assertEquals( "Guy", theUserInfoOfFacade1.getName() );
      assertEquals( "guy.chauliac@gmail.com", theUserInfoOfFacade1.getEMail() );

      UserInfo theUserInfoOfFacade2 = theFacade1.getUserInfo().get( theFacade2.getPeerId() );
      assertEquals( "Leslie", theUserInfoOfFacade2.getName() );
      assertEquals( "leslie.torreele@gmail.com", theUserInfoOfFacade2.getEMail() );

      theFacade1.setUserInfoProvider( new UserInfoProvider("Chauliac", "guy.chauliac@axa.be") );

      //give the user info protocol some time to spread the new user info through the network
      Thread.sleep( 1000 );

      theUserInfoOfFacade1 = theFacade2.getUserInfo().get( theFacade1.getPeerId() );
      assertEquals( "Chauliac", theUserInfoOfFacade1.getName() );
      assertEquals( "guy.chauliac@axa.be", theUserInfoOfFacade1.getEMail() );
    }finally{
      theFacade1.stop();
      theFacade2.stop();
    }
  }

  public void testMessageArchive() throws P2PFacadeException, InterruptedException, ExecutionException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 20 );

    DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
    theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 20 );
    
    System.out.println("testMessageArchive Peer id: " + theFacade1.getPeerId());
    System.out.println("testMessageArchive Peer id: " + theFacade2.getPeerId());

    Thread.sleep( 2000 );

    MessageArchive theArchive1 = theFacade1.getMessageArchive();
    MessageArchive theArchive2 = theFacade2.getMessageArchive();

    try{

      int times = 10;
      for(int i=0;i<times;i++){
        MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "test message" )
        .addDestination( theFacade2.getPeerId() );

        theMessage = theFacade1.sendEncryptedMessage( theMessage, Executors.newFixedThreadPool( 1 ) ).get();
        assertNotNull( theMessage );

        Thread.sleep( 1000 );

        Map<String, DeliveryReport> theReports = theArchive1.getDeliveryReportsForMultiPeerMessage( theMessage );
        //we only send to 1 peer and it should only contain the latest delivery report, so the size must be 1
        assertEquals( 1, theReports.size() );
        //and it must be delivered
        assertEquals( DeliveryReport.Status.DELIVERED, theReports.get( theFacade2.getPeerId() ).getDeliveryStatus() );
      }

      assertEquals( times, theArchive1.getDeliveryReports().size());
      assertEquals( 0, theArchive1.getReceivedMessages().size());
      assertEquals( times, theArchive1.getAllMessages().size());

      assertEquals( 0, theArchive2.getDeliveryReports().size());
      assertEquals( times, theArchive2.getReceivedMessages().size());
      assertEquals( times, theArchive2.getAllMessages().size());

      for(Map< String, DeliveryReport > theReportsPerPeer : theArchive1.getDeliveryReports().values()){
        for(DeliveryReport theReport : theReportsPerPeer.values()){
          assertEquals( DeliveryReport.Status.DELIVERED, theReport.getDeliveryStatus() );
        }
      }
    } finally{
      theFacade1.stop();
      theFacade2.stop();
    }    
  }

  public void testStopWhenAlreadyRunning() throws P2PFacadeException, InterruptedException, ExecutionException{
    P2PFacade theFacade1 = null;
    P2PFacade theFacade2 = null;
    try{
      theFacade1 = new P2PFacade()
      .setExchangeDelay( 300 )
      .setPersist( true )
      .setStopWhenAlreadyRunning(true)
      .start( 20 );
      
      Thread.sleep(5000);

      theFacade2 = new P2PFacade()
      .setExchangeDelay( 300 )
      .setPersist( true )
      .setStopWhenAlreadyRunning(true)
      .start( 20 );
      
      System.out.println("testStopWhenAlreadyRunning Peer id: " + theFacade1.getPeerId());
      System.out.println("testStopWhenAlreadyRunning Peer id: " + theFacade2.getPeerId());

      fail("Whe must not get here, an exception should have occured");
    }catch(P2PFacadeException e){
      assertTrue(e.getCause() instanceof AlreadyRunningException);
    } finally {
      if(theFacade1 != null) theFacade1.stop();
      if(theFacade2 != null) theFacade2.stop();
    }
  }
}
