/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;

import junit.framework.TestCase;
import chabernac.protocol.message.MessageIndicator;
import chabernac.protocol.message.MultiPeerMessage;

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
  }
}
