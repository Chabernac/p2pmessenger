/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.facade;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.message.DeliveryReport;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.testingutils.DeliveryReportCollector;
import chabernac.testingutils.FileHandler;
import chabernac.testingutils.MessageCollector;

public class P2PFacadeTest extends TestCase {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void testP2PSendMessage() throws P2PFacadeException, InterruptedException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPeerId( "a" )
    .setPersist( false )
    .start( 5 );

    DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
    theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPeerId( "b" )
    .setPersist( false )
    .start( 5 );

    Thread.sleep( 2000 );

    try{
      MessageCollector theMessageCollector = new MessageCollector();
      theFacade2.addMessageListener( theMessageCollector );

      MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "test message" )
      .addDestination( "b" );

      theFacade1.sendEncryptedMessage( theMessage );

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

  public void testFailMessage() throws P2PFacadeException, InterruptedException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPeerId( "3" )
    .setPersist( false )
    .start( 5 );

    Thread.sleep( 2000 );
    try{
      DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
      theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

      MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "test message" )
      .addDestination( "99" );

      theFacade1.sendEncryptedMessage( theMessage );

      Thread.sleep( 1000 );

      assertEquals( 1, theDeliveryReportCollector.getDeliveryReports().size() );
      assertEquals( DeliveryReport.Status.FAILED, theDeliveryReportCollector.getDeliveryReports().get( 0 ).getDeliveryStatus());
    }finally{
      theFacade1.stop();
    }
  }
  
  public void testSendFile() throws InterruptedException, P2PFacadeException, IOException{
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPeerId( "4" )
    .setPersist( false )
    .start( 5 );

    DeliveryReportCollector theDeliveryReportCollector = new DeliveryReportCollector();
    theFacade1.addDeliveryReportListener( theDeliveryReportCollector );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPeerId( "5" )
    .setPersist( false )
    .start( 5 );

    Thread.sleep( 2000 );

    File theFile = new File("test.txt");
    theFile.createNewFile();
    
    try{
      FileHandler theFilehandler = new FileHandler();
      theFacade2.setFileHandler( theFilehandler );
      theFacade1.sendFile( theFile, "5");
      
      assertEquals( 1, theFilehandler.getReceivedFiles().size());
      assertEquals( 0, theFilehandler.getFailedFiles().size());
    } finally{
      theFacade1.stop();
      theFacade2.stop();
      theFile.delete();
    }
  }
}
