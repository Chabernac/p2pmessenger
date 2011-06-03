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
import java.util.concurrent.TimeUnit;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.log4j.Logger;
import org.doomdark.uuid.UUID;

@PersistenceCapable
public class RoutingTable implements Iterable< RoutingTableEntry >, Serializable{

  private static final long serialVersionUID = -6991803452368949789L;

  private static Logger LOGGER = Logger.getLogger( RoutingTable.class );

  private String myLocalPeerId;
  private transient boolean isKeepHistory = false;
  private Map<String, RoutingTableEntry> myRoutingTable = new HashMap< String, RoutingTableEntry >();
  private transient Set<IRoutingTableListener> myRoutingTableListeners = null;
  private transient List<RoutingTableEntryHistory> myRoutingTableEntryHistory = null;
  private transient iPeerInspector myPeerInspector = new TestPeerInspector();

  public RoutingTable(String aLocalPeerId){
    myLocalPeerId = aLocalPeerId;
  }

  private UUID getUUIDForPeer(AbstractPeer aPeer){
    return new UUID(aPeer.getPeerId());
  }

  public synchronized void removeRoutingTableEntry(RoutingTableEntry anEntry){
    inspectRoutingTableEntryHistory();
    
    if(isKeepHistory){
      myRoutingTableEntryHistory.add( new RoutingTableEntryHistory(anEntry,RoutingTableEntryHistory.Action.DELETE) );
    }

    myRoutingTable.remove(anEntry.getPeer().getPeerId());
    
    checkIntegrityForEntry( anEntry.derivedEntry( RoutingTableEntry.MAX_HOP_DISTANCE ) );
    
    notifyListenersOfRoutingTableEntryRemoval( anEntry );
  }

  /**
   * user this method for test purposes only
   * with this method you can insert all kind of entries
   * no check is done
   * 
   * @param anEntry
   */
  public synchronized void addEntry(RoutingTableEntry anEntry){
    myRoutingTable.put(anEntry.getPeer().getPeerId(), anEntry);
  }
  
  public synchronized void addRoutingTableEntry(RoutingTableEntry anEntry){
    inspectRoutingTableEntryHistory();
    
    RoutingTableEntryHistory theHistoryRow =  null;
    if(isKeepHistory){
      theHistoryRow = new RoutingTableEntryHistory(anEntry,RoutingTableEntryHistory.Action.ADD );
      myRoutingTableEntryHistory.add( theHistoryRow );
    }

    if(anEntry.getPeer().getPeerId() == null || anEntry.getPeer().getPeerId().equals( "" )){
      throw new IllegalArgumentException("Received routing table entry with no peer id");
    }
    
    //ignore entries which have as gateway our selfs, this might create loops in the routing table hierarchy
    if(!anEntry.getPeer().getPeerId().equals( myLocalPeerId ) && anEntry.getGateway().getPeerId().equals( myLocalPeerId )){
      return;
    }
    
    if(!isValidPeer(anEntry.getPeer())){
      
    }
    
    if(anEntry.getHopDistance() == RoutingTableEntry.MAX_HOP_DISTANCE && 
       !containsEntryForPeer( anEntry.getPeer().getPeerId() ) &&
       !anEntry.getGateway().getPeerId().equals( myLocalPeerId )){
      //there is no need for adding peer entries of peers received from another peer which can not be reached
      return;
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

    if(!anEntry.getPeer().isValidEndPoint()){
      LOGGER.error("Can not add a peer with an invalid end point '" + anEntry.getPeer().toString() + "'");
//      throw new IllegalArgumentException("Can not add a peer with an invalid end point");
    }
    
//    removeEntriesOlderThanAndOnTheSameSocketAs(anEntry);

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
        if(theHistoryRow != null) theHistoryRow.setResultedInUpdate( true );
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
        checkIntegrityForEntry( anEntry );
      }

    } else {
      myRoutingTable.put(anEntry.getPeer().getPeerId(), anEntry);
      if(theHistoryRow != null) theHistoryRow.setResultedInUpdate( true );
      notifyListenersOfRoutingTableEntryChange( anEntry );
      LOGGER.debug( "Added a new routing table entry to routing table for peer: " + myLocalPeerId + " : "  + anEntry);
      checkIntegrityForEntry( anEntry );
    }
    synchronized(this){
      notifyAll();
    }
  }
  
  private boolean isValidPeer(AbstractPeer anPeer) {
    if(myPeerInspector == null) return true;
    return myPeerInspector.isValidPeer(anPeer);
  }

  private void checkIntegrityForEntry(RoutingTableEntry anEntry){
    if(!anEntry.isReachable()){
      //look for entries which have this peer as gateway.  they will not be reachable as well
      for(RoutingTableEntry theEntry : getReachableEntriesEntries()){
        if(theEntry.getGateway().getPeerId().equals( anEntry.getPeer().getPeerId() )){
          //this entry will not be reachable  because the gateway is not reachable
          RoutingTableEntry theNewEntry = theEntry.derivedEntry( RoutingTableEntry.MAX_HOP_DISTANCE );
          addRoutingTableEntry( theNewEntry );
        }
      }
    }
  }
  
  private void inspectRoutingTableEntryHistory() {
    if(myRoutingTableEntryHistory == null){
      myRoutingTableEntryHistory = new ArrayList< RoutingTableEntryHistory >();
    }
    
  }

  /**
   * This method just solves a problem which should not occure.
   * Sometimes different routing tables are added with peers which reside on the same host and port
   * this is of course not possible.  So we remove the older peer entries which are on the same host and port
   */
