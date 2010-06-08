/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.routing.RoutingProtocol;

public class MultiPeerMessageProtocolTest extends AbstractProtocolTest {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testMultiPeerMessageProtocol() throws ProtocolException, InterruptedException{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "x" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "y" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5); 
    
    ProtocolContainer theProtocol3 = getProtocolContainer( -1, false, "z" );
    ProtocolServer theServer3 = new ProtocolServer(theProtocol3, RoutingProtocol.START_PORT + 2, 5);
    
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    MultiPeerMessageProtocol theMessageProtocol1 = (MultiPeerMessageProtocol)theProtocol1.getProtocol( MultiPeerMessageProtocol.ID );
    
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    MultiPeerMessageProtocol theMessageProtocol2 = (MultiPeerMessageProtocol)theProtocol2.getProtocol( MultiPeerMessageProtocol.ID );
    
    RoutingProtocol theRoutingProtocol3 = (RoutingProtocol)theProtocol3.getProtocol( RoutingProtocol.ID );
    MultiPeerMessageProtocol theMessageProtocol3 = (MultiPeerMessageProtocol)theProtocol3.getProtocol( MultiPeerMessageProtocol.ID );
    
    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      assertTrue( theServer3.start() );
      
      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol3.scanLocalSystem();
      
      //scanning the local system might take a small time
      Thread.sleep( 1000 );
      
      DeliverReportCollector theDeliveryReportCollector = new DeliverReportCollector();
      theMessageProtocol1.addDeliveryReportListener( theDeliveryReportCollector );
      
      MessageCollector theMessageCollector2 = new MessageCollector();
      theMessageProtocol2.addMultiPeerMessageListener( theMessageCollector2);
      
      MessageCollector theMessageCollector3 = new MessageCollector();
      theMessageProtocol3.addMultiPeerMessageListener( theMessageCollector3);
      
      MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "berichtje")
      .addDestination( "y" )
      .addDestination( "z" )
      .addMessageIndicator( MessageIndicator.TO_BE_ENCRYPTED );
      
      theMessageProtocol1.sendMessage( theMessage );
      
      Thread.sleep( 2000 );
      
      assertEquals( 4, theDeliveryReportCollector.getDeliveryReports().size() );
      assertEquals( DeliveryReport.Status.DELIVERED, theDeliveryReportCollector.getDeliveryReports().get( 2 ).getDeliveryStatus());
      assertEquals( DeliveryReport.Status.DELIVERED, theDeliveryReportCollector.getDeliveryReports().get( 3 ).getDeliveryStatus());
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
    private List<DeliveryReport> myDeliveryReports = new ArrayList< DeliveryReport >();

    @Override
    public void acceptDeliveryReport( DeliveryReport aDeliverReport ) {
      myDeliveryReports.add(aDeliverReport);
    }
    
    public List<DeliveryReport> getDeliveryReports(){
      return myDeliveryReports;
    }
    
  }
}
