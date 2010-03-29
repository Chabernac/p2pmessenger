/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class RoutingTable implements Iterable< RoutingTableEntry >{
  private static Logger LOGGER = Logger.getLogger( RoutingTable.class );

  private String myLocalPeerId;
  private Map<String, RoutingTableEntry> myRoutingTable = new HashMap< String, RoutingTableEntry >();
  private Set<IRoutingTableListener> myRoutingTableListeners = new HashSet< IRoutingTableListener >();

  /**
   * you should not use this constructor
   * this one is only present to be able to stream to object to XML
   * always make sure the routing table has a local peer id
   * @deprecated
   */
  public RoutingTable(){}

  public RoutingTable(String aLocalPeerId){
    myLocalPeerId = aLocalPeerId;
  }

  public synchronized void addRoutingTableEntry(RoutingTableEntry anEntry){
    if(myRoutingTable.containsKey( anEntry.getPeer().getPeerId() )){
      RoutingTableEntry thePeerEntry = myRoutingTable.get( anEntry.getPeer().getPeerId() );

      //if the gateway of the local entry is the same as the peer from which the entry comes, then that entry is the most accurate
      //so upate the table
      
      if(thePeerEntry.getGateway().getPeerId().equals( anEntry.getGateway().getPeerId() ) ||
//      if(thePeerEntry.getGateway().getPeerId().equals( aContainingPeerEntry ) ||
          anEntry.closerThen( thePeerEntry )){

        myRoutingTable.put( anEntry.getPeer().getPeerId(), anEntry );
        notifyListenersOfRoutingTableEntryChange( anEntry );
//        LOGGER.debug( "Updated routing table entry to routing table for peer:   " + myLocalPeerId + " : "  + anEntry );
        
//        if(aContainingPeerEntry.equals( myLocalPeerId ) && anEntry.getGateway().getPeerId() == anEntry.getPeer().getPeerId() && anEntry.getHopDistance() >= 2 && anEntry.getHopDistance() <= 5){
//          
//          Exception e = new Exception();
//          LOGGER.error("We have received an entry of our selfs", e);
//        }
        
        if(thePeerEntry.getGateway().getPeerId().equals( anEntry.getGateway().getPeerId() )){
          LOGGER.debug( "Updated routing table entry to routing table for peer:   " + myLocalPeerId + " : "  + anEntry + " because gateway of local entry: '" + thePeerEntry.getGateway().getPeerId() + "' is the peer from which we received the entry" ); 
        } else {
          LOGGER.debug( "Updated routing table entry to routing table for peer:   " + myLocalPeerId + " : "  + anEntry + " because the entry is shorter than the entry we have" );
        }
      }

    } else {
      myRoutingTable.put(anEntry.getPeer().getPeerId(), anEntry);
      notifyListenersOfRoutingTableEntryChange( anEntry );
      LOGGER.debug( "Added a new routing table entry to routing table for peer: " + myLocalPeerId + " : "  + anEntry);
    }
  }
  
  private void notifyListenersOfRoutingTableEntryChange(RoutingTableEntry anEntry){
    for(IRoutingTableListener theListener : myRoutingTableListeners){
      theListener.routingTableEntryChanged( anEntry );
    }
  }

  public synchronized Peer getGatewayForPeer(Peer aPeer) throws UnkwownPeerException{
    if(!myRoutingTable.containsKey( aPeer.getPeerId() )) throw new UnkwownPeerException(aPeer, "Peer with id: " + aPeer.getPeerId() + " is not kwown in the routingtable for peer: " + myLocalPeerId);

    return myRoutingTable.get( aPeer.getPeerId() ).getGateway();
  }

  public synchronized Peer obtainLocalPeer() throws SocketException {
    if(!myRoutingTable.containsKey( myLocalPeerId )){
      Peer theLocalPeer = new Peer( myLocalPeerId );
      theLocalPeer.detectLocalInterfaces();
      myRoutingTable.put( myLocalPeerId, new RoutingTableEntry( theLocalPeer, 1, theLocalPeer ) );
    }
    return myRoutingTable.get( myLocalPeerId ).getPeer();
  }

  public String getLocalPeerId() {
    return myLocalPeerId;
  }

  public void setLocalPeerId( String anLocalPeerId ) {
    myLocalPeerId = anLocalPeerId;
  }

  public synchronized Iterator< RoutingTableEntry > iterator(){
    return Collections.unmodifiableCollection(  myRoutingTable.values() ).iterator();
  }

  public synchronized void merge(RoutingTable anotherRoutingTable) throws SocketException{
    for(Iterator< RoutingTableEntry > i = anotherRoutingTable.iterator(); i.hasNext();){
      RoutingTableEntry theEntry = i.next();
      addRoutingTableEntry(theEntry.entryForNextPeer( anotherRoutingTable.obtainLocalPeer() ) );
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
  public Map< String, RoutingTableEntry > getRoutingTable() {
    return new HashMap< String, RoutingTableEntry >(myRoutingTable);
  }

  public void setRoutingTable( Map< String, RoutingTableEntry > anRoutingTable ) {
    myRoutingTable = anRoutingTable;
  }

  public synchronized RoutingTableEntry getEntryForPeer( String aPeerId ) {
    return myRoutingTable.get( aPeerId );
  }

  public synchronized RoutingTableEntry getEntryForLocalPeer( ) {
    return myRoutingTable.get( getLocalPeerId() );
  }

  public Set<Peer> getAllPeers(){
    Set<Peer> thePeers = new HashSet< Peer >();
    for(RoutingTableEntry theEntry : myRoutingTable.values()){
      thePeers.add(theEntry.getPeer());
      thePeers.add(theEntry.getGateway());
    }
    return thePeers;
  }

  public void add( RoutingTable anTable ) {
    //copy all entries from the given table to this table
    for(RoutingTableEntry theEntry : anTable){
      addRoutingTableEntry( theEntry );
    }
  }
  
  public void addRoutingTableListener(IRoutingTableListener aListener){
    myRoutingTableListeners.add( aListener );
  }
  
  public void removeAllButLocalPeer(){
    for(Iterator< RoutingTableEntry > i = myRoutingTable.values().iterator();i.hasNext();){
      RoutingTableEntry theEntry = i.next();
      if(!theEntry.getPeer().getPeerId().equalsIgnoreCase( myLocalPeerId )){
        i.remove();
      }
    }
  }
  
  public int getNrOfReachablePeers(){
    int theCounter = 0;
    for(RoutingTableEntry theEntry : getEntries()){
      if(theEntry.isReachable()){
        theCounter ++;
      }
    }
    return theCounter;
  }
}
