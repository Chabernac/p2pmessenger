/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class RoutingTable implements Iterable< RoutingTableEntry >{
  private static Logger LOGGER = Logger.getLogger( RoutingTable.class );
  
  private Peer myPeer = null;
  private Map<Peer, RoutingTableEntry> myRoutingTable = new HashMap< Peer, RoutingTableEntry >();
  
  public RoutingTable(){}
  
  public RoutingTable(Peer aPeer){
    myPeer = aPeer;
  }
  
  public synchronized void addRoutingTableEntry(RoutingTableEntry anEntry){
    if(myRoutingTable.containsKey( anEntry.getPeer() )){
      RoutingTableEntry thePeerEntry = myRoutingTable.get( anEntry.getPeer() );
      if(anEntry.closerThen( thePeerEntry ) || (!thePeerEntry.isResponding() && anEntry.isResponding())){
        myRoutingTable.put( anEntry.getPeer(), anEntry );
        if(!thePeerEntry.isResponding()){
          //if the entry is repsonding for the remote system but not for the local system
          //then set it to non responding as this is the real situation.
          //this way we now the local system can not reach the peer directly but only trough the gateway
          anEntry.setResponding( false );
        }
        LOGGER.debug( "Updated routing table entry to routing table for peer: " + myPeer.getPeerId() + " : "  + anEntry);
      }
    } else {
      myRoutingTable.put(anEntry.getPeer(), anEntry);
      LOGGER.debug( "Added a new routing table entry to routing table for peer: " + myPeer.getPeerId() + " : "  + anEntry);
    }
  }
  
  public synchronized Peer getGatewayForPeer(Peer aPeer) throws UnkwownPeerException{
    if(!myRoutingTable.containsKey( aPeer )) throw new UnkwownPeerException(aPeer, "Peer with id: " + aPeer.getPeerId() + " is not kwown in the routintble");
    
    return myRoutingTable.get( aPeer ).getGateway();
  }
  
  public Peer getPeer() {
    return myPeer;
  }

  public void setPeer( Peer anPeer ) {
    myPeer = anPeer;
  }

  public synchronized Iterator< RoutingTableEntry > iterator(){
    return Collections.unmodifiableCollection(  myRoutingTable.values() ).iterator();
  }
  
  public synchronized void merge(RoutingTable anotherRoutingTable){
    for(Iterator< RoutingTableEntry > i = anotherRoutingTable.iterator(); i.hasNext();){
      RoutingTableEntry theEntry = i.next();
      //change the gateway to the peer from which this routing tables comes from
      theEntry.setGateway( anotherRoutingTable.getPeer() );
      //increment the hop distance
      theEntry.incrementHopDistance();
      addRoutingTableEntry( theEntry );
    }
  }
  
  public List<RoutingTableEntry> getEntries(){
    return Collections.unmodifiableList(  new ArrayList< RoutingTableEntry >(myRoutingTable.values()) );
  }

  public Map< Peer, RoutingTableEntry > getRoutingTable() {
    return myRoutingTable;
  }

  public void setRoutingTable( Map< Peer, RoutingTableEntry > anRoutingTable ) {
    myRoutingTable = anRoutingTable;
  }
}
 