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
    
    theMessage = MultiPeerMessage.createMessage( "1", "message" )
    .setSource( "a" )
    .addDestination( "2" );
    
    theMessage = theMessage.reply();
    
    assertEquals( 1, theMessage.getDestinations().size());
    assertEquals( "a", theMessage.getDestinations().get( 0 ) );
    
    theMessage = MultiPeerMessage.createMessage( "a", "message" )
    .addDestination( "1" )
    .addDestination( "2" );
    
    assertEquals( 2, theMessage.getDestinations().size());
    assertTrue( theMessage.getDestinations().contains( "1" ));
    assertTrue( theMessage.getDestinations().contains( "2" ));

    theMessage = theMessage.replyAll();
    
    assertEquals( 3, theMessage.getDestinations().size());
    assertTrue( theMessage.getDestinations().contains( "a" ));
    assertTrue( theMessage.getDestinations().contains( "1" ));
    assertTrue( theMessage.getDestinations().contains( "2" ));
    
    theMessage = theMessage.removeDestination( "1" );
    assertEquals( 2, theMessage.getDestinations().size());
    assertFalse( theMessage.getDestinations().contains( "1" ));
    
    theMessage = theMessage.setMessage( "hopla" );
    assertEquals( "hopla", theMessage.getMessage() );
    
  }
}
