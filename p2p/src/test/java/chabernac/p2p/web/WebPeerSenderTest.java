package chabernac.p2p.web;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import chabernac.comet.CometEvent;
import chabernac.comet.CometServlet;
import chabernac.comet.EndPointContainer;
import chabernac.p2p.io.WebToPeerSender;
import chabernac.protocol.ProtocolWebServer;
import chabernac.protocol.routing.SocketPeer;
import chabernac.protocol.routing.WebPeer;

public class WebPeerSenderTest extends TestCase {
  
  static{
    BasicConfigurator.configure();
  }
  
  public void testWebPeerSender() throws Exception{
    Server theServer = new Server(9090);
    ExecutorService theService = Executors.newCachedThreadPool();
    try{
      Context root = new Context(theServer,ProtocolWebServer.CONTEXT,Context.SESSIONS);
      root.addServlet(new ServletHolder(new CometServlet()), ProtocolWebServer.COMET);
      theServer.start();

      final WebPeer theWebPeer = new WebPeer("1", new URL("http://localhost:9090"));
      theService.execute( new Runnable(){
        public void run(){
          try {
            while(true){
              CometEvent theEvent = theWebPeer.waitForEvent("2");
              theEvent.setOutput( "output" );
            }
          } catch ( Exception e ) {
            e.printStackTrace();
          }
        }
      });

      Thread.sleep( 2000 );

      EndPointContainer theEndPointContainer =  (EndPointContainer)root.getServletContext().getAttribute( "EndPoints" );
      theWebPeer.setEndPointContainer( theEndPointContainer );
      
      assertNotNull( theEndPointContainer );
      assertTrue( theEndPointContainer.containsEndPointFor( "2" ) );

      WebToPeerSender theSender = new WebToPeerSender();

      SocketPeer theLocalPeer = new SocketPeer("2");
      assertEquals("output", theSender.sendMessageTo( theWebPeer, theLocalPeer, "event1", 2000));
      WebPeer theWebPeer2 = new WebPeer("2", null);
      assertEquals("output", theSender.sendMessageTo( theWebPeer, theWebPeer2, "event1", 2000));
    }finally{
      theServer.stop();
      theService.shutdownNow();
    }
  }
}
