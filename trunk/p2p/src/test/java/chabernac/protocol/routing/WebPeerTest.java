/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import chabernac.comet.CometEvent;
import chabernac.comet.EndPoint;
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
      root.addServlet(Class.forName("chabernac.comet.CometServlet"), "/comet");
//      root.addServlet(Class.forName("chabernac.p2p.web.ProtocolServlet"), "/protocol");
      
      ProtocolServlet theProtocolServlet = new ProtocolServlet();
      ServletHolder theProtocolHolder = new ServletHolder(theProtocolServlet);
      theProtocolHolder.setInitOrder(2);
      root.addServlet(theProtocolHolder, "/protocol");
      theProtocolHolder.setInitParameter( "serverurl", "http://localhost:9090/p2p" );
      
      theServer.start();

      final WebPeer theWebPeer = new WebPeer("1", new URL("http://localhost:9090"));
      theService.execute( new Runnable(){
        public void run(){
          try {
            CometEvent theEvent = theWebPeer.waitForEvent("2");
            theEvent.setOutput( "output" );
          } catch ( IOException e ) {
            e.printStackTrace();
          }
        }
      });

      Thread.sleep( 2000 );

      Map<String, EndPoint> theEndPoints =  (Map<String, EndPoint>)root.getServletContext().getAttribute( "EndPoints" );
      assertNotNull( theEndPoints );
      assertTrue( theEndPoints.containsKey( "2" ) );

      EndPoint theEndPoint = theEndPoints.get("2");
      CometEvent theServerToClientEvent = new CometEvent("event1", "input");
      theEndPoint.setEvent( theServerToClientEvent );
      assertEquals( "output", theServerToClientEvent.getOutput( 2000 ));
      
      assertEquals("123", getPeerSender( theProtocolServlet ).send( theWebPeer, "ECO123"));
    }finally{
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
    protected String doSend( AbstractPeer aTo, String aMessage, int aTimeout ) throws IOException {
      return null;
    }
  }
}
