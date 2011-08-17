package chabernac.protocol.routing;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import chabernac.comet.CometServlet;
import chabernac.p2p.web.ProtocolServlet;
import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.message.iMessageListener;

public class WebPeerProtocolTest extends AbstractProtocolTest {
  private static Logger LOGGER = Logger.getLogger(RoutingProtocolTest.class);

  private ProtocolServer theServer1;
  private RoutingProtocol myRoutingProtocol1;
  private MessageProtocol myMessageProtocol1;

  private ProtocolServer theServer2;
  private MessageProtocol myMessageProtocol2;

  private Server theWebServer;

  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }

  public void setUp() throws Exception{
    ProtocolContainer theProtocol1 = getProtocolContainer( -1, false, "1" );
    theServer1 = new ProtocolServer(theProtocol1, RoutingProtocol.START_PORT, 5);
    myRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    myMessageProtocol1 = (MessageProtocol)theProtocol1.getProtocol( MessageProtocol.ID );
    myRoutingProtocol1.getLocalUnreachablePeerIds().add("2");
    theProtocol1.getProtocol( WebPeerProtocol.ID );

    ProtocolContainer theProtocol2 = getProtocolContainer( -1, false, "2" );
    theServer2 = new ProtocolServer(theProtocol2, RoutingProtocol.START_PORT + 1, 5);
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    myMessageProtocol2 = (MessageProtocol)theProtocol2.getProtocol( MessageProtocol.ID );
    theRoutingProtocol2.getLocalUnreachablePeerIds().add("1");
    theProtocol2.getProtocol( WebPeerProtocol.ID );

    theWebServer = new Server(9090);

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
    theProtocolHolder.setInitParameter( "serverurl", "http://localhost:9090/p2p" );

    theWebServer.start();

    theServer1.start();
    theServer2.start();
    Thread.sleep( 1000 );
    
    assertTrue( theWebServer.isRunning() );

    new ScanWebSystem(myRoutingProtocol1, new URL("http://localhost:9090/")).run();
    new ScanWebSystem(theRoutingProtocol2, new URL("http://localhost:9090/")).run();

    Thread.sleep( 2000 );

    //after the webpeer has been added, entries must be present in the comet servlet endpoints
    assertTrue( theCometServlet.getEndPointContainer().containsEndPointFor( "1"));
    assertTrue( theCometServlet.getEndPointContainer().containsEndPointFor( "2"));

    RoutingProtocol theWebPeerRoutingProtocol = (RoutingProtocol)theProtocolServlet.getProtocolContainer().getProtocol(RoutingProtocol.ID);
    String theWebPeerId = theWebPeerRoutingProtocol.getRoutingTable().getLocalPeerId();
    theWebPeerRoutingProtocol.getRoutingTable().setKeepHistory(true);


    for(int i=0;i<3;i++){
      myRoutingProtocol1.exchangeRoutingTable();
      theRoutingProtocol2.exchangeRoutingTable();
    }

    Thread.sleep( 1000 );

    assertTrue(myRoutingProtocol1.getRoutingTable().containsEntryForPeer(theWebPeerId));
    assertEquals(1, myRoutingProtocol1.getRoutingTable().getEntryForPeer(theWebPeerId).getHopDistance());
    assertEquals(2, myRoutingProtocol1.getRoutingTable().getEntryForPeer("2").getHopDistance());
    assertEquals(theWebPeerId, myRoutingProtocol1.getRoutingTable().getEntryForPeer("2").getGateway().getPeerId());
  }

  public void tearDown(){
    if(theServer1 != null) theServer1.stop();
    if(theServer2 != null) theServer2.stop();
    if(theWebServer != null)
      try {
        theWebServer.stop();
      } catch ( Exception e ) {
      }
  }

  public void testWebPeerProtocol() throws Exception {
    //peer 1 now knows peer 2 trough the web peer, let's trying sending messages to it which will pass trough the web peer
    MessageCollector theCollector = new MessageCollector();
    myMessageProtocol2.addMessageListener(theCollector);

    int times=1000;
    for(int i=0;i<times;i++){
      Message theMessage = new Message();
      theMessage.setDestination(myRoutingProtocol1.getRoutingTable().getEntryForPeer("2").getPeer());
      theMessage.setMessage("message " + i);
      myMessageProtocol1.sendMessage(theMessage);
      System.out.println("Message " + i + " successfull");
    }

    assertEquals(times, theCollector.getMessages().size());
  }

  public void testWebPeerPerformance() throws UnknownPeerException, InterruptedException{
    ExecutorService theService = Executors.newFixedThreadPool( 5 );

    MessageCollector theCollector = new MessageCollector();
    myMessageProtocol2.addMessageListener(theCollector);

    int times=1000;
    final CountDownLatch theLatch = new CountDownLatch( times );
    for(int i=0;i<times;i++){
      final int j = i;
      theService.execute( new Runnable(){
        public void run(){
          try{
            Message theMessage = new Message();
            theMessage.setDestination(myRoutingProtocol1.getRoutingTable().getEntryForPeer("2").getPeer());
            theMessage.setMessage("message " + j);
            myMessageProtocol1.sendMessage(theMessage);
            theLatch.countDown();
          }catch(Exception e){
          }
        }
      });
    }
    
    theLatch.await( 20, TimeUnit.SECONDS );

    assertEquals(times, theCollector.getMessages().size());

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

    @Override
    public void messageUpdated( Message aMessage ) {
      // TODO Auto-generated method stub

    }
  }
}
