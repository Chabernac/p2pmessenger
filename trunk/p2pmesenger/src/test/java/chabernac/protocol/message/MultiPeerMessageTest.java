/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import junit.framework.TestCase;

public class MultiPeerMessageTest extends TestCase {
  public void testMultiMessagePeer(){
    MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "1", "message" );
    assertEquals( "1", theMessage.getSource());
    assertEquals( "message", theMessage.getMessage() );
    assertEquals( 0, theMessage.getDestinations().size());
    assertEquals( 0, theMessage.getIndicators().size() );
    
    MultiPeerMessage theNewMessage = theMessage.addDestination( "2" );
    assertEquals( 1, theNewMessage.getDestinations().size() );
    assertEquals( 0, theMessage.getDestinations().size());
    
    theNewMessage = theMessage.addMessageIndicator( MessageIndicator.ENCRYPTED );
    assertEquals( 1, theNewMessage.getIndicators().size());
    assertEquals( 0, theMessage.getIndicators().size());
  }
}
