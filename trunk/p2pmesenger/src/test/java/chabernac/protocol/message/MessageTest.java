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
  }
}
