package chabernac.p2p.web;

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
import chabernac.p2p.web.WebPeerSender;
import chabernac.protocol.routing.SocketPeer;
import chabernac.protocol.routing.WebPeer;

public class WebPeerSenderTest extends TestCase {
  public void testWebPeerSender() throws Exception{
    Server theServer = new Server(9090);
    ExecutorService theService = Executors.newCachedThreadPool();
    try{
      Context root = new Context(theServer,"/p2p",Context.SESSIONS);
      root.addServlet(new ServletHolder(new CometServlet()), "/comet");
      theServer.start();

      final WebPeer theWebPeer = new WebPeer("1", new URL("http://localhost:9090"));
      theService.execute( new Runnable(){
        public void run(){
          try {
            while(true){
              CometEvent theEvent = theWebPeer.waitForEvent("2");
              theEvent.setOutput( "output" );
            }
          } catch ( IOException e ) {
            e.printStackTrace();
          }
        }
      });

      Thread.sleep( 2000 );

      Map<String, EndPoint> theEndPoints =  (Map<String, EndPoint>)root.getServletContext().getAttribute( "EndPoints" );
      assertNotNull( theEndPoints );
      assertTrue( theEndPoints.containsKey( "2" ) );


      WebPeerSender theSender = new WebPeerSender(theEndPoints);
      SocketPeer theLocalPeer = new SocketPeer("2");
      assertEquals("output", theSender.send("event1", theLocalPeer, 2000));
      WebPeer theWebPeer2 = new WebPeer("2", null);
      assertEquals("output", theSender.send("event1", theWebPeer2, 2000));
    }finally{
      theServer.stop();
      theService.shutdownNow();
    }
  }
}
