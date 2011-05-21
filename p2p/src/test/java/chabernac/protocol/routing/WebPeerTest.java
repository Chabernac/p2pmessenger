/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import chabernac.comet.CometEvent;
import chabernac.comet.CometServlet;
import chabernac.comet.EndPoint;
import chabernac.comet.EndPointContainer;
import chabernac.p2p.web.ProtocolServlet;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.echo.EchoProtocol;
import chabernac.protocol.userinfo.UserInfoProtocol;

public class WebPeerTest extends TestCase {
  static{
    BasicConfigurator.configure();
  }
  
  public void testWebPeer() throws Exception{
    Server theServer = new Server(9090);
    ExecutorService theService = Executors.newCachedThreadPool();

    try{
      Context root = new Context(theServer,"/p2p",Context.SESSIONS);
      
      CometServlet theCometServlet = new CometServlet();
      ServletHolder theCometServletHolder = new ServletHolder(theCometServlet);
      theCometServletHolder.setInitOrder(1);
      root.addServlet(theCometServletHolder, "/comet");

      ProtocolServlet theProtocolServlet = new ProtocolServlet();
      ServletHolder theProtocolHolder = new ServletHolder(theProtocolServlet);
      theProtocolHolder.setInitOrder(2);
      root.addServlet(theProtocolHolder, "/protocol");
      theProtocolHolder.setInitParameter( "serverurl", "http://localhost:9090" );
      
      theServer.start();
      
      Thread.sleep( 2000 );

      final WebPeer theWebPeer = new WebPeer("1", new URL("http://localhost:9090"));
      theService.execute( new Runnable(){
        public void run(){
          try {
            while(true){
              System.out.println("wait for event");
              CometEvent theEvent = theWebPeer.waitForEvent("2");
              theEvent.setOutput( "output" );
            }
          } catch ( Exception e ) {
            e.printStackTrace();
          }
        }
      });
      
      Thread.sleep( 1000 );

      EndPointContainer theEndPoints =  (EndPointContainer)root.getServletContext().getAttribute( "EndPoints" );
      assertNotNull( theEndPoints );
      assertTrue( theEndPoints.containsEndPointFor( "2" ) );

      EndPoint theEndPoint = theEndPoints.getEndPointFor( "2", 5, TimeUnit.SECONDS);
      CometEvent theServerToClientEvent = new CometEvent("event1", "input");
      theEndPoint.setEvent( theServerToClientEvent );
      assertEquals( "output", theServerToClientEvent.getOutput( 2000 ));
      
      theEndPoint = theEndPoints.getEndPointFor( "2", 5, TimeUnit.SECONDS);
      theServerToClientEvent = new CometEvent("event2", "input");
      theEndPoint.setEvent( theServerToClientEvent );
      assertEquals( "output", theServerToClientEvent.getOutput( 2000 ));

      
//      assertEquals("123", getPeerSender( theProtocolServlet ).send( theWebPeer, "ECO123"));
    } finally {
      theServer.stop();
      theService.shutdownNow();
    }
  }
  
  private iPeerSender getPeerSender(ProtocolServlet aServlet) throws ProtocolException{
    return ((RoutingProtocol)aServlet.getProtocolContainer().getProtocol( RoutingProtocol.ID )).getPeerSender();
  }
  
  public void testSupportedProtocols() throws IOException{
    WebPeer theWebPeer = new WebPeer(  );
    assertTrue( theWebPeer.isProtocolSupported( EchoProtocol.ID ) );
    assertTrue( theWebPeer.isProtocolSupported( UserInfoProtocol.ID ) );
    assertTrue( theWebPeer.isProtocolSupported( RoutingProtocol.ID ) );
    assertTrue( theWebPeer.isProtocolSupported( WebPeerProtocol.ID ) );
    
    theWebPeer.addSupportedProtocol( EchoProtocol.ID );
    
    assertTrue( theWebPeer.isProtocolSupported( EchoProtocol.ID ) );
    assertFalse( theWebPeer.isProtocolSupported( UserInfoProtocol.ID ) );
    assertFalse( theWebPeer.isProtocolSupported( RoutingProtocol.ID ) );
    assertFalse( theWebPeer.isProtocolSupported( WebPeerProtocol.ID ) );
    
    
    MyPeerSender thePeerSender = new MyPeerSender();
    
    thePeerSender.send(theWebPeer, "ECO123" );
    try{
      thePeerSender.send(theWebPeer, "ROU123" );
      fail("We should not get here");
    }catch(Exception e){
    }
  }
  
  public void testReplacePlus(){
    assertEquals("+", "+".replaceAll("\\+", "{plus}").replaceAll("\\{plus\\}", "\\+"));
  }
  
  private class MyPeerSender extends AbstractPeerSender{

    @Override
    protected String doSend( PeerMessage aMessage, int aTimeout ) throws IOException {
      return null;
    }
  }
}
