/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.keyexchange;

import java.io.File;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.encryption.EncryptionException;
import chabernac.encryption.StoredPrivateKeyEncryption;
import chabernac.encryption.iPublicPrivateKeyEncryption;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.pipe.PipeProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

public class KeyExchangeProtocolTest extends TestCase {
  
  static{
    BasicConfigurator.configure();
  }
  
  public void testKeyExchangeProtocol() throws InterruptedException, EncryptionException{
    //p1 <--> p2 <--> p3 peer 1 cannot reach peer 3

    RoutingTable theRoutingTable1 = new RoutingTable("1");
    ProtocolContainer theProtocol1 = new ProtocolContainer();
    RoutingProtocol theRoutingProtocol1 = new RoutingProtocol(theRoutingTable1, -1, false);
    theProtocol1.addProtocol( theRoutingProtocol1 );
    PipeProtocol thePipeProtocol1 = new PipeProtocol(theRoutingTable1, 5);
    theProtocol1.addProtocol( thePipeProtocol1 );
    theProtocol1.addProtocol( new MessageProtocol(theRoutingTable1) );
    iPublicPrivateKeyEncryption theEncryption1 = new StoredPrivateKeyEncryption(new File("i:\\"), "1");
    KeyExchangeProtocol theKeyExchangeProtocol1 = new KeyExchangeProtocol(theEncryption1);
    theProtocol1.addProtocol( theKeyExchangeProtocol1 );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);


    RoutingTable theRoutingTable2 = new RoutingTable("2");
    ProtocolContainer theProtocol2 = new ProtocolContainer();
    RoutingProtocol theRoutingProtocol2 = new RoutingProtocol(theRoutingTable2, -1, false);
    theProtocol2.addProtocol( theRoutingProtocol2 );
    PipeProtocol thePipeProtocol2 = new PipeProtocol(theRoutingTable2, 5);
    theProtocol2.addProtocol( thePipeProtocol2 );
    theProtocol2.addProtocol( new MessageProtocol(theRoutingTable2) );
    iPublicPrivateKeyEncryption theEncryption2 = new StoredPrivateKeyEncryption(new File("i:\\"),"2");
    KeyExchangeProtocol theKeyExchangeProtocol2 = new KeyExchangeProtocol(theEncryption2);
    theProtocol2.addProtocol( theKeyExchangeProtocol2 );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);

    RoutingTable theRoutingTable3 = new RoutingTable("3");
    ProtocolContainer theProtocol3 = new ProtocolContainer();
    RoutingProtocol theRoutingProtocol3 = new RoutingProtocol(theRoutingTable3, -1, false);
    theProtocol3.addProtocol( theRoutingProtocol3 );
    PipeProtocol thePipeProtocol3 = new PipeProtocol(theRoutingTable3, 5);
    theProtocol3.addProtocol( thePipeProtocol3 );
    theProtocol3.addProtocol( new MessageProtocol(theRoutingTable3) );
    iPublicPrivateKeyEncryption theEncryption3 = new StoredPrivateKeyEncryption(new File("i:\\"), "3");
    KeyExchangeProtocol theKeyExchangeProtocol3 = new KeyExchangeProtocol(theEncryption3);
    theProtocol3.addProtocol( theKeyExchangeProtocol3 );
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
      
      for(int i=0;i<3;i++){
        theRoutingProtocol1.exchangeRoutingTable();
        theRoutingProtocol2.exchangeRoutingTable();
        theRoutingProtocol3.exchangeRoutingTable();
      }
      
      
      assertTrue( theKeyExchangeProtocol1.assurePublicKeyForPeer( "3" ) );
      assertNotNull( theEncryption1.getPublicKeyForUser( "3" ) );
    } finally{
      theServer1.stop();
      theServer2.stop();
      theServer3.stop();
    }

  }
}
