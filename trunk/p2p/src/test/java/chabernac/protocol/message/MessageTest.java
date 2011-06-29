/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
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
  
  public void testMessageSize() throws IOException{
    iObjectStringConverter< Message > myMessageConverter = new Base64ObjectStringConverter< Message >();
    Message theMessage = new Message();
    theMessage.setBytes( new byte[4096] );
    
    ByteArrayOutputStream theBytes = new ByteArrayOutputStream();
    ObjectOutputStream theOut = new ObjectOutputStream( theBytes );
    theOut.writeObject( theMessage );
    theOut.flush();
    System.out.println(theBytes.toByteArray().length);
    
    String theString = myMessageConverter.toString( theMessage );
    theOut = new ObjectOutputStream( theBytes );
    theOut.writeObject( theString );
    theOut.flush();
    System.out.println(theBytes.toByteArray().length);
    
    byte[] theResult = Base64.encodeBase64( new byte[4096], false );
    System.out.println(theResult.length);
    
  }
}
