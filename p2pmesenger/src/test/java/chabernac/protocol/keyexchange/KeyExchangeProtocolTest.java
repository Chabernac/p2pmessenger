/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.keyexchange;

import org.apache.log4j.BasicConfigurator;

import chabernac.encryption.EncryptionException;
import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.routing.RoutingProtocol;

public class KeyExchangeProtocolTest extends AbstractProtocolTest {
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testKeyExchangeProtocol() throws InterruptedException, EncryptionException, ProtocolException{
    //p1 <--> p2 <--> p3 peer 1 cannot reach peer 3

    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1", ".", "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2", ".", "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    ProtocolContainer theProtocol3 = getProtocolContainer( -1, false, "3", ".", "3" );
    ProtocolServer theServer3 = new ProtocolServer(theProtocol3, RoutingProtocol.START_PORT + 2, 5);

    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    KeyExchangeProtocol theKeyExchangeProtocol1 = (KeyExchangeProtocol)theProtocol1.getProtocol( KeyExchangeProtocol.ID );
    
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    RoutingProtocol theRoutingProtocol3 = (RoutingProtocol)theProtocol3.getProtocol( RoutingProtocol.ID );
    
    theRoutingProtocol1.getLocalUnreachablePeerIds().add( "3" );
    theRoutingProtocol3.getLocalUnreachablePeerIds().add( "1" );

    try{
      assertTrue( theServer1.start() );
      assertTrue( theServer2.start() );
      assertTrue( theServer3.start() );

      theRoutingProtocol1.scanLocalSystem();
      theRoutingProtocol2.scanLocalSystem();
      theRoutingProtocol3.scanLocalSystem();
      
      for(int i=0;i<3;i++){
        theRoutingProtocol1.exchangeRoutingTable();
        theRoutingProtocol2.exchangeRoutingTable();
        theRoutingProtocol3.exchangeRoutingTable();
      }
      
      
      assertTrue( theKeyExchangeProtocol1.assurePublicKeyForPeer( "3" ) );
      assertNotNull( theKeyExchangeProtocol1.getEncryption().getPublicKeyForUser( "3" ) );
    } finally{
      theServer1.stop();
      theServer2.stop();
      theServer3.stop();
    }

  }
}
