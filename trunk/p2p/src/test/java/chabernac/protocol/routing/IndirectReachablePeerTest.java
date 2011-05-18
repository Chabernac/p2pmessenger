package chabernac.protocol.routing;

import junit.framework.TestCase;
import chabernac.protocol.echo.EchoProtocol;

public class IndirectReachablePeerTest extends TestCase {
  public void testIndirectReachablePeer(){
    AbstractPeer thePeer = new SocketPeer("1")
    .setChannel("CHANNEL")
    .addSupportedProtocol(RoutingProtocol.ID)
    .addSupportedProtocol(WebPeerProtocol.ID)
    .setTemporaryPeer(true);
    
    IndirectReachablePeer theNewPeer = new IndirectReachablePeer(thePeer);
    
    assertEquals("1", theNewPeer.getPeerId());
    assertEquals("CHANNEL", theNewPeer.getChannel());
    assertEquals(2, theNewPeer.getSupportedProtocols().size());
    assertTrue(theNewPeer.isProtocolSupported(RoutingProtocol.ID));
    assertTrue(theNewPeer.isProtocolSupported(WebPeerProtocol.ID));
    assertFalse(theNewPeer.isProtocolSupported(EchoProtocol.ID));
    assertTrue(theNewPeer.isTemporaryPeer());
  }
}
