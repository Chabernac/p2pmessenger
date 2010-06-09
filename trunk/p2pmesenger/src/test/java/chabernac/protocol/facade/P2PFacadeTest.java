/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.facade;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.message.DeliveryReport;
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
    .start( 5 );

    DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
    theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 5 );

    Thread.sleep( 2000 );

    try{
      MessageCollector theMessageCollector = new MessageCollector();
      theFacade2.addMessageListener( theMessageCollector );

      MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "test message" )
      .addDestination( theFacade2.getPeerId() );

      assertTrue( theFacade1.sendEncryptedMessage( theMessage, Executors.newFixedThreadPool( 1 ) ).get() );

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
    .start( 5 );

    Thread.sleep( 2000 );
    try{
      DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
      theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

      MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "test message" )
      .addDestination( "99" );

      assertTrue( theFacade1.sendEncryptedMessage( theMessage, Executors.newFixedThreadPool( 1 ) ).get() );

      Thread.sleep( 1000 );

      assertEquals( 1, theDeliveryReportCollector.getDeliveryReports().size() );
      assertEquals( DeliveryReport.Status.FAILED, theDeliveryReportCollector.getDeliveryReports().get( 0 ).getDeliveryStatus());
    }finally{
      theFacade1.stop();
    }
  }
  
  public void testSendFile() throws InterruptedException, P2PFacadeException, IOException, ExecutionException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 5 );

    DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
    theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 5 );

    Thread.sleep( 2000 );

    File theFile = new File("test.txt");
    theFile.createNewFile();
    
    try{
      FileHandler theFilehandler = new FileHandler();
      theFacade2.setFileHandler( theFilehandler );
      assertTrue( theFacade1.sendFile( theFile, theFacade2.getPeerId(), Executors.newFixedThreadPool( 1 )).get() );
      
      assertEquals( 1, theFilehandler.getReceivedFiles().size());
      assertEquals( 0, theFilehandler.getFailedFiles().size());
    } finally{
      theFacade1.stop();
      theFacade2.stop();
      theFile.delete();
    }
  }
  
  public void testPipe() throws P2PFacadeException, InterruptedException, IOException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 5 );
    
    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .start( 5 );

    Thread.sleep( 2000 );
    
    try{
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
    .start( 5 );
    
    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( new UserInfoProvider("Leslie", "leslie.torreele@gmail.com") )
    .start( 5 );

    Thread.sleep( 2000 );
    
    try{
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
}
