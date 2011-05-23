/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import chabernac.protocol.routing.DummyPeer;
import junit.framework.TestCase;

public class MessageTest extends TestCase {
  public void testMessage(){
    Message theMessage = new Message();
    assertFalse( theMessage.containsIndicator( MessageIndicator.ENCRYPTED ));
    theMessage.addMessageIndicator( MessageIndicator.ENCRYPTED );
    assertTrue( theMessage.containsIndicator( MessageIndicator.ENCRYPTED ));
    theMessage.removeMessageIndicator( MessageIndicator.ENCRYPTED );
    assertFalse( theMessage.containsIndicator( MessageIndicator.ENCRYPTED ));
    theMessage.addMessageIndicator( MessageIndicator.TO_BE_ENCRYPTED );
    assertTrue( theMessage.containsIndicator( MessageIndicator.TO_BE_ENCRYPTED));
  }
  
  public void testTTL(){
    Message theMessage = new Message();
    assertEquals( 8, theMessage.getTTL() );
    theMessage.decreaseTTL();
    assertEquals( 7, theMessage.getTTL() );
    for(int i=0;i<10;i++){
      theMessage.decreaseTTL();
    }
    assertEquals( 0, theMessage.getTTL() );
    assertTrue( theMessage.isEndOfTTL() );
  }
  
  public void testMessageCopy(){
    Message theMessage = new Message();
    theMessage.setSource(new DummyPeer("1"));
    theMessage.setDestination(new DummyPeer("2"));
    theMessage.setMessage("test");
    theMessage.setMessageTimeoutInSeconds(4);
    theMessage.setProtocolMessage(true);
    theMessage.setTTL(3);
    
    Message theCopy = theMessage.copy();
    
    assertEquals("1", theCopy.getSource().getPeerId());
    assertEquals("2", theCopy.getDestination().getPeerId());
    assertEquals("test", theCopy.getMessage());
    assertEquals(4, theCopy.getMessageTimeoutInSeconds());
    assertEquals(true, theMessage.isProtocolMessage());
    assertEquals(3, theMessage.getTTL());
    
    theMessage.addHeader("HEADER_1", "a header");
    theMessage.addMessageIndicator(MessageIndicator.ENCRYPTED);
    
    theCopy = theMessage.copy();
    
    assertEquals("a header", theCopy.getHeader("HEADER_1"));
    assertTrue(theMessage.containsIndicator(MessageIndicator.ENCRYPTED));
  }
}
