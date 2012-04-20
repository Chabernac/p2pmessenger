/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import junit.framework.TestCase;

public class ByteBufferTest extends TestCase {
  public void testByteBuffer(){
    ByteBuffer theBuffer = ByteBuffer.allocate( 2 );
    theBuffer.order( ByteOrder.BIG_ENDIAN );
    theBuffer.put( (byte)5 );
    theBuffer.put( (byte)2 );
//    System.out.println(theBuffer.get
  }
}
