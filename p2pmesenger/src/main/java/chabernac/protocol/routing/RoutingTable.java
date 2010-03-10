/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class RoutingTable implements Iterable< RoutingTableEntry >{
  private static Logger LOGGER = Logger.getLogger( RoutingTable.class );
  
  private long myLocalPeerId;
  private Map<Long, RoutingTableEntry> myRoutingTable = new HashMap< Long, RoutingTableEntry >();

  /**
   * you should not use this constructor
   * this one is only present to be able to stream to object to XML
   * always make sure the routing table has a local peer id
   * @deprecated
   */
  public RoutingTable(){}
  
  public RoutingTable(long aLocalPeerId){
    myLocalPeerId = aLocalPeerId;
  }
  
  public synchronized void addRoutingTableEntry(RoutingTableEntry anEntry){
    addRoutingTableEntry( getLocalPeerId(), anEntry );
  }
  
  public synchronized void addRoutingTableEntry(long aContainingPeerEntry, RoutingTableEntry anEntry){
    if(myRoutingTable.containsKey( anEntry.getPeer().getPeerId() )){
      RoutingTableEntry thePeerEntry = myRoutingTable.get( anEntry.getPeer().getPeerId() );

      //if the gateway of the local entry is the same as the peer from which the entry comes, then that entry is the most accurate
      //so upate the table
      if(thePeerEntry.getGateway().getPeerId() == aContainingPeerEntry || anEntry.closerThen( thePeerEntry )){
        myRoutingTable.put( anEntry.getPeer().getPeerId(), anEntry );
      }
      
      LOGGER.debug( "Updated routing table entry to routing table for peer: " + myLocalPeerId + " : "  + anEntry);
    } else {
      myRoutingTable.put(anEntry.getPeer().getPeerId(), anEntry);
      LOGGER.debug( "Added a new routing table entry to routing table for peer: " + myLocalPeerId + " : "  + anEntry);
    }
  }
  
  public synchronized Peer getGatewayForPeer(Peer aPeer) throws UnkwownPeerException{
    if(!myRoutingTable.containsKey( aPeer )) throw new UnkwownPeerException(aPeer, "Peer with id: " + aPeer.getPeerId() + " is not kwown in the routintble");
    
    return myRoutingTable.get( aPeer ).getGateway();
  }
  
  public synchronized Peer obtainLocalPeer() throws SocketException {
    if(!myRoutingTable.containsKey( myLocalPeerId )){
      Peer theLocalPeer = new Peer( myLocalPeerId );
      theLocalPeer.detectLocalInterfaces();
      myRoutingTable.put( myLocalPeerId, new RoutingTableEntry( theLocalPeer, 1, theLocalPeer ) );
    }
    return myRoutingTable.get( myLocalPeerId ).getPeer();
  }
  
  public long getLocalPeerId() {
    return myLocalPeerId;
  }

  public void setLocalPeerId( long anLocalPeerId ) {
    myLocalPeerId = anLocalPeerId;
  }

  public synchronized Iterator< RoutingTableEntry > iterator(){
    return Collections.unmodifiableCollection(  myRoutingTable.values() ).iterator();
  }
  
  public synchronized void merge(RoutingTable anotherRoutingTable) throws SocketException{
    for(Iterator< RoutingTableEntry > i = anotherRoutingTable.iterator(); i.hasNext();){
      RoutingTableEntry theEntry = i.next();
      //change the gateway to the peer from which this routing tables comes from
      theEntry.setGateway( anotherRoutingTable.obtainLocalPeer() );
      //increment the hop distance
      theEntry.incrementHopDistance();
      addRoutingTableEntry(anotherRoutingTable.getLocalPeerId(), theEntry );
    }
  }
  
  public List<RoutingTableEntry> getEntries(){
    return Collections.unmodifiableList(  new ArrayList< RoutingTableEntry >(myRoutingTable.values()) );
  }

  /**
   * this method is necessar for being able to serialize the table 
   * but you should not use it as it is not thread safe.
   * @return
   * @deprecated
   */
  public Map< Long, RoutingTableEntry > getRoutingTable() {
    return new HashMap< Long, RoutingTableEntry >(myRoutingTable);
  }

  public void setRoutingTable( Map< Long, RoutingTableEntry > anRoutingTable ) {
    myRoutingTable = anRoutingTable;
  }

  public synchronized RoutingTableEntry getEntryForPeer( long aPeerId ) {
    return myRoutingTable.get( aPeerId );
  }
  
  public synchronized RoutingTableEntry getEntryForLocalPeer( ) {
    return myRoutingTable.get( getLocalPeerId() );
  }
}
 