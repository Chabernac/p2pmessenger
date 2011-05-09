/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;


/**
 * Class which represents a peer which can not be reached directly 
 * all communication to this peer will be send trough another peer
 * e.g. a webpeer  
 *
 */

public class IndirectReachablePeer extends AbstractPeer {
  private static final long serialVersionUID = 214472238814548564L;

  public IndirectReachablePeer( String anPeerId ) {
    super( anPeerId );
  }


  @Override
  public String getEndPointRepresentation() {
    return null;
  }

  @Override
  public boolean isSameEndPointAs( AbstractPeer aPeer ) {
    return false;
  }

  @Override
  public boolean isValidEndPoint() {
    return true;
  }
}
