/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.nserver.EchoProtocol;
import chabernac.nserver.Server;
import chabernac.tools.NetTools;

public class SocketPoolTest extends TestCase {
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testSocketPool() throws IOException{
    ServerSocket theServerSocket = NetTools.openServerSocket( 1500 );

    try{
      CachingSocketPool thePool = new CachingSocketPool();
      thePool.setCleanUpTimeInSeconds( 30 );
      thePool.cleanUp();
      Socket theSocket1 = thePool.checkOut( new InetSocketAddress("localhost", theServerSocket.getLocalPort()) );
      Socket theSocket2 = thePool.checkOut( new InetSocketAddress("localhost", theServerSocket.getLocalPort()) );

      //we now have 2 checked out connections and none checked in
      assertEquals( 2, thePool.getCheckedOutPool().size() );
      assertEquals( 0, thePool.getCheckedInPool().size() );

      //check in 1 connections, we have 1 checked out and 1 checked in connections
      thePool.checkIn( theSocket1 );
      assertEquals( 1, thePool.getCheckedOutPool().size() );
      assertEquals( 1, thePool.getCheckedInPool().size() );

      //check in the other connections, we only have check in connections
      thePool.checkIn( theSocket2 );
      assertEquals( 0, thePool.getCheckedOutPool().size() );
      assertEquals( 2, thePool.getCheckedInPool().size() );

      //check out a connections which is in the checked in pool, one of the available sockets must be returned
      Socket theSocket3 = thePool.checkOut( new InetSocketAddress("localhost", theServerSocket.getLocalPort()) );
      assertEquals( 1, thePool.getCheckedOutPool().size() );
      assertEquals( 1, thePool.getCheckedInPool().size() );
      assertTrue( theSocket3 == theSocket1 || theSocket3 == theSocket2 );

      //the clean up should only clean connections which have not been checked our or in for 30 seconds.
      //nothing must have changed
      thePool.cleanUp();
      assertEquals( 1, thePool.getCheckedOutPool().size() );
      assertEquals( 1, thePool.getCheckedInPool().size() );

      thePool.close( theSocket3 );
      assertTrue( theSocket3.isClosed() );

      assertEquals( 0, thePool.getCheckedOutPool().size() );
      assertEquals( 1, thePool.getCheckedInPool().size() );

      theSocket1 = thePool.checkOut( new InetSocketAddress("localhost", theServerSocket.getLocalPort()) );

      assertEquals( 1, thePool.getCheckedOutPool().size() );
      assertEquals( 0, thePool.getCheckedInPool().size() );

      thePool.checkIn( theSocket1 );

      assertEquals( 0, thePool.getCheckedOutPool().size() );
      assertEquals( 1, thePool.getCheckedInPool().size() );

      thePool.close( theSocket1 );
      assertTrue( theSocket1.isClosed() );

      assertEquals( 0, thePool.getCheckedOutPool().size() );
      assertEquals( 0, thePool.getCheckedInPool().size() );

      theSocket1 = thePool.checkOut( new InetSocketAddress("localhost", theServerSocket.getLocalPort()) );
      theSocket2 = thePool.checkOut( new InetSocketAddress("localhost", theServerSocket.getLocalPort()) );

      thePool.checkIn( theSocket1 );

      assertEquals( 1, thePool.getCheckedOutPool().size() );
      assertEquals( 1, thePool.getCheckedInPool().size() );

      thePool.fullClean();
      assertTrue( theSocket1.isClosed() );
      assertTrue( theSocket2.isClosed() );

      assertEquals( 0, thePool.getCheckedOutPool().size() );
      assertEquals( 0, thePool.getCheckedInPool().size() );

      try{
        theSocket3 = thePool.checkOut( new InetSocketAddress("localhost", 78978) );
        fail("We must not get here");
      }catch(Exception e){
      }

      assertEquals( 0, thePool.getCheckedOutPool().size() );
      assertEquals( 0, thePool.getCheckedInPool().size() );


    }finally{
      theServerSocket.close();
    }

  }

