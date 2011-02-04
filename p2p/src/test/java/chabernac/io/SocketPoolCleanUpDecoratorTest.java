/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import chabernac.tools.NetTools;

public class SocketPoolCleanUpDecoratorTest extends TestCase {
  public void testSocketPoolCleanUpDecorator() throws IOException, InterruptedException{
    SocketPoolCleanUpDecorator theDecorator = new SocketPoolCleanUpDecorator( new BasicSocketPool(), 5, TimeUnit.SECONDS );

    Thread.sleep( 2000 );

    ServerSocket theSocket = NetTools.openServerSocket( 20000 );

    try{
      SocketProxy theProxy = theDecorator.checkOut( new InetSocketAddress( "localhost", theSocket.getLocalPort() ) );
      assertTrue( theProxy.isConnected() );
      assertEquals( 1, theDecorator.getCheckedOutPool().size() );
      theDecorator.checkIn( theProxy );
      assertEquals( 0, theDecorator.getCheckedOutPool().size() );
      assertEquals( 1, theDecorator.getCheckedInPool().size() );

      Thread.sleep( 8000 );
      assertEquals( 0, theDecorator.getCheckedOutPool().size() );
      assertEquals( 0, theDecorator.getCheckedInPool().size() );
    }finally {
      theSocket.close();
    }
  }
}
