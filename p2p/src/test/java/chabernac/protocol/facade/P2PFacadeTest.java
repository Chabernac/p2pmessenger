/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.facade;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.io.SocketProxy;
import chabernac.protocol.message.DeliveryReport;
import chabernac.protocol.message.MessageArchive;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iMultiPeerMessageListener;
import chabernac.protocol.pipe.Pipe;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.UnknownPeerException;
import chabernac.protocol.routing.WebPeer;
import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.UserInfo.Status;
import chabernac.testingutils.DeliveryReportCollector;
import chabernac.testingutils.EchoPipeListener;
import chabernac.testingutils.FileHandler;
import chabernac.testingutils.MessageCollector;
import chabernac.testingutils.MessagePrinter;
import chabernac.testingutils.UserInfoProvider;

public class P2PFacadeTest extends TestCase {
  private static Logger LOGGER = Logger.getLogger(P2PFacadeTest.class);

  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void testP2PSendMessage() throws P2PFacadeException, InterruptedException, ExecutionException{
    LOGGER.debug("Executing test " + new Exception().getStackTrace()[0].getMethodName());
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 20 );

    Thread.sleep(1000);

    DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
    theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 20 );

    System.out.println("testP2PSendMessage Peer id: " + theFacade1.getPeerId());
    System.out.println("testP2PSendMessage Peer id: " + theFacade2.getPeerId());

    Thread.sleep( 2000 );

    assertTrue( theFacade1.getRoutingTable().containsEntryForPeer( theFacade2.getPeerId() ) );
    assertTrue( theFacade2.getRoutingTable().containsEntryForPeer( theFacade1.getPeerId() ) );

    try{
      MessageCollector theMessageCollector = new MessageCollector();
      theFacade2.addMessageListener( theMessageCollector );

      MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "test message" )
      .addDestination( theFacade2.getPeerId() );

      assertNotNull(  theFacade1.sendEncryptedMessage( theMessage, Executors.newFixedThreadPool( 1 ) ).get() );

      Thread.sleep( 4000 );

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
    LOGGER.debug("Executing test " + new Exception().getStackTrace()[0].getMethodName());
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
    LOGGER.debug("Executing test " + new Exception().getStackTrace()[0].getMethodName());
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
    LOGGER.debug("Executing test " + new Exception().getStackTrace()[0].getMethodName());
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
    LOGGER.debug("Executing test " + new Exception().getStackTrace()[0].getMethodName());
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
    LOGGER.debug("Executing test " + new Exception().getStackTrace()[0].getMethodName());
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
      Thread.sleep( 2000 );

      theUserInfoOfFacade1 = theFacade2.getUserInfo().get( theFacade1.getPeerId() );
      assertEquals( "Chauliac", theUserInfoOfFacade1.getName() );
      assertEquals( "guy.chauliac@axa.be", theUserInfoOfFacade1.getEMail() );
    }finally{
      theFacade1.stop();
      theFacade2.stop();
    }
  }

  public void testMessageArchive() throws P2PFacadeException, InterruptedException, ExecutionException{
    LOGGER.debug("Executing test " + new Exception().getStackTrace()[0].getMethodName());
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

        Thread.sleep( 3000 );

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
    LOGGER.debug("Executing test " + new Exception().getStackTrace()[0].getMethodName());
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

      //TODO find a way to set AlreadyRunningException as cause
      //      assertTrue(e.getCause() instanceof AlreadyRunningException);
    } finally {
      if(theFacade1 != null) theFacade1.stop();
      if(theFacade2 != null) theFacade2.stop();
    }
  }

  public void testChannel() throws P2PFacadeException, InterruptedException{
    LOGGER.debug("Executing test " + new Exception().getStackTrace()[0].getMethodName());
    P2PFacade theFacade1 = null;
    P2PFacade theFacade2 = null;
    P2PFacade theFacade3 = null;
    P2PFacade theFacade4 = null;

    try{
      theFacade1 = new P2PFacade()
      .setExchangeDelay( 300 )
      .setPersist( false )
      .setKeepRoutingTableHistory( true )
      .setChannel("A")
      .start( 10 );

      theFacade2 = new P2PFacade()
      .setExchangeDelay( 300 )
      .setPersist( false )
      .setKeepRoutingTableHistory( true )
      .setChannel("A")
      .start( 10 );

      theFacade3 = new P2PFacade()
      .setExchangeDelay( 300 )
      .setPersist( false )
      .setKeepRoutingTableHistory( true )
      .setChannel("B")
      .start( 10 );

      theFacade4 = new P2PFacade()
      .setExchangeDelay( 300 )
      .setPersist( false )
      .setKeepRoutingTableHistory( true )
      .setChannel("B")
      .start( 10 );

      Thread.sleep(4000);

      assertTrue(theFacade1.getUserInfo().containsKey(theFacade1.getPeerId()));
      assertTrue(theFacade1.getUserInfo().containsKey(theFacade2.getPeerId()));
      assertFalse(theFacade1.getUserInfo().containsKey(theFacade3.getPeerId()));
      assertFalse(theFacade1.getUserInfo().containsKey(theFacade4.getPeerId()));

      assertTrue(theFacade2.getUserInfo().containsKey(theFacade1.getPeerId()));
      assertTrue(theFacade2.getUserInfo().containsKey(theFacade2.getPeerId()));
      assertFalse(theFacade2.getUserInfo().containsKey(theFacade3.getPeerId()));
      assertFalse(theFacade2.getUserInfo().containsKey(theFacade4.getPeerId()));

      assertFalse(theFacade3.getUserInfo().containsKey(theFacade1.getPeerId()));
      assertFalse(theFacade3.getUserInfo().containsKey(theFacade2.getPeerId()));
      assertTrue(theFacade3.getUserInfo().containsKey(theFacade3.getPeerId()));
      assertTrue(theFacade3.getUserInfo().containsKey(theFacade4.getPeerId()));

      assertFalse(theFacade4.getUserInfo().containsKey(theFacade1.getPeerId()));
      assertFalse(theFacade4.getUserInfo().containsKey(theFacade2.getPeerId()));
      assertTrue(theFacade4.getUserInfo().containsKey(theFacade3.getPeerId()));
      assertTrue(theFacade4.getUserInfo().containsKey(theFacade4.getPeerId()));
    } finally{
      if(theFacade1 != null) theFacade1.stop();
      if(theFacade2 != null) theFacade2.stop();
      if(theFacade3 != null) theFacade3.stop();
      if(theFacade4 != null) theFacade4.stop();
    }
  }

  public void testSetInfoObject() throws P2PFacadeException, InterruptedException{
    LOGGER.debug("Executing test " + new Exception().getStackTrace()[0].getMethodName());
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setInfoObject( "test", "test1" )
    .start( 20 );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setInfoObject( "test", "test2" )
    .setPersist( false )
    .start( 20 );

    Thread.sleep( 2000 );

    try{
      assertTrue( theFacade1.getInfoMap().containsKey( theFacade1.getPeerId() ));
      assertEquals( "test1", theFacade1.getInfoMap().get( theFacade1.getPeerId() ).get( "test" ));

      assertTrue( theFacade1.getInfoMap().containsKey( theFacade2.getPeerId() ));
      assertEquals( "test2", theFacade1.getInfoMap().get( theFacade2.getPeerId() ).get( "test" ));

      assertTrue( theFacade2.getInfoMap().containsKey( theFacade1.getPeerId() ));
      assertEquals( "test1", theFacade2.getInfoMap().get( theFacade1.getPeerId() ).get( "test" ));

      assertTrue( theFacade2.getInfoMap().containsKey( theFacade2.getPeerId() ));
      assertEquals( "test2", theFacade2.getInfoMap().get( theFacade2.getPeerId() ).get( "test" ));

      //now modify when running
      theFacade1.setInfoObject( "test", "test1running" );
      theFacade2.setInfoObject( "test", "test2running" );

      //give some time to synchronize

      Thread.sleep( 2000 );

      assertTrue( theFacade1.getInfoMap().containsKey( theFacade1.getPeerId() ));
      assertEquals( "test1running", theFacade1.getInfoMap().get( theFacade1.getPeerId() ).get( "test" ));

      assertTrue( theFacade1.getInfoMap().containsKey( theFacade2.getPeerId() ));
      assertEquals( "test2running", theFacade1.getInfoMap().get( theFacade2.getPeerId() ).get( "test" ));

      assertTrue( theFacade2.getInfoMap().containsKey( theFacade1.getPeerId() ));
      assertEquals( "test1running", theFacade2.getInfoMap().get( theFacade1.getPeerId() ).get( "test" ));

      assertTrue( theFacade2.getInfoMap().containsKey( theFacade2.getPeerId() ));
      assertEquals( "test2running", theFacade2.getInfoMap().get( theFacade2.getPeerId() ).get( "test" ));

    } finally {
      if(theFacade1 != null) theFacade1.stop();
      if(theFacade2 != null) theFacade2.stop();
    }
  }



  public void testMessageResender() throws P2PFacadeException, InterruptedException, UnknownPeerException, ExecutionException{
    LOGGER.debug("Executing test " + new Exception().getStackTrace()[0].getMethodName());
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setInfoObject( "test", "test1" )
    .setMessageResenderActivated( true )
    .start( 20 );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setInfoObject( "test", "test2" )
    .setPersist( false )
    .setMessageResenderActivated( true )
    .start( 20 );

    String thePeerId2 = theFacade2.getPeerId();

    MessageCollector theCollector = new MessageCollector();
    theFacade2.addMessageListener( theCollector );

    DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
    theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

    Thread.sleep( 2000 );

    assertNotNull( theFacade1.getFailedMessageResender() );
    assertNotNull( theFacade2.getFailedMessageResender() );

    assertTrue( theFacade1.getRoutingTable().containsEntryForPeer( theFacade2.getPeerId() ));
    assertTrue( theFacade2.getRoutingTable().containsEntryForPeer( theFacade1.getPeerId() ));

    try{
      theFacade2.stop();

      Thread.sleep( 3000 );

      assertFalse( theFacade1.getRoutingTable().getEntryForPeer( thePeerId2 ).isReachable() );

      MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "test" )
      .addDestination( thePeerId2 );

      assertTrue( theFacade1.sendMessage( theMessage, Executors.newFixedThreadPool( 1 ) ).get() );

      //give the system some time to detect that the peer is unreachble and collect the message int the message resender
      Thread.sleep( 3000 );

      assertEquals( 0, theCollector.getMessages().size() );
      assertEquals( 1, theFacade1.getFailedMessageResender().getNrOfMessagesWaitingForResend() );

      theFacade2 = new P2PFacade()
      .setExchangeDelay( 300 )
      .setInfoObject( "test", "test2" )
      .setPersist( false )
      .setPeerId( thePeerId2 )
      .setMessageResenderActivated( true )
      .start( 20 );

      theFacade2.addMessageListener( theCollector );

      Thread.sleep( 3000 );

      //the message resender should be informed that peer 2 has come online and try to resend the message

      assertEquals( 1, theCollector.getMessages().size() );
      assertEquals( 0, theFacade1.getFailedMessageResender().getNrOfMessagesWaitingForResend() );


    } finally {
      if(theFacade1 != null) theFacade1.stop();
      if(theFacade2 != null) theFacade2.stop();
    }
  }

  public void testChangeUserInfoRemotely() throws P2PFacadeException, InterruptedException{
    LOGGER.debug("Executing test " + new Exception().getStackTrace()[0].getMethodName());
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setInfoObject( "test", "test1" )
    .start( 20 );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setInfoObject( "test", "test2" )
    .setPersist( false )
    .start( 20 );

    Thread.sleep( 2000 );

    try{
      theFacade2.changeRemoteUserStatus( theFacade1.getPersonalInfo().getId(), Status.AWAY );
      Thread.sleep( 1000 );
      assertEquals( Status.AWAY, theFacade1.getPersonalInfo().getStatus() );

      theFacade2.changeRemoteUserStatus( theFacade1.getPersonalInfo().getId(), Status.BUSY);
      Thread.sleep( 1000 );
      assertEquals( Status.BUSY, theFacade1.getPersonalInfo().getStatus() );

      theFacade2.changeRemoteUserStatus( theFacade1.getPersonalInfo().getId(), Status.ONLINE);
      Thread.sleep( 1000 );
      assertEquals( Status.ONLINE, theFacade1.getPersonalInfo().getStatus() );

      theFacade2.changeRemoteUserStatus( theFacade1.getPersonalInfo().getId(), Status.OFFLINE);
      Thread.sleep( 1000 );
      assertEquals( Status.OFFLINE, theFacade1.getPersonalInfo().getStatus() );

      theFacade2.changeRemoteUserStatus( theFacade1.getPersonalInfo().getId(), Status.AWAY, "a" );
      Thread.sleep( 1000 );
      assertEquals( Status.AWAY, theFacade1.getPersonalInfo().getStatus() );
      assertEquals( "a", theFacade1.getPersonalInfo().getStatusMessage() );

      theFacade2.changeRemoteUserStatus( theFacade1.getPersonalInfo().getId(), Status.BUSY, "b");
      Thread.sleep( 1000 );
      assertEquals( Status.BUSY, theFacade1.getPersonalInfo().getStatus() );
      assertEquals( "b", theFacade1.getPersonalInfo().getStatusMessage() );

      theFacade2.changeRemoteUserStatus( theFacade1.getPersonalInfo().getId(), Status.ONLINE, "c");
      Thread.sleep( 1000 );
      assertEquals( Status.ONLINE, theFacade1.getPersonalInfo().getStatus() );
      assertEquals( "c", theFacade1.getPersonalInfo().getStatusMessage() );

      theFacade2.changeRemoteUserStatus( theFacade1.getPersonalInfo().getId(), Status.OFFLINE, "d");
      Thread.sleep( 1000 );
      assertEquals( Status.OFFLINE, theFacade1.getPersonalInfo().getStatus() );
      assertEquals( "d", theFacade1.getPersonalInfo().getStatusMessage() );
    } finally {
      if(theFacade1 != null) theFacade1.stop();
      if(theFacade2 != null) theFacade2.stop();
    }
  }

  public void testStressWebToPeer() throws P2PFacadeException, MalformedURLException, InterruptedException{

    P2PFacade theWebPeer = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setWebNode( true) 
    .setWebPort( 8080 )
    .setWebURL( new URL("http://localhost:8080") )
    .start(20);


    P2PFacade theSocketPeer = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .addSuperNode( "http://localhost:8080" )
    .start( 20 );


    theSocketPeer.scanSuperNodes();

    Thread.sleep( 2000 );

    try{
      assertTrue( theSocketPeer.getRoutingTable().containsEntryForPeer( theWebPeer.getRoutingTable().getLocalPeerId() ));
      assertTrue( theWebPeer.getRoutingTable().containsEntryForPeer( theSocketPeer.getRoutingTable().getLocalPeerId() ));

      int times = 300;
      
      final CountDownLatch theLatch = new CountDownLatch(times);
      
      MessageCollector theCollector = new MessageCollector();
      theSocketPeer.addMessageListener( theCollector );
      theSocketPeer.addMessageListener(new iMultiPeerMessageListener(){
        @Override
        public void messageReceived(MultiPeerMessage aMessage) {
          theLatch.countDown();
        }
      });
      theSocketPeer.addMessageListener(new MessagePrinter());

      ExecutorService theSendService = Executors.newSingleThreadExecutor();


      for(int i=0;i<times;i++){
        MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "test" + i )
        .addDestination( theSocketPeer.getPeerId() );
        theWebPeer.sendMessage( theMessage, theSendService );
      }
      
      theLatch.await(20, TimeUnit.SECONDS);

      assertEquals( times, theCollector.getMessages().size() );

    } finally {
      if(theWebPeer != null) theWebPeer.stop();
      if(theSocketPeer != null) theSocketPeer.stop();
    }
  }

  public void testSetWebURL() throws MalformedURLException, P2PFacadeException, UnknownPeerException{
    P2PFacade theFacade = new P2PFacade()
    .setWebNode( true )
    .setWebPort( 8080 )
    .setPersist( false )
    .setWebURL( new URL("http://localhost:8080/") )
    .start();

    try{
      AbstractPeer thePeer = theFacade.getRoutingTable().getEntryForLocalPeer().getPeer();

      assertTrue( thePeer instanceof WebPeer );

      WebPeer theLocalPeer = (WebPeer)thePeer;

      assertEquals( "http://localhost:8080/", theLocalPeer.getURL().toString() );

      try{
        theFacade.setWebURL( new URL("http://localhost:8080/") );
        fail("An exception must have been thrown");
      }catch(Exception e){
      }
    }finally {
      if(theFacade != null){
        theFacade.stop();
      }
    }

    try{
      theFacade = new P2PFacade()
      .setWebNode( false )
      .setWebURL( new URL("http://localhost:8080/") )
      .start();
      fail("An exception must have been thrown");
    }catch(Exception e){
    }
  }

  public void testSetWebPort() throws MalformedURLException, P2PFacadeException{
    P2PFacade theFacade = new P2PFacade()
    .setWebNode( true )
    .setWebPort( 8080 )
    .setPersist( false )
    .setWebURL( new URL("http://localhost:8080/") )
    .start();

    try{
      theFacade.setWebPort( 9090 );
      fail("An exception must have been thrown");
    }catch(Exception e){
    }

    try{
      theFacade = new P2PFacade()
      .setWebNode( false )
      .setWebPort( 9090 )
      .start();
      fail("An exception must have been thrown");
    }catch(Exception e){
    }

    if(theFacade != null){
      theFacade.stop();
    }
  }
  
  public void testSetAJPPort() throws P2PFacadeException, UnknownHostException, IOException{
    P2PFacade theFacade = new P2PFacade()
    .setWebNode( true )
    .setWebPort( 8080 )
    .setAJPPort( 9090 )
    .setPersist( false )
    .setWebURL( new URL("http://localhost:8080/") )
    .start();

    
    //test if the AJP port is effectively open
    
    Socket theSocket = new Socket( "localhost", 9090 );
    assertTrue( theSocket.isBound() );
    
    try{
      theFacade.setAJPPort( 9091 );
      fail("An exception must have been thrown");
    }catch(Exception e){
    }

    try{
      theFacade = new P2PFacade()
      .setWebNode( false )
      .setAJPPort( 9091 )
      .start();
      fail("An exception must have been thrown");
    }catch(Exception e){
    }

    if(theFacade != null){
      theFacade.stop();
    }
  }

  public void testSetWebNode() throws P2PFacadeException, MalformedURLException, UnknownPeerException{
    P2PFacade theFacade = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setInfoObject( "test", "test1" )
    .start( 20 );

    try{
      theFacade.setWebNode( true );
      fail("An exception must have been thrown");
    } catch(Exception e){
    }finally{
      if(theFacade != null){
        theFacade.stop();
      }
    }

    theFacade = new P2PFacade()
    .setWebNode( true )
    .setWebPort( 8080 )
    .setPersist( false )
    .setWebURL( new URL("http://localhost:8080/") )
    .start();

    try{
      AbstractPeer thePeer = theFacade.getRoutingTable().getEntryForLocalPeer().getPeer();

      assertTrue( thePeer instanceof WebPeer );
    } finally {
      if(theFacade != null){
        theFacade.stop();
      }
    }

  }


}