  public void testBasicSocketPool() throws IOException, InterruptedException{
    Server theServer = new Server(1600, new EchoProtocol());
    try{
      theServer.start();
      
      Thread.sleep( 1000 );

      BasicSocketPool theSocketPool = new BasicSocketPool();

      Socket theSocket = theSocketPool.checkOut( new InetSocketAddress("localhost", theServer.getPort()));
      assertEquals( 1, theSocketPool.getCheckedOutPool().size() );
      assertEquals( "123", writeAndReadToSocket( theSocket, "123" ));
      
      theSocketPool.checkIn( theSocket );
      assertEquals( 0, theSocketPool.getCheckedOutPool().size() );
      assertEquals( 1, theSocketPool.getCheckedInPool().size() );
      assertEquals( 0, theSocketPool.getConnectingPool().size() );
      Socket theNewSocket = theSocketPool.checkOut( new InetSocketAddress("localhost", theServer.getPort()));
      assertEquals( 1, theSocketPool.getCheckedOutPool().size() );
      assertEquals( 0, theSocketPool.getCheckedInPool().size() );
      assertEquals( 0, theSocketPool.getConnectingPool().size() );
      
      assertTrue( theSocket == theNewSocket);
      assertEquals( "123", writeAndReadToSocket( theNewSocket, "123" ));
      
      theSocketPool.close( theSocket );
      assertEquals( 0, theSocketPool.getCheckedOutPool().size() );
      assertEquals( 0, theSocketPool.getCheckedInPool().size() );
      assertEquals( 0, theSocketPool.getConnectingPool().size() );
      
      //test if the socket pool detects a closed socket and creates a new one
      theSocket = theSocketPool.checkOut( new InetSocketAddress("localhost", theServer.getPort()) );
      theSocket.close();
      theSocketPool.checkIn( theSocket );
      theSocket = theSocketPool.checkOut( new InetSocketAddress("localhost", theServer.getPort()) );
      assertEquals( "123", writeAndReadToSocket( theSocket, "123" ));
      
      assertEquals( 1, theSocketPool.getCheckedOutPool().size() );
      assertEquals( 0, theSocketPool.getCheckedInPool().size() );
      assertEquals( 0, theSocketPool.getConnectingPool().size() );
      
      Socket theSocket2 = theSocketPool.checkOut( new InetSocketAddress("localhost", theServer.getPort()) );
      theSocketPool.checkIn( theSocket2 );
      
      assertEquals( 1, theSocketPool.getCheckedOutPool().size() );
      assertEquals( 1, theSocketPool.getCheckedInPool().size() );
      assertEquals( 0, theSocketPool.getConnectingPool().size() );
      
      theSocketPool.cleanUp();
      
      assertEquals( 0, theSocketPool.getCheckedOutPool().size() );
      assertEquals( 0, theSocketPool.getCheckedInPool().size() );
      assertEquals( 0, theSocketPool.getConnectingPool().size() );

      
      //test the max allowed sockets per socket address
      
      theSocketPool.setMaxAllowSocketsPerSocketAddress( 2 );
      
      Socket theSocket_1 = theSocketPool.checkOut( new InetSocketAddress("localhost", theServer.getPort()) );
      Socket theSocket_2 = theSocketPool.checkOut( new InetSocketAddress("localhost", theServer.getPort()) );
      Socket theSocket_3 = theSocketPool.checkOut( new InetSocketAddress("localhost", theServer.getPort()) );
      
      theSocketPool.checkIn( theSocket_1 );
      theSocketPool.checkIn( theSocket_2 );
      theSocketPool.checkIn( theSocket_3 );
      
      //only 2 sockets are allowed in the socket pool per socket address
      assertEquals( 0, theSocketPool.getCheckedOutPool().size() );
      assertEquals( theSocketPool.getMaxAllowSocketsPerSocketAddress(), theSocketPool.getCheckedInPool().size() );
      assertEquals( 0, theSocketPool.getConnectingPool().size() );
      
      theSocketPool.cleanUp();
      
      assertEquals( 0, theSocketPool.getCheckedOutPool().size() );
      assertEquals( 0, theSocketPool.getCheckedInPool().size() );
      assertEquals( 0, theSocketPool.getConnectingPool().size() );
      
    }finally {
      theServer.stop();
    }
  }

  private String writeAndReadToSocket(Socket aSocket, String aMessage) throws IOException{
    PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(aSocket.getOutputStream()));
    BufferedReader theReader= new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
    theWriter.println(aMessage);
    theWriter.flush();
    String theLine = theReader.readLine();
    if(theLine.equals( "HELLO" )){
      theLine = theReader.readLine();
    }
    return theLine;

  }



}

