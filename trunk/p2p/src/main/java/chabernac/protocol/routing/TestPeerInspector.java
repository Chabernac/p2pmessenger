package chabernac.protocol.routing;

import chabernac.tools.TestTools;

public class TestPeerInspector implements iPeerInspector {

  @Override
  public boolean isValidPeer(AbstractPeer aPeer) {
    //only return true if the peer was created in the same context as we currently are
    return TestTools.isInUnitTest() == aPeer.isTestPeer();
  }

}
