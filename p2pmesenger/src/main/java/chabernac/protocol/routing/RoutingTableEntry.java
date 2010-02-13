/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;


public class RoutingTableEntry {
	//the peer for which this is an entry
	private Peer myPeer = null;

	//the hop distance of the peer.  this indicates how many peers must 
	//be travelled to reach the destination peer
	private int myHopDistance = 1000;

	//indicates if this peer is responding
	private boolean isResponding = false;

	//the gateway for accessing this peer.  this is the same as the target peer
	//if the target peer can be reached directly
	private Peer myGateway = null;

	public RoutingTableEntry (){}

	public RoutingTableEntry ( Peer anHost , int anHopDistance , Peer anGateway ) {
		super();
		myPeer = anHost;
		myHopDistance = anHopDistance;
		myGateway = anGateway;
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
	}

	public Peer getGateway() {
		return myGateway;
	}

	public void setGateway( Peer anGateway ) {
		myGateway = anGateway;
	}

	public boolean isResponding() {
		return isResponding;
	}

	public void setResponding(boolean isResponding) {
		this.isResponding = isResponding;
	}

	public boolean closerThen(RoutingTableEntry anEntry){
		return myHopDistance < anEntry.getHopDistance();
	}

	public void incrementHopDistance(){
		myHopDistance++;
	}
}
