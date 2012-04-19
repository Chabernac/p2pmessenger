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
	private final AbstractPeer myPeer;

	//the hop distance of the peer.  this indicates how many peers must 
	//be travelled to reach the destination peer
	private final int myHopDistance;
	
	//the time distance of the peer
	private final long myTimeDistance;
  
	//the gateway for accessing this peer.  this is the same as the target peer
	//if the target peer can be reached directly
	private final AbstractPeer myGateway;
	
	private final long myCreationTime = System.currentTimeMillis();
	
	private final long myLastOnlineTime;
	
	//create a routing table entry for this peer which is directly reachable
	public RoutingTableEntry ( AbstractPeer anHost ){
	  this(anHost, 1, anHost, System.currentTimeMillis(), 0);
	}
	
	public RoutingTableEntry ( AbstractPeer anHost , int anHopDistance , AbstractPeer anGateway, long aLastOnlineTime, long aTimeDistance ) {
		super();
		myPeer = anHost;
		if(anHopDistance >= MAX_HOP_DISTANCE){
		  myHopDistance = MAX_HOP_DISTANCE;
		  if(aLastOnlineTime > 0){
		    myLastOnlineTime = aLastOnlineTime;
		  } else {
		    myLastOnlineTime = System.currentTimeMillis();
		  }
		} else {
		  myHopDistance = anHopDistance;
		  myLastOnlineTime = System.currentTimeMillis();
		}
		myTimeDistance = aTimeDistance;
		myGateway = anGateway;
		checkValidity();
	}
	
	private void checkValidity(){
	  if(myPeer == null) throw new RuntimeException("Peer can not be null");
	  if(myGateway == null) throw new RuntimeException("Gateway can not be null");
	  if(myPeer.getPeerId() == null) throw new RuntimeException("Peer id can not be null");
    if(myGateway.getPeerId() == null) throw new RuntimeException("Gateway peer id can not be null");
	  if(!myPeer.getPeerId().equals( myGateway.getPeerId() ) && myHopDistance == 1){
	    //this is not possible in this case hop distance must be > 1
	    throw new RuntimeException("Hop distance can not be 1 when a gateway different from the peer is present");
	  }
	}

	public AbstractPeer getPeer() {
		return myPeer;
	}

	public int getHopDistance() {
		return myHopDistance;
	}

	public long getTimeDistance() {
    return myTimeDistance;
  }

  public AbstractPeer getGateway() {
		return myGateway;
	}

	public boolean closerThen(RoutingTableEntry anEntry){
		return myHopDistance < anEntry.getHopDistance();
	}

  public boolean isResponding() {
    return myHopDistance <= 1 && myGateway.getPeerId().equals( myPeer.getPeerId() );
  }

  public boolean isReachable() {
    return myHopDistance < MAX_HOP_DISTANCE;
  }

  public long getCreationTime() {
    return myCreationTime;
  }
  
  public long getLastOnlineTime() {
    return myLastOnlineTime;
  }

  public String toString(){
    return "<PeerEntry peerid='" + myPeer.getPeerId() + "' endpoint='" + myPeer.getEndPointRepresentation() + "' hopDistance='" + myHopDistance + "' gateway='" + myGateway.getPeerId() + "' creationTime='" + myCreationTime + "'/>";
  }
  
  public int hashCode(){
    return getPeer().getPeerId().hashCode();
  }
  
  public boolean equals(Object anObject){
    if(!(anObject instanceof RoutingTableEntry)) return false;
    RoutingTableEntry theEntry = (RoutingTableEntry)anObject;
    if(!getPeer().getPeerId().equals(theEntry.getPeer().getPeerId())) return false;
    if(getHopDistance() != theEntry.getHopDistance() ) return false;
    if(!getGateway().getPeerId().equals( theEntry.getGateway().getPeerId())) return false;
    return true;
  }
  
  public RoutingTableEntry entryForNextPeer(AbstractPeer aReceivedPeer, long aTimeDistance){
    return new RoutingTableEntry(getPeer(), getHopDistance() + 1, aReceivedPeer, System.currentTimeMillis(), getTimeDistance() + aTimeDistance);
  }
  
  public RoutingTableEntry derivedEntry(int aHopDistance){
    return new RoutingTableEntry(getPeer(), aHopDistance, getGateway(), myLastOnlineTime, getTimeDistance());
  }
  
  public RoutingTableEntry incHopDistance(){
    return new RoutingTableEntry(getPeer(), getHopDistance() + 1, getGateway(), myLastOnlineTime, getTimeDistance());
  }

}