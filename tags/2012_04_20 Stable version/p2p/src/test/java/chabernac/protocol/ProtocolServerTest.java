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

import org.apache.log4j.BasicConfigurator;

import chabernac.io.StreamSplitter;
import chabernac.protocol.P2PServerFactory.ServerMode;
import chabernac.protocol.ping.PingProtocol;
import chabernac.protocol.routing.NoAvailableNetworkAdapterException;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.SocketPeer;
import chabernac.protocol.routing.iPeerSender;
import chabernac.tools.PropertyMap;

public class ProtocolServerTest extends AbstractProtocolTest {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void testProtocolServer() throws UnknownHostException, IOException{
    ProtocolContainer theMasterProtocol = new ProtocolContainer(new ProtocolFactory(new PropertyMap()));

    int thePort = 12026;

    ProtocolServer theServer = new ProtocolServer(theMasterProtocol, thePort);

    try{
      assertTrue( theServer.start() );

      Socket theClientSocket = new Socket("localhost", thePort);

      PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(theClientSocket.getOutputStream()));

      theWriter.println( PingProtocol.ID + "ping" );
      theWriter.flush();

      BufferedReader theReader = new BufferedReader(new InputStreamReader(theClientSocket.getInputStream()));

      assertEquals( PingProtocol.Response.PONG.name(), theReader.readLine() );
    }finally{
      theServer.stop();
    }
  }
  
  public void testProtocolServerStreamSplitterCompatibility() throws UnknownHostException, IOException{
    ProtocolContainer theMasterProtocol = new ProtocolContainer(new ProtocolFactory(new PropertyMap()));

    int thePort = 12026;

    ProtocolServer theServer = new ProtocolServer(theMasterProtocol, thePort);

    try{
      assertTrue( theServer.start() );

      Socket theClientSocket = new Socket("localhost", thePort);

      PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(theClientSocket.getOutputStream()));

      theWriter.println( StreamSplitter.IN + PingProtocol.ID + "ping" );
      theWriter.flush();

      BufferedReader theReader = new BufferedReader(new InputStreamReader(theClientSocket.getInputStream()));

      assertEquals( PingProtocol.Response.PONG.name(), theReader.readLine() );
    }finally{
      theServer.stop();
    }

  }

  public void testProtocolServerWithPeer() throws UnknownHostException, IOException, NoAvailableNetworkAdapterException, ProtocolException{
    ProtocolContainer theMasterProtocol = new ProtocolContainer(new ProtocolFactory(new PropertyMap()));

    RoutingProtocol.determinePorts(ServerMode.SOCKET);
    int thePort = 12750;

    ProtocolServer theServer = new ProtocolServer(theMasterProtocol, thePort);
    try{
      assertTrue( theServer.start() );

      SocketPeer thePeer = new SocketPeer();
      thePeer.detectLocalInterfaces();
      thePeer.setPort( thePort );

      assertEquals( PingProtocol.Response.PONG.name(), getPeerSender(theMasterProtocol).send(thePeer, PingProtocol.ID + "ping" ) );
    }finally{

      theServer.stop();
    }
  }
  
  private iPeerSender getPeerSender(ProtocolContainer aContainer) throws ProtocolException{
    return ((RoutingProtocol)aContainer.getProtocol( RoutingProtocol.ID )).getPeerSender(); 
  }
}

