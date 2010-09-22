/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;

import junit.framework.TestCase;
import chabernac.protocol.message.MessageIndicator;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.routing.SocketPeer;

public class Base64ObjectStringConverterTest extends TestCase {
  public void testBase64ObjectStringConverter() throws IOException{
    Base64ObjectStringConverter< MultiPeerMessage > theConverter = new Base64ObjectStringConverter< MultiPeerMessage >();
    
    MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "1", "message" )
    .addDestination( "1" )
    .addDestination( "2 ")
    .addMessageIndicator( MessageIndicator.ENCRYPTED );
    
    String theString = theConverter.toString( theMessage );
    
    assertNotNull( theString );
    
    MultiPeerMessage theNewMessage = theConverter.getObject( theString );
    
    assertEquals( theMessage, theNewMessage );
    
    Base64ObjectStringConverter< SocketPeer> theConverter2 = new Base64ObjectStringConverter< SocketPeer >();
    
    SocketPeer theSocketPeer = new SocketPeer("1", "x22P0212", 12700);
    SocketPeer theSocketPeer2 = new SocketPeer("1", "x22P0212", 12701);
    
    String theSocketPeerString = theConverter2.toString(theSocketPeer);
    assertNotNull(theSocketPeerString);
    SocketPeer theRestoredPeer = theConverter2.getObject(theSocketPeerString);
    
    assertEquals(theSocketPeer.getPeerId(), theRestoredPeer.getPeerId());
    assertEquals(theSocketPeer.getHosts().get(0), theRestoredPeer.getHosts().get(0));
    assertEquals(theSocketPeer.getPort(), theRestoredPeer.getPort());
    
    
    
  }
}
