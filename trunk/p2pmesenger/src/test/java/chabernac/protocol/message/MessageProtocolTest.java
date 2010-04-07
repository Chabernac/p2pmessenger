/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.net.SocketException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;

public class MessageProtocolTest extends AbstractProtocolTest {
  private static Logger LOGGER = Logger.getLogger(MessageProtocolTest.class);
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void testMessageProtocol() throws ProtocolException, InterruptedException, SocketException{
    LOGGER.debug("Begin of testMessageProtocol");
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    ProtocolContainer theProtocol3 = getProtocolContainer( -1, false, "3" );
    ProtocolServer theServer3 = new ProtocolServer(theProtocol3, RoutingProtocol.START_PORT + 2, 5);


    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable1 = theRoutingProtocol1.getRoutingTable();
    MessageProtocol theMessageProtocol1 = (MessageProtocol)theProtocol1.getProtocol( MessageProtocol.ID );
    
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable2 = theRoutingProtocol2.getRoutingTable();
    
    RoutingProtocol theRoutingProtocol3 = (RoutingProtocol)theProtocol3.getProtocol( RoutingProtocol.ID );
    RoutingTable theRoutingTable3 = theRoutingProtocol3.getRoutingTable();
    
    theRoutingProtocol1.getLocalUnreachablePeerIds().add( "3" );
    theRoutingProtocol3.getLocalUnreachablePeerIds().add( "1" );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      assertTrue( theServer3.start() );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol3.scanLocalSystem();
      
      //scanning the local system might take a small time
      Thread.sleep( 1000 );
      
      //after a local system scan we must at least know our selfs
      assertNotNull( theRoutingTable1.getEntryForLocalPeer() );
      assertNotNull( theRoutingTable2.getEntryForLocalPeer() );
      assertNotNull( theRoutingTable3.getEntryForLocalPeer() );

      for(int i=0;i<5;i++){
        theRoutingProtocol1.exchangeRoutingTable();
        theRoutingProtocol2.exchangeRoutingTable();
        theRoutingProtocol3.exchangeRoutingTable();
      }
      
      RoutingTableEntry theRoutingTableEntry = theRoutingTable1.getEntryForPeer( "3" );
      
      assertEquals( 2, theRoutingTableEntry.getHopDistance() );
      
      Message theMessage = new Message();
      theMessage.setDestination( theRoutingTable1.getEntryForPeer( "3" ).getPeer() );
      theMessage.setMessage( "ECOTest" );
      assertEquals( "Test", theMessageProtocol1.handleMessage( 0, theMessage ));
      
      
    }finally{
      theServer1.stop();
      theServer2.stop();
      theServer3.stop();
    }
  }
}

