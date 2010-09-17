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
  
	//the gateway for accessing this peer.  this is the same as the target peer
	//if the target peer can be reached directly
	private final AbstractPeer myGateway;
	
	private final long myCreationTime = System.currentTimeMillis();
	
	private final long myLastOnlineTime;
	
	public RoutingTableEntry ( AbstractPeer anHost , int anHopDistance , AbstractPeer anGateway, long aLastOnlineTime ) {
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
		myGateway = anGateway;
		checkValidity();
	}
	
	private void checkValidity(){
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
    return "<PeerEntry peerid='" + myPeer.getPeerId() + "' hopDistance='" + myHopDistance + "' gateway='" + myGateway.getPeerId() + "' creationTime='" + myCreationTime + "'/>";
  }
  
  public boolean equals(Object anObject){
    if(!(anObject instanceof RoutingTableEntry)) return false;
    RoutingTableEntry theEntry = (RoutingTableEntry)anObject;
    if(!getPeer().getPeerId().equals(theEntry.getPeer().getPeerId())) return false;
    if(getHopDistance() != theEntry.getHopDistance() ) return false;
    if(!getGateway().getPeerId().equals( theEntry.getGateway().getPeerId())) return false;
    return true;
  }
  
  public RoutingTableEntry entryForNextPeer(AbstractPeer aReceivedPeer){
    return new RoutingTableEntry(getPeer(), getHopDistance() + 1, aReceivedPeer, System.currentTimeMillis());
  }
  
  public RoutingTableEntry derivedEntry(int aHopDistance){
    return new RoutingTableEntry(getPeer(), aHopDistance, getGateway(), myLastOnlineTime);
  }
  
  public RoutingTableEntry incHopDistance(){
    return new RoutingTableEntry(getPeer(), getHopDistance() + 1, getGateway(), myLastOnlineTime);
  }

}