//  private void removeEntriesOlderThanAndOnTheSameSocketAs(RoutingTableEntry anEntry){
//    List<RoutingTableEntry> theEntriesToRemove = new ArrayList<RoutingTableEntry>();
//    for(RoutingTableEntry theEntry : myRoutingTable.values()){
//      if(theEntry.getPeer().isSameEndPointAs(anEntry.getPeer()) && theEntry.getCreationTime() < anEntry.getCreationTime()){
//        theEntriesToRemove.add(theEntry);
//      }
//    }
//    for(RoutingTableEntry theEntry : theEntriesToRemove){
//      LOGGER.error("Removing entry with peer id: '" + theEntry.getPeer().getPeerId() + "' because it is on the same host and port as the new entry: '" + anEntry.getPeer().getPeerId() + "'");
//      removeRoutingTableEntry(theEntry);
//    }
//  }
  
  private void inspectListeners(){
    if(myRoutingTableListeners == null){
      myRoutingTableListeners = new HashSet< IRoutingTableListener >();
    }
  }

  private void notifyListenersOfRoutingTableEntryChange(RoutingTableEntry anEntry){
    inspectListeners();
    for(IRoutingTableListener theListener : myRoutingTableListeners){
      theListener.routingTableEntryChanged( anEntry );
    }
  }
  
  private void notifyListenersOfRoutingTableEntryRemoval(RoutingTableEntry anEntry){
    inspectListeners();
    for(IRoutingTableListener theListener : myRoutingTableListeners){
      theListener.routingTableEntryRemoved( anEntry );
    }
  }

  public AbstractPeer getGatewayForPeer(AbstractPeer aPeer) throws UnknownPeerException{
    if(!copyOfRoutingTable().containsKey( aPeer.getPeerId() )) throw new UnknownPeerException(aPeer, "Peer with id: " + aPeer.getPeerId() + " is not kwown in the routingtable for peer: " + myLocalPeerId);

    return copyOfRoutingTable().get( aPeer.getPeerId() ).getGateway();
  }

  public String getLocalPeerId() {
    return myLocalPeerId;
  }

  public void setLocalPeerId( String anLocalPeerId ) {
    myLocalPeerId = anLocalPeerId;
  }

  public Iterator< RoutingTableEntry > iterator(){
    return Collections.unmodifiableCollection(  copyOfRoutingTable().values() ).iterator();
  }

  public synchronized void merge(RoutingTable anotherRoutingTable) throws SocketException, NoAvailableNetworkAdapterException, UnknownPeerException{
    for(Iterator< RoutingTableEntry > i = anotherRoutingTable.iterator(); i.hasNext();){
      RoutingTableEntry theEntry = i.next();
      //add all entries except the entry for ourselfs
      //and except the entries which have our peer id as gateway, otherwise loops may be created in the routing table hierarchy
      if(!theEntry.getPeer().getPeerId().equals( myLocalPeerId )){
        addRoutingTableEntry(theEntry.entryForNextPeer( anotherRoutingTable.getEntryForLocalPeer().getPeer() ) );
      }
    }
    
//    List<RoutingTableEntry> theEntriesToRemove = new ArrayList<RoutingTableEntry>();
//    //now delete the entries of the local routing table which have the other peer as a gateway and which are not present in the other routing table
//    for(Iterator< RoutingTableEntry > i = iterator(); i.hasNext();){
//      RoutingTableEntry theEntry = i.next();
//      if(theEntry.getGateway().getPeerId().equals(anotherRoutingTable.getLocalPeerId()) && 
//         !anotherRoutingTable.containsEntryForPeer(theEntry.getPeer().getPeerId())){
//        theEntriesToRemove.add(theEntry);
//      }
//    }
//    
//    for(RoutingTableEntry theEntry : theEntriesToRemove){
//      removeRoutingTableEntry(theEntry);
//    }
    
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

  public void setRoutingTable( Map< String, RoutingTableEntry > anRoutingTable ) {
    myRoutingTable = anRoutingTable;
  }
  
  private Map< String, RoutingTableEntry > copyOfRoutingTable(){
    return new HashMap< String, RoutingTableEntry >(myRoutingTable);
  }

  public boolean containsEntryForPeer(String aPeerId){
    //take a copy and then test it for the key
    //this way we can not have concurrent modification errors
    //and we avoid deadlocks because there is no need for synchronization
    return copyOfRoutingTable().containsKey(aPeerId);
  }

  public RoutingTableEntry getEntryForPeer( String aPeerId ) throws UnknownPeerException{
    if(!copyOfRoutingTable().containsKey( aPeerId )){
      throw new UnknownPeerException("The routing table does not contain a peer with id '" + aPeerId + "'");
    }
    return copyOfRoutingTable().get( aPeerId );
  }

  public RoutingTableEntry getEntryForLocalPeer( ) throws UnknownPeerException {
    if(!copyOfRoutingTable().containsKey( getLocalPeerId() )){
      throw new UnknownPeerException("The local entry is not known");
    }
    return copyOfRoutingTable().get( getLocalPeerId() );
  }

  public Set<AbstractPeer> getAllPeers(){
    Set<AbstractPeer> thePeers = new HashSet< AbstractPeer >();
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
    inspectListeners();
    myRoutingTableListeners.add( aListener );
  }

  public void removeRoutingTableListener(IRoutingTableListener aListener){
    inspectListeners();
    myRoutingTableListeners.remove( aListener );
  }

  public void removeAllRoutingTableListeners(){
    inspectListeners();
    myRoutingTableListeners.clear();
  }

  public synchronized void removeAllButLocalPeer(){
    for(Iterator< RoutingTableEntry > i = myRoutingTable.values().iterator();i.hasNext();){
      RoutingTableEntry theEntry = i.next();
      if(!theEntry.getPeer().getPeerId().equalsIgnoreCase( myLocalPeerId )){
        i.remove();
      }
    }
  }
  
  public synchronized void removeEntriesOlderThan(int aNumber, TimeUnit aTimeUnit){
    long theOldestTime = System.currentTimeMillis() - aTimeUnit.toMillis( aNumber );
    
    for(Iterator< RoutingTableEntry > i = myRoutingTable.values().iterator();i.hasNext();){
      RoutingTableEntry theEntry = i.next();
      System.out.println(theEntry.getPeer().getPeerId() + " " + theEntry.getLastOnlineTime() + " <? " + theOldestTime);
      if(theEntry.getLastOnlineTime() < theOldestTime){
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

  public iPeerInspector getPeerInspector() {
    return myPeerInspector;
  }

  public void setPeerInspector(iPeerInspector anPeerInspector) {
    myPeerInspector = anPeerInspector;
  }

  public List< RoutingTableEntryHistory > getHistory() {
    inspectRoutingTableEntryHistory();
    return Collections.unmodifiableList( myRoutingTableEntryHistory );
  }

  public void clearHistory(){
    inspectRoutingTableEntryHistory();
    myRoutingTableEntryHistory.clear();
  }
  
  public RoutingTable copyWithoutUnreachablePeers(){
    RoutingTable theRoutingTable = new RoutingTable(myLocalPeerId);
    for(RoutingTableEntry theEntry : copyOfRoutingTable().values()){
      if(theEntry.isReachable()){
        theRoutingTable.addRoutingTableEntry( theEntry );
      }
    }
    return theRoutingTable;
  }
  
  public String toString(){
    String theS = "";
    for(RoutingTableEntry theEntry : myRoutingTable.values()){
      theS += theEntry.toString() + "\r\n";
    }
    return theS;
  }
}