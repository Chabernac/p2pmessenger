package chabernac.protocol;

import chabernac.protocol.routing.Peer;

public class AlreadyRunningException extends ProtocolException {
  private static final long serialVersionUID = -2584346282962440683L;
  private final Peer myPeer;

  public AlreadyRunningException(Peer aPeer) {
    super();
    myPeer = aPeer;
  }

  public Peer getPeer() {
    return myPeer;
  }
}
