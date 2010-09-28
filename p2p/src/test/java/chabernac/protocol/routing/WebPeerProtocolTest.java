package chabernac.protocol.routing;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import chabernac.comet.CometServlet;
import chabernac.p2p.web.ProtocolServlet;
import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.message.iMessageListener;

public class WebPeerProtocolTest extends AbstractProtocolTest {
  private static Logger LOGGER = Logger.getLogger(RoutingProtocolTest.class);

  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void testWebPeerProtocol() throws Exception{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    ProtocolServer theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    MessageProtocol theMessageProtocol1 = (MessageProtocol)theProtocol1.getProtocol( MessageProtocol.ID );
    theRoutingProtocol1.getLocalUnreachablePeerIds().add("2");
    theProtocol1.getProtocol( WebPeerProtocol.ID );

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    ProtocolServer theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    MessageProtocol theMessageProtocol2 = (MessageProtocol)theProtocol2.getProtocol( MessageProtocol.ID );
    theRoutingProtocol2.getLocalUnreachablePeerIds().add("1");
    theProtocol2.getProtocol( WebPeerProtocol.ID );
    
    Server theWebServer = new Server(9090);

    try{
      assertTrue( theServer1.start() );

      Context root = new Context(theWebServer,"/p2p",Context.SESSIONS);
      CometServlet theCometServlet= new CometServlet();
      ServletHolder theCometHolder = new ServletHolder(theCometServlet);
      theCometHolder.setInitOrder(1);
      root.addServlet(theCometHolder, "/comet");
      ProtocolServlet theProtocolServlet = new ProtocolServlet();
      ServletHolder theProtocolHolder = new ServletHolder(theProtocolServlet);
      theProtocolHolder.setInitOrder(2);
      root.addServlet(theProtocolHolder, "/protocol");

      theWebServer.start();

      theServer1.start();
      theServer2.start();
      Thread.sleep( 1000 );

      new ScanWebSystem(theRoutingProtocol1, new URL("http://localhost:9090/")).run();
      new ScanWebSystem(theRoutingProtocol2, new URL("http://localhost:9090/")).run();
      
      Thread.sleep( 2000 );
      
      //after the webpeer has been added, entries must be present in the comet servlet endpoints
      theCometServlet.getEndPoints().containsKey("1");
      theCometServlet.getEndPoints().containsKey("2");
      
      RoutingProtocol theWebPeerRoutingProtocol = (RoutingProtocol)theProtocolServlet.getProtocolContainer().getProtocol(RoutingProtocol.ID);
      String theWebPeerId = theWebPeerRoutingProtocol.getRoutingTable().getLocalPeerId();
      theWebPeerRoutingProtocol.getRoutingTable().setKeepHistory(true);


      for(int i=0;i<3;i++){
        theRoutingProtocol1.exchangeRoutingTable();
        theRoutingProtocol2.exchangeRoutingTable();
      }
      
      Thread.sleep( 1000 );
      
      assertTrue(theRoutingProtocol1.getRoutingTable().containsEntryForPeer(theWebPeerId));
      assertEquals(1, theRoutingProtocol1.getRoutingTable().getEntryForPeer(theWebPeerId).getHopDistance());
      assertEquals(2, theRoutingProtocol1.getRoutingTable().getEntryForPeer("2").getHopDistance());
      assertEquals(theWebPeerId, theRoutingProtocol1.getRoutingTable().getEntryForPeer("2").getGateway().getPeerId());
      
      //peer 1 now knows peer 2 trough the web peer, let's trying sending messages to it which will pass trough the web peer
      MessageCollector theCollector = new MessageCollector();
      theMessageProtocol2.addMessageListener(theCollector);
      
      int times=1000;
      for(int i=0;i<times;i++){
       Message theMessage = new Message();
       theMessage.setDestination(theRoutingProtocol1.getRoutingTable().getEntryForPeer("2").getPeer());
       theMessage.setMessage("message " + i);
       theMessageProtocol1.sendMessage(theMessage);
       System.out.println("Message " + i + " successfull");
      }
      
      assertEquals(times, theCollector.getMessages().size());
      
    } finally{
      theServer1.stop();
      theWebServer.stop();
    }
  }
  
  public class MessageCollector implements iMessageListener{
    private List<Message> myMessages = Collections.synchronizedList( new ArrayList< Message >() );

    @Override
    public void messageReceived( Message aMessage ) {
      myMessages.add(aMessage); 
    }

    public List<Message> getMessages(){
      return myMessages;
    }
  }
}
