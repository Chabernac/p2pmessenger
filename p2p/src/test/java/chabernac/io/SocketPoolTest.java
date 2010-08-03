/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.TestCase;
import chabernac.tools.NetTools;

public class SocketPoolTest extends TestCase {
  public void testSocketPool() throws IOException{
    ServerSocket theServerSocket = NetTools.openServerSocket( 1500 );

    try{
      SocketPool thePool = SocketPool.getInstance( );
      thePool.cleanUp();
      Socket theSocket1 = thePool.checkOut( new InetSocketAddress("localhost", theServerSocket.getLocalPort()) );
      Socket theSocket2 = thePool.checkOut( new InetSocketAddress("localhost", theServerSocket.getLocalPort()) );

      //we now have 2 checked out connections and none checked in
      assertEquals( 2, thePool.getCheckOutPool().size() );
      assertEquals( 0, thePool.getCheckInPool().size() );

      //check in 1 connections, we have 1 checked out and 1 checked in connections
      thePool.checkIn( theSocket1 );
      assertEquals( 1, thePool.getCheckOutPool().size() );
      assertEquals( 1, thePool.getCheckInPool().size() );

      //check in the other connections, we only have check in connections
      thePool.checkIn( theSocket2 );
      assertEquals( 0, thePool.getCheckOutPool().size() );
      assertEquals( 2, thePool.getCheckInPool().size() );

      //check out a connections which is in the checked in pool, one of the available sockets must be returned
      Socket theSocket3 = thePool.checkOut( new InetSocketAddress("localhost", theServerSocket.getLocalPort()) );
      assertEquals( 1, thePool.getCheckOutPool().size() );
      assertEquals( 1, thePool.getCheckInPool().size() );
      assertTrue( theSocket3 == theSocket1 || theSocket3 == theSocket2 );

      //the clean up should only clean the checked in connections
      thePool.cleanUp();
      assertEquals( 1, thePool.getCheckOutPool().size() );
      assertEquals( 0, thePool.getCheckInPool().size() );
      
      thePool.close( theSocket3 );
      assertTrue( theSocket3.isClosed() );
      
      assertEquals( 0, thePool.getCheckOutPool().size() );
      assertEquals( 0, thePool.getCheckInPool().size() );
    }finally{
      theServerSocket.close();
    }

  }
}
