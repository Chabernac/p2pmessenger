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

  public void testProtocolServerWithPeer() throws UnknownHostException, IOException, NoAvailableNetworkAdapterException{
    ProtocolContainer theMasterProtocol = new ProtocolContainer(new ProtocolFactory(new PropertyMap()));

    int thePort = 12027;

    ProtocolServer theServer = new ProtocolServer(theMasterProtocol, thePort, 5);
    try{
      assertTrue( theServer.start() );

      SocketPeer thePeer = new SocketPeer();
      thePeer.detectLocalInterfaces();
      thePeer.setPort( thePort );

      assertEquals( PingProtocol.Response.PONG.name(), thePeer.send( PingProtocol.ID + "ping" ) );
    }finally{

      theServer.stop();
    }
  }

  public void testKillOldestSocket() throws UnknownHostException, IOException{
    ProtocolContainer theMasterProtocol = new ProtocolContainer(new ProtocolFactory(new PropertyMap()));

    int thePort = 12026;


    int theNrOfThreads = 5;
    Socket[] theSockets = new Socket[theNrOfThreads];

    ProtocolServer theServer = new ProtocolServer(theMasterProtocol, thePort, theNrOfThreads);
    try{
      assertTrue( theServer.start() );

      //first create theNrOfThreads sockets, they will consome all available threads on the server
      for(int i=0;i<theNrOfThreads;i++){
        theSockets[i] = new Socket("localhost", thePort);
      }

      //now test if we can still send a command and get a response
      //this can only be if the server is smart enough to kill the oldest socket

      Socket theClientSocket = new Socket("localhost", thePort);

      PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(theClientSocket.getOutputStream()));

      theWriter.println( PingProtocol.ID + "ping" );
      theWriter.flush();

      BufferedReader theReader = new BufferedReader(new InputStreamReader(theClientSocket.getInputStream()));

      assertEquals( PingProtocol.Response.PONG.name(), theReader.readLine() );

      theServer.stop();
    }finally{
      theServer.stop();
      for(int i=0;i<theSockets.length;i++){
        if(theSockets[i] != null){
          theSockets[i].close();
        }
      }
    }

  }
}
