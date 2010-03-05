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

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.routing.Peer;

public class ProtocolServerTest extends TestCase {
  public void setUp(){
    BasicConfigurator.configure();
  }
  
  public void testProtocolServer() throws UnknownHostException, IOException{
    MasterProtocol theMasterProtocol = new MasterProtocol();
    PingProtocol thePingProtocol = new PingProtocol();
    theMasterProtocol.addSubProtocol( thePingProtocol );
    
    int thePort = 12026;
    
    ProtocolServer theServer = new ProtocolServer(theMasterProtocol, thePort, 5);
    assertTrue( theServer.start() );
    
    Socket theClientSocket = new Socket("localhost", thePort);
    
    PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(theClientSocket.getOutputStream()));
    
    theWriter.println( thePingProtocol.getId() + "ping" );
    theWriter.flush();
    
    BufferedReader theReader = new BufferedReader(new InputStreamReader(theClientSocket.getInputStream()));
    
    assertEquals( PingProtocol.Response.PONG.name(), theReader.readLine() );
    
    theServer.stop();
  }
  
  public void testProtocolServerWithPeer() throws UnknownHostException, IOException{
    MasterProtocol theMasterProtocol = new MasterProtocol();
    PingProtocol thePingProtocol = new PingProtocol();
    theMasterProtocol.addSubProtocol( thePingProtocol );
    
    int thePort = 12027;
    
    ProtocolServer theServer = new ProtocolServer(theMasterProtocol, thePort, 5);
    assertTrue( theServer.start() );
    
    Peer thePeer = new Peer();
    thePeer.detectLocalInterfaces();
    thePeer.setPort( thePort );
    
    assertEquals( PingProtocol.Response.PONG.name(), thePeer.send( thePingProtocol.createMessage( "ping" ) ));
    
    theServer.stop();
  }
}
