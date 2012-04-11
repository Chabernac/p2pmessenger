/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.P2PServerFactoryException;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.iP2PServer;
import chabernac.protocol.encryption.EncryptionProtocol;
import chabernac.protocol.message.DeliveryReport.Status;
import chabernac.protocol.routing.RoutingProtocol;

public class MultiPeerMessageProtocolTest extends AbstractProtocolTest {
  private static Logger LOGGER = Logger.getLogger(MultiPeerMessageProtocolTest.class);
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testMultiPeerMessageProtocol() throws ProtocolException, InterruptedException, MessageException, P2PServerFactoryException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "x" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "y" );
    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1); 
    
    ProtocolContainer theProtocol3 = getProtocolContainer( -1, false, "z" );
    iP2PServer theServer3 = getP2PServer( theProtocol3, RoutingProtocol.START_PORT + 2);
    
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    MultiPeerMessageProtocol theMessageProtocol1 = (MultiPeerMessageProtocol)theProtocol1.getProtocol( MultiPeerMessageProtocol.ID );
    theProtocol1.getProtocol(EncryptionProtocol.ID);
    
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    MultiPeerMessageProtocol theMessageProtocol2 = (MultiPeerMessageProtocol)theProtocol2.getProtocol( MultiPeerMessageProtocol.ID );
    theProtocol2.getProtocol(EncryptionProtocol.ID);
    
    RoutingProtocol theRoutingProtocol3 = (RoutingProtocol)theProtocol3.getProtocol( RoutingProtocol.ID );
    MultiPeerMessageProtocol theMessageProtocol3 = (MultiPeerMessageProtocol)theProtocol3.getProtocol( MultiPeerMessageProtocol.ID );
    theProtocol3.getProtocol(EncryptionProtocol.ID);
    
    
    
    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      assertTrue( theServer3.start() );
      
      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol3.scanLocalSystem();
      
      //scanning the local system might take a small time
      Thread.sleep( SLEEP_AFTER_SCAN );
      
      CountDownLatch theLath = new CountDownLatch(4);
      DeliverReportCollector theDeliveryReportCollector = new DeliverReportCollector(theLath);
      theMessageProtocol1.addDeliveryReportListener( theDeliveryReportCollector );
      
      MessageCollector theMessageCollector2 = new MessageCollector();
      theMessageProtocol2.addMultiPeerMessageListener( theMessageCollector2);
      
      MessageCollector theMessageCollector3 = new MessageCollector();
      theMessageProtocol3.addMultiPeerMessageListener( theMessageCollector3);
      
      MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "berichtje")
      .addDestination( "y" )
      .addDestination( "z" )
      .addMessageIndicator( MessageIndicator.TO_BE_ENCRYPTED );
      
      LOGGER.debug("Sending multipeer message");
      theMessageProtocol1.sendMessage( theMessage );
      theLath.await(5, TimeUnit.SECONDS);
      
      //TODO why do we have to wait 5 seconds, what's causing the timeout?
      
      assertEquals( 4, theDeliveryReportCollector.getDeliveryReports().size() );
      
      //there should be 2 reports with status in progress, and 2 with status delivered
      int theDelivered = 0;
      int theInProgress = 0;
      for(DeliveryReport theReport : theDeliveryReportCollector.getDeliveryReports()){
        if(theReport.getDeliveryStatus() == Status.DELIVERED) theDelivered++;
        if(theReport.getDeliveryStatus() == Status.IN_PROGRESS) theInProgress++;
      }
      assertEquals( 2, theDelivered );
      assertEquals( 2, theInProgress);
      assertEquals( 1, theMessageCollector2.getMessages().size());
      assertEquals( 1, theMessageCollector3.getMessages().size());
      
            
    }finally{
      theServer1.stop();
      theServer2.stop();
      theServer3.stop();
    }
  }
  
  private class MessageCollector implements iMultiPeerMessageListener{
    private List< MultiPeerMessage > myReceivedMessages = new ArrayList< MultiPeerMessage >();

    @Override
    public void messageReceived( MultiPeerMessage aMessage ) {
      myReceivedMessages.add( aMessage );
    }
    
    public List<MultiPeerMessage> getMessages(){
      return myReceivedMessages;
    }
  }
  
  private class DeliverReportCollector implements iDeliverReportListener{
    private final CountDownLatch myLatch;
    private List<DeliveryReport> myDeliveryReports = new ArrayList< DeliveryReport >();

    
    public DeliverReportCollector(CountDownLatch myLatch) {
      super();
      this.myLatch = myLatch;
    }

    @Override
    public void acceptDeliveryReport( DeliveryReport aDeliverReport ) {
      myDeliveryReports.add(aDeliverReport);
      myLatch.countDown();
    }
    
    public List<DeliveryReport> getDeliveryReports(){
      return myDeliveryReports;
    }
    
  }
}
