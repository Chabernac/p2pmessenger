/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RoutingTable implements Iterable< RoutingTableEntry >{
  private Map<Peer, RoutingTableEntry> myRoutingTable = new HashMap< Peer, RoutingTableEntry >();
  
  public synchronized void addRoutingTableEntry(RoutingTableEntry anEntry){
    if(myRoutingTable.containsKey( anEntry.getPeer() )){
      RoutingTableEntry thePeerEntry = myRoutingTable.get( anEntry.getPeer() );
      if(anEntry.closerThen( thePeerEntry )){
        myRoutingTable.put( anEntry.getPeer(), anEntry );
      }
    }
  }
  
  public synchronized Peer getGatewayForPeer(Peer aPeer) throws UnkwownPeerException{
    if(!myRoutingTable.containsKey( aPeer )) throw new UnkwownPeerException(aPeer, "Peer with id: " + aPeer.getPeerId() + " is not kwown in the routintble");
    
    return myRoutingTable.get( aPeer ).getGateway();
  }
  
  public synchronized Iterator< RoutingTableEntry > iterator(){
    return Collections.unmodifiableCollection(  myRoutingTable.values() ).iterator();
  }
  
  public synchronized void merge(RoutingTable anotherRoutingTable){
    for(Iterator< RoutingTableEntry > i = anotherRoutingTable.iterator(); i.hasNext();){
      RoutingTableEntry theEntry = i.next();
      theEntry.incrementHopDistance();
      addRoutingTableEntry( theEntry );
    }
  }
}
 