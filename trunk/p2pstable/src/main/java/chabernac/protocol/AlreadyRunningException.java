package chabernac.protocol;

import chabernac.protocol.routing.AbstractPeer;

public class AlreadyRunningException extends ProtocolException {
  private static final long serialVersionUID = -2584346282962440683L;
  private final AbstractPeer myPeer;

  public AlreadyRunningException(AbstractPeer aPeer) {
    super();
    myPeer = aPeer;
  }

  public AbstractPeer getPeer() {
    return myPeer;
  }
}
