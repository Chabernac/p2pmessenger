/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import junit.framework.TestCase;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.echo.EchoProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;

public class MessageProtocolTest extends TestCase {

  public void testMessageProtocol(){
    RoutingTable theRoutingTable1 = new RoutingTable("1");
    ProtocolContainer theProtocol1 = new ProtocolContainer();
    MessageProtocol theMessageProtocol1 = new MessageProtocol(theRoutingTable1); 
    RoutingProtocol theRoutingProtocol1 = new RoutingProtocol(theRoutingTable1, -1, false);
    theProtocol1.addProtocol( theRoutingProtocol1 );
    theProtocol1.addProtocol( theMessageProtocol1 );
    theProtocol1.addProtocol( new EchoProtocol() );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    RoutingTable theRoutingTable2 = new RoutingTable("2");
    ProtocolContainer theProtocol2 = new ProtocolContainer();
    RoutingProtocol theRoutingProtocol2 = new RoutingProtocol(theRoutingTable2, -1, false);
    theProtocol2.addProtocol( theRoutingProtocol2 );
    theProtocol2.addProtocol( new MessageProtocol(theRoutingTable2) );
    theProtocol2.addProtocol( new EchoProtocol() );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    RoutingTable theRoutingTable3 = new RoutingTable("3");
    ProtocolContainer theProtocol3 = new ProtocolContainer();
    RoutingProtocol theRoutingProtocol3 = new RoutingProtocol(theRoutingTable3, -1, false);
    theProtocol3.addProtocol( theRoutingProtocol3 );
    theProtocol3.addProtocol( new MessageProtocol(theRoutingTable3) );
    theProtocol3.addProtocol( new EchoProtocol() );
    ProtocolServer theServer3 = new ProtocolServer(theProtocol3, RoutingProtocol.START_PORT + 2, 5);


    theRoutingProtocol1.getLocalUnreachablePeerIds().add( "3" );
    theRoutingProtocol3.getLocalUnreachablePeerIds().add( "1" );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      assertTrue( theServer3.start() );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol3.scanLocalSystem();

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

