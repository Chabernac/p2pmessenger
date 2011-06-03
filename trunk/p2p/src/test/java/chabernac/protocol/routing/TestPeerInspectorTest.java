package chabernac.protocol.routing;

import junit.framework.TestCase;

public class TestPeerInspectorTest extends TestCase {
  public void testTestPeerInspector(){
    TestPeerInspector theInspector = new TestPeerInspector();
    
    DummyPeer thePeer = new DummyPeer("1");
    
    assertTrue(theInspector.isValidPeer(thePeer));
  }
}
