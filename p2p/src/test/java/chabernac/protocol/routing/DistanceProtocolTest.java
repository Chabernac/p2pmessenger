package chabernac.protocol.routing;

import chabernac.protocol.AbstractProtocolTest;
import chabernac.protocol.P2PServerFactoryException;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.iP2PServer;

public class DistanceProtocolTest extends AbstractProtocolTest {
  public void testDistanceProtocol() throws P2PServerFactoryException, ProtocolException, UnknownPeerException{
    ProtocolContainer theProtocol1 = getProtocolContainer( 1, false, "1" );
    iP2PServer theServer1 = getP2PServer( theProtocol1, RoutingProtocol.START_PORT);

    ProtocolContainer theProtocol2 = getProtocolContainer( 1, false, "2" );
    iP2PServer theServer2 = getP2PServer( theProtocol2, RoutingProtocol.START_PORT + 1);
    
    DistanceProtocol theDistanceProtocol1 = (DistanceProtocol)theProtocol1.getProtocol( DistanceProtocol.ID );
    DistanceProtocol theDistanceProtocol2 = (DistanceProtocol)theProtocol2.getProtocol( DistanceProtocol.ID );
    
    RoutingProtocol theRoutingProtocol1 = (RoutingProtocol)theProtocol1.getProtocol( RoutingProtocol.ID );
    RoutingProtocol theRoutingProtocol2 = (RoutingProtocol)theProtocol2.getProtocol( RoutingProtocol.ID );
    
    theServer1.start();
    theServer2.start();

    theRoutingProtocol1.scanLocalSystem();
    theRoutingProtocol2.scanLocalSystem();
    
    theRoutingProtocol1.exchangeRoutingTable();
    theRoutingProtocol2.exchangeRoutingTable();
    
    assertNotNull(theRoutingProtocol1.getRoutingTable().getEntryForPeer("2", 5));
    assertNotNull(theRoutingProtocol2.getRoutingTable().getEntryForPeer("1", 5));
    
    
    long theTime1 = theDistanceProtocol1.getTimeDistance(theRoutingProtocol1.getRoutingTable().getEntryForPeer("2").getPeer());
    long theTime2 = theDistanceProtocol2.getTimeDistance(theRoutingProtocol2.getRoutingTable().getEntryForPeer("1").getPeer());
    
    //now clear 
    System.out.println(theTime1);
    System.out.println(theTime2);
    
    theDistanceProtocol1.clear();
    theDistanceProtocol2.clear();
    
    //clear the entries in jvm peer sender so that communication through sockets is forced
    JVMPeerSender.getInstance().clear();
    long theTime12 = theDistanceProtocol1.getTimeDistance(theRoutingProtocol1.getRoutingTable().getEntryForPeer("2").getPeer());
    long theTime22 = theDistanceProtocol2.getTimeDistance(theRoutingProtocol2.getRoutingTable().getEntryForPeer("1").getPeer());
    
    System.out.println(theTime12);
    System.out.println(theTime22);
    
    assertTrue(theTime1 < theTime12);
    assertTrue(theTime2 < theTime22);
  }
}
