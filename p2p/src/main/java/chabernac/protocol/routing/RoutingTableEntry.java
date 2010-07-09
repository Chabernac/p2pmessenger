/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.Serializable;


public class RoutingTableEntry implements Serializable{
  private static final long serialVersionUID = -1285319346105443401L;

  public static int MAX_HOP_DISTANCE = 6;
  
	//the peer for which this is an entry
	private Peer myPeer = null;

	//the hop distance of the peer.  this indicates how many peers must 
	//be travelled to reach the destination peer
	private int myHopDistance = MAX_HOP_DISTANCE;
  
	//the gateway for accessing this peer.  this is the same as the target peer
	//if the target peer can be reached directly
	private Peer myGateway = null;
	
	private long myOnlineTime;

	public RoutingTableEntry (){}

	public RoutingTableEntry ( Peer anHost , int anHopDistance , Peer anGateway, long anOnlineTime ) {
		super();
		myPeer = anHost;
		myHopDistance = anHopDistance;
		if(myHopDistance > MAX_HOP_DISTANCE){
		  myHopDistance = MAX_HOP_DISTANCE;
		}
		myGateway = anGateway;
		myOnlineTime = anOnlineTime;
		checkValidity();
	}
	
	private void checkValidity(){
	  if(!myPeer.getPeerId().equals( myGateway.getPeerId() ) && myHopDistance == 1){
	    //this is not possible in this case hop distance must be > 1
	    throw new RuntimeException("Hop distance can not be 1 when a gateway different from the peer is present");
	  }
	}

	public Peer getPeer() {
		return myPeer;
	}

	public void setPeer( Peer anPeer ) {
		myPeer = anPeer;
	}

	public int getHopDistance() {
		return myHopDistance;
	}

	public void setHopDistance( int anHopDistance ) {
		myHopDistance = anHopDistance;
		if(myHopDistance == 1 && myPeer != null){
		  myGateway = myPeer;
		}
	}

	public Peer getGateway() {
		return myGateway;
	}

	public void setGateway( Peer anGateway ) {
		myGateway = anGateway;
	}

	public boolean closerThen(RoutingTableEntry anEntry){
		return myHopDistance < anEntry.getHopDistance();
	}

	public void incrementHopDistance(){
	  if(myHopDistance < MAX_HOP_DISTANCE){
	    myHopDistance++;
	  }
	}

  public boolean isResponding() {
    return myHopDistance <= 1 && myGateway.getPeerId().equals( myPeer.getPeerId() );
  }

  public boolean isReachable() {
    return myHopDistance < MAX_HOP_DISTANCE;
  }


  public String toString(){
    return "<PeerEntry peerid='" + myPeer.getPeerId() + "' hopDistance='" + myHopDistance + "' gateway='" + myGateway.getPeerId() + "'/>";
  }
  
  public boolean equals(Object anObject){
    if(!(anObject instanceof RoutingTableEntry)) return false;
    RoutingTableEntry theEntry = (RoutingTableEntry)anObject;
    if(!getPeer().getPeerId().equals(theEntry.getPeer().getPeerId())) return false;
    if(getHopDistance() != theEntry.getHopDistance() ) return false;
    if(!getGateway().getPeerId().equals( theEntry.getGateway().getPeerId())) return false;
    return true;
  }
  
  public RoutingTableEntry entryForNextPeer(Peer aReceivedPeer){
    return new RoutingTableEntry(getPeer(), getHopDistance() + 1, aReceivedPeer, getOnlineTime());
  }
  
  public RoutingTableEntry derivedEntry(int aHopDistance){
    return new RoutingTableEntry(getPeer(), aHopDistance, getGateway(), getOnlineTime());
  }
  
  public RoutingTableEntry incHopDistance(){
    return new RoutingTableEntry(getPeer(), getHopDistance() + 1, getGateway(), getOnlineTime());
  }

  public long getOnlineTime() {
    return myOnlineTime;
  }

  public void setOnlineTime( long anOnlineTime ) {
    myOnlineTime = anOnlineTime;
  }
}