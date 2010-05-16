/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import junit.framework.TestCase;

public class SocketTest extends TestCase {
  public void testSocketReuse() throws UnknownHostException, IOException{
    Socket theSocket = new Socket("www.google.com", 80);
    int theLocalPort = theSocket.getLocalPort();
//    theSocket.
    theSocket.connect( new InetSocketAddress("www.axa.be", 80) );
    assertEquals( theLocalPort, theSocket.getLocalPort() );
  }

}
