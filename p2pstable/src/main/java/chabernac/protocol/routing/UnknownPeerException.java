/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

public class UnknownPeerException extends Exception {
  
  private static final long serialVersionUID = 686844496245613583L;
  private AbstractPeer myPeer = null;

  public UnknownPeerException (AbstractPeer aPeer) {
    super();
    myPeer = aPeer;
  }

  public UnknownPeerException ( AbstractPeer aPeer, String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
    myPeer = aPeer;
  }

  public UnknownPeerException ( AbstractPeer aPeer, String anMessage ) {
    super( anMessage );
    myPeer = aPeer;
  }

  public UnknownPeerException ( AbstractPeer aPeer, Throwable anCause ) {
    super( anCause );
    myPeer = aPeer;
  }
  
  public AbstractPeer getPeer(){
    return myPeer;
  }

  public UnknownPeerException () {
    super();
  }

  public UnknownPeerException ( String anMessage , Throwable anCause ) {
    super( anMessage, anCause );
  }

  public UnknownPeerException ( String anMessage ) {
    super( anMessage );
  }

  public UnknownPeerException ( Throwable anCause ) {
    super( anCause );
  }
}
