/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.routing;

public class Peer {
  private long myPeerId;
  private String myHost = null;
  private int myPort;
  
  public Peer (){}
  
  public Peer ( long anPeerId ) {
    super();
    myPeerId = anPeerId;
  }
  
  public String getHost() {
    return myHost;
  }
  public void setHost( String anHost ) {
    myHost = anHost;
  }
  public int getPort() {
    return myPort;
  }
  public void setPort( int anPort ) {
    myPort = anPort;
  }
  
  public long getPeerId() {
    return myPeerId;
  }

  public void setPeerId( long anPeerId ) {
    myPeerId = anPeerId;
  }

  public boolean equals(Object anObject){
    if(!(anObject instanceof Peer)) return false;
    Peer thePeer = (Peer)anObject;
    
    return myPeerId == thePeer.getPeerId();
  }
  
  public int hashCode(){
    return (int)myPeerId;
  }
}
