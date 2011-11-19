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

import junit.framework.TestCase;
import chabernac.nserver.EchoProtocol;
import chabernac.nserver.Server;

public class SocketProxyTest extends TestCase {
  public void testSocketProxy() throws IOException{
    Server theServer = new Server(1600, new EchoProtocol());
    try{
      theServer.start();
      SocketProxy theProxy = new SocketProxy( new InetSocketAddress("localhost", theServer.getPort()));
      assertEquals( "a", writeAndReadToSocket( theProxy, "a" ) );
      theProxy.close();
      
      assertFalse( theProxy.isBound() );
      assertTrue( theProxy.isClosed() );
      assertFalse( theProxy.isConnected() );
      assertTrue( theProxy.isInputShutdown() );
      assertTrue( theProxy.isOutputShutdown() );
      assertNull( theProxy.getLocalAddress() );
      assertNotNull( theProxy.getSocketAddress() );
      assertNull( theProxy.getLocalSocketAddress());
      assertNull( theProxy.getRemoteSocketAddress());
      
      
      //the proxy just reconnects and we can use it again
      assertEquals( "a", writeAndReadToSocket( theProxy, "a" ) );
      
      assertTrue( theProxy.isBound() );
      assertFalse( theProxy.isClosed() );
      assertTrue( theProxy.isConnected() );
      assertFalse( theProxy.isInputShutdown() );
      assertFalse( theProxy.isOutputShutdown() );
      assertNotNull( theProxy.getLocalAddress() );
      assertNotNull( theProxy.getSocketAddress() );
      assertNotNull( theProxy.getLocalSocketAddress());
      assertNotNull( theProxy.getRemoteSocketAddress());
      
    } finally {
      theServer.stop();
    }

  }
  
  private String writeAndReadToSocket(SocketProxy aSocket, String aMessage) throws IOException{
    PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(aSocket.getOutputStream()));
    BufferedReader theReader= new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
    theWriter.println(aMessage);
    theWriter.flush();
    String theLine = theReader.readLine();
    if("HELLO".equals( theLine )){
      theLine = theReader.readLine();
    }
    return theLine;
  }

}
