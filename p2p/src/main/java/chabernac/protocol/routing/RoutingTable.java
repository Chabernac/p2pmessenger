/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.Serializable;
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
import org.doomdark.uuid.UUID;

public class RoutingTable implements Iterable< RoutingTableEntry >, Serializable{
  
  private static final long serialVersionUID = -6991803452368949789L;

  private static Logger LOGGER = Logger.getLogger( RoutingTable.class );

  private String myLocalPeerId;
  private transient boolean isKeepHistory = false;
  private Map<String, RoutingTableEntry> myRoutingTable = new HashMap< String, RoutingTableEntry >();
  private transient Set<IRoutingTableListener> myRoutingTableListeners = new HashSet< IRoutingTableListener >();
  private transient List<RoutingTableEntryHistory> myRoutingTableEntryHistory = new ArrayList< RoutingTableEntryHistory >();

  public RoutingTable(String aLocalPeerId){
    myLocalPeerId = aLocalPeerId;
  }
  
  private UUID getUUIDForPeer(Peer aPeer){
    return new UUID(aPeer.getPeerId());
  }

  public synchronized void removeRoutingTableEntry(RoutingTableEntry anEntry){
    if(isKeepHistory){
      myRoutingTableEntryHistory.add( new RoutingTableEntryHistory(anEntry,RoutingTableEntryHistory.Action.DELETE) );
    }
    
    myRoutingTable.remove(anEntry.getPeer().getPeerId());
  }
  
  public synchronized void addRoutingTableEntry(RoutingTableEntry anEntry){
    if(isKeepHistory){
      myRoutingTableEntryHistory.add( new RoutingTableEntryHistory(anEntry,RoutingTableEntryHistory.Action.ADD ) );
    }
    
    if(anEntry.getPeer().getPeerId() == null || anEntry.getPeer().getPeerId().equals( "" )){
      throw new IllegalArgumentException("Received routing table entry with no peer id");
    }

    if(anEntry.getPeer().getPeerId().equalsIgnoreCase( getLocalPeerId() ) && anEntry.getGateway().getPeerId().equals( anEntry.getPeer().getPeerId() ) && anEntry.getHopDistance() > 0 && anEntry.getHopDistance() != RoutingTableEntry.MAX_HOP_DISTANCE){
      try{
        //if the comparison of the UUID of the peer entry and local peer entry is smaller than 0 then the given peer
        //had assigned his peer id first.  So we're at the point of hyjacking someones peer id, this may not happen, shut down the application
        if(getUUIDForPeer(anEntry.getPeer()).compareTo(getUUIDForPeer(getEntryForLocalPeer().getPeer())) < 0){
          throw new Error("Stopping application immediately because this peer id is already registered");
        }
      }catch(UnknownPeerException e){
      }
      return;
    }

    if(anEntry.getPeer().getPort() == 0){
      throw new IllegalArgumentException("Can not add an routing table entry with a peer that has port 0");
    }

    if(myRoutingTable.containsKey( anEntry.getPeer().getPeerId() )){
      RoutingTableEntry thePeerEntry = myRoutingTable.get( anEntry.getPeer().getPeerId() );

      //if the gateway of the local entry is the same as the peer from which the entry comes, then that entry is the most accurate
      //so upate the table

      if((thePeerEntry.getGateway().getPeerId().equals( anEntry.getGateway().getPeerId() ) ||
          //      if(thePeerEntry.getGateway().getPeerId().equals( aContainingPeerEntry ) ||
          anEntry.closerThen( thePeerEntry )) 
          //only update the entry if something changed
          &&!thePeerEntry.equals( anEntry )){

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
    synchronized(this){
      notifyAll();
    }
  }

  private void notifyListenersOfRoutingTableEntryChange(RoutingTableEntry anEntry){
    for(IRoutingTableListener theListener : myRoutingTableListeners){
      theListener.routingTableEntryChanged( anEntry );
    }
  }

  public synchronized Peer getGatewayForPeer(Peer aPeer) throws UnknownPeerException{
    if(!myRoutingTable.containsKey( aPeer.getPeerId() )) throw new UnknownPeerException(aPeer, "Peer with id: " + aPeer.getPeerId() + " is not kwown in the routingtable for peer: " + myLocalPeerId);

    return myRoutingTable.get( aPeer.getPeerId() ).getGateway();
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

  public synchronized void merge(RoutingTable anotherRoutingTable) throws SocketException, NoAvailableNetworkAdapterException, UnknownPeerException{
    for(Iterator< RoutingTableEntry > i = anotherRoutingTable.iterator(); i.hasNext();){
      RoutingTableEntry theEntry = i.next();
      //add all entries except the entry for ourselfs
      if(!theEntry.getPeer().getPeerId().equals( myLocalPeerId )){
        addRoutingTableEntry(theEntry.entryForNextPeer( anotherRoutingTable.getEntryForLocalPeer().getPeer() ) );
      }
    }
  }

  public List<RoutingTableEntry> getEntries(){
    return Collections.unmodifiableList(  new ArrayList< RoutingTableEntry >(myRoutingTable.values()) );
  }

  public List<RoutingTableEntry> getReachableEntriesEntries(){
    List<RoutingTableEntry> theEntries = new ArrayList< RoutingTableEntry >();
    for(RoutingTableEntry theEntry : myRoutingTable.values()){
      if(theEntry.isReachable()){
        theEntries.add(theEntry);
      }
    }
    return theEntries;
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
  
  public synchronized boolean containsEntryForPeer(String aPeerId){
	  return myRoutingTable.containsKey(aPeerId);
  }

  public synchronized RoutingTableEntry getEntryForPeer( String aPeerId ) throws UnknownPeerException{
    if(!myRoutingTable.containsKey( aPeerId )){
      throw new UnknownPeerException("The routing table does not contain a peer with id '" + aPeerId + "'");
    }
    return myRoutingTable.get( aPeerId );
  }

  public synchronized RoutingTableEntry getEntryForLocalPeer( ) throws UnknownPeerException {
    if(!myRoutingTable.containsKey( getLocalPeerId() )){
      throw new UnknownPeerException("The local entry is not known");
    }
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

  public void removeRoutingTableListener(IRoutingTableListener aListener){
    myRoutingTableListeners.remove( aListener );
  }

  public void removeAllroutingTableListeners(){
    myRoutingTableListeners.clear();
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

  public boolean isKeepHistory() {
    return isKeepHistory;
  }

  public void setKeepHistory( boolean anKeepHistory ) {
    isKeepHistory = anKeepHistory;
  }

  public List< RoutingTableEntryHistory > getHistory() {
    return Collections.unmodifiableList( myRoutingTableEntryHistory );
  }
  
  public void clearHistory(){
    myRoutingTableEntryHistory.clear();
  }
  
  
}
