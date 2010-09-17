/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.ping.PingProtocol;
import chabernac.protocol.routing.NoAvailableNetworkAdapterException;
import chabernac.protocol.routing.SocketPeer;
import chabernac.tools.PropertyMap;

public class ProtocolServerTest extends AbstractProtocolTest {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testProtocolServer() throws UnknownHostException, IOException{
    ProtocolContainer theMasterProtocol = new ProtocolContainer(new ProtocolFactory(new PropertyMap()));
    
    int thePort = 12026;
    
    ProtocolServer theServer = new ProtocolServer(theMasterProtocol, thePort, 5);
    assertTrue( theServer.start() );
    
    Socket theClientSocket = new Socket("localhost", thePort);
    
    PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(theClientSocket.getOutputStream()));
    
    theWriter.println( PingProtocol.ID + "ping" );
    theWriter.flush();
    
    BufferedReader theReader = new BufferedReader(new InputStreamReader(theClientSocket.getInputStream()));
    
    assertEquals( PingProtocol.Response.PONG.name(), theReader.readLine() );
    
    theServer.stop();
  }
  
  public void testProtocolServerWithPeer() throws UnknownHostException, IOException, NoAvailableNetworkAdapterException{
    ProtocolContainer theMasterProtocol = new ProtocolContainer(new ProtocolFactory(new PropertyMap()));
    
    int thePort = 12027;
    
    ProtocolServer theServer = new ProtocolServer(theMasterProtocol, thePort, 5);
    assertTrue( theServer.start() );
    
    SocketPeer thePeer = new SocketPeer();
    thePeer.detectLocalInterfaces();
    thePeer.setPort( thePort );
    
    assertEquals( PingProtocol.Response.PONG.name(), thePeer.send( PingProtocol.ID + "ping" ) );
    
    theServer.stop();
  }
}
