/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import chabernac.comet.CometEvent;
import chabernac.comet.CometServlet;
import chabernac.comet.EndPoint;
import chabernac.protocol.routing.PeerSender;
import chabernac.protocol.routing.WebPeer;

public class WebPeerTest extends TestCase {
  public void testWebPeer() throws Exception{
    Server theServer = new Server(9090);
    ExecutorService theService = Executors.newCachedThreadPool();

    try{
      Context root = new Context(theServer,"/p2p",Context.SESSIONS);
      root.addServlet(new ServletHolder(new CometServlet()), "/comet");
      root.addServlet(new ServletHolder(new ProtocolServlet()), "/protocol");
      
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
      
      theWebPeer.setPeerSender(new PeerSender());
      assertEquals("123", theWebPeer.send("ECO123"));
    }finally{
      theServer.stop();
      theService.shutdownNow();
    }
  }
}
