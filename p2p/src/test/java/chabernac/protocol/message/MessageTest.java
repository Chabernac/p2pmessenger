/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

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
    //TODO implement a test for Message.copy();
  }
}
