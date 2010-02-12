/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

public class UnkwownPeerException extends Exception {
  
  private static final long serialVersionUID = 686844496245613583L;
  private Peer myPeer = null;

  public UnkwownPeerException (Peer aPeer) {
    super();
    myPeer = aPeer;
  }

  public UnkwownPeerException ( Peer aPeer, String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
    myPeer = aPeer;
  }

  public UnkwownPeerException ( Peer aPeer, String anMessage ) {
    super( anMessage );
    myPeer = aPeer;
  }

  public UnkwownPeerException ( Peer aPeer, Throwable anCause ) {
    super( anCause );
    myPeer = aPeer;
  }
  
  public Peer getPeer(){
    return myPeer;
  }

}
