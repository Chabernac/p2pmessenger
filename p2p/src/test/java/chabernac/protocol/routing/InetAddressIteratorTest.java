/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

public class InetAddressIteratorTest extends TestCase {
  public void testIterator() throws UnknownHostException{
    InetAddresIterator theIterator = new InetAddresIterator(InetAddress.getByName ( "192.168.15.100"), 10);

    assertTrue( theIterator.hasNext() );
    assertEquals( "192.168.15.101", theIterator.next());
    assertTrue( theIterator.hasNext() );
    assertEquals( "192.168.15.99", theIterator.next() );
    assertTrue( theIterator.hasNext() );
    assertEquals( "192.168.15.102", theIterator.next() );
    assertTrue( theIterator.hasNext() );
    assertEquals( "192.168.15.98", theIterator.next() );

    theIterator = new InetAddresIterator(InetAddress.getByName ( "192.168.15.1"), 10);

    assertTrue( theIterator.hasNext() );
    assertEquals( "192.168.15.2", theIterator.next());
    assertTrue( theIterator.hasNext() );
    assertEquals( "192.168.14.255", theIterator.next() );
    assertTrue( theIterator.hasNext() );
    assertEquals( "192.168.15.3", theIterator.next() );
    assertTrue( theIterator.hasNext() );
    assertEquals( "192.168.14.254", theIterator.next() );

    theIterator = new InetAddresIterator(InetAddress.getByName ( "192.168.255.255"), 10);

    assertTrue( theIterator.hasNext() );
    assertEquals( "192.169.0.1", theIterator.next());
    assertTrue( theIterator.hasNext() );
    assertEquals( "192.168.255.254", theIterator.next() );
    assertTrue( theIterator.hasNext() );
    assertEquals( "192.169.0.2", theIterator.next() );
    assertTrue( theIterator.hasNext() );
    assertEquals( "192.168.255.253", theIterator.next() );

    theIterator = new InetAddresIterator(InetAddress.getByName ( "192.168.255.255"), 10);

    for(int i=0;i<10;i++){
      assertTrue( theIterator.hasNext() );
      theIterator.next();
    }

    assertFalse( theIterator.hasNext() );

  }
}

