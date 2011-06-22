package chabernac.protocol.routing;

import org.apache.log4j.Logger;

import chabernac.tools.TestTools;

public class TestPeerInspector implements iPeerInspector {
  private static Logger LOGGER = Logger.getLogger(TestPeerInspector.class);
  
  private static boolean isInUnitTest = TestTools.isInUnitTest();

  @Override
  public boolean isValidPeer(AbstractPeer aPeer) {
    //do some specific tests for socket peer, it should not be necessar but it seems though that still peers are exchanged
    //between unit tests and real systems
    
//    if(aPeer instanceof SocketPeer){
//      SocketPeer thePeer = (SocketPeer)aPeer;
//      if(thePeer.getPort() < RoutingProtocol.START_PORT){
//        return false;
//      }
//      if(thePeer.getPort() > RoutingProtocol.END_PORT){
//        return false;
//      }
//    }
    
    //only return true if the peer was created in the same context as we currently are
    LOGGER.debug( isInUnitTest + " ==? " + aPeer.isTestPeer() );
    return isInUnitTest == aPeer.isTestPeer();
  }

}
