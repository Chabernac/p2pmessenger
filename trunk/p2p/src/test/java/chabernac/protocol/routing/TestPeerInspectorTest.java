package chabernac.protocol.routing;

import junit.framework.TestCase;

public class TestPeerInspectorTest extends TestCase {
  public void testTestPeerInspector() throws NoAvailableNetworkAdapterException{
    TestPeerInspector theInspector = new TestPeerInspector();
    
    DummyPeer thePeer = new DummyPeer("1");
    
    assertTrue(theInspector.isValidPeer(thePeer));
    
    SocketPeer theSocketPeer = new SocketPeer("1", 12700);
    SocketPeer theSocketPeer2 = new SocketPeer("2", 12708);
    
    assertFalse(theInspector.isValidPeer(theSocketPeer));
    assertFalse(theInspector.isValidPeer(theSocketPeer2));
  }
}
