/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import chabernac.io.persist.iObjectPersister;
import chabernac.protocol.Protocol;
import chabernac.tools.NetTools;
import chabernac.tools.XMLTools;

/**
 *  the routing protocol will do the following
 *  
 *  - send it's own routing table to all known peers in the routing table
 *  - update the routing table with the items received from another peer.  in this process the fasted path to a peer must be stored in the routing table
 *  - periodically contact all peers to see if they are still online and retrieve the routing table of the other peer.
 *  
 *  TODO changes should be promoted through the network immediatly
 */

public class RoutingProtocol extends Protocol {
  public static String ID = "ROU";

  private static Logger LOGGER = Logger.getLogger( RoutingProtocol.class );

  public static final int START_PORT = 12700;
  public static final int END_PORT = 12720;

  private static enum Command { REQUEST_TABLE, WHO_ARE_YOU, ANNOUNCEMENT_WITH_REPLY, ANNOUNCEMENT };
  private static enum Status { UNKNOWN_COMMAND };

  private RoutingTable myRoutingTable = null;

  private long myExchangeDelay;

  //this counter has just been added for unit testing reasons
  private AtomicLong myExchangeCounter = new AtomicLong(0);

  //this list is for test reasons to simulate peers which can not reach each other locally 
  private List<String> myLocalUnreachablePeers = new ArrayList< String >();

  //this list is for test reasons to simulate peers which can not reach each other remotely
  private List<String> myRemoteUnreachablePeers = new ArrayList< String >();

  private ScheduledExecutorService mySheduledService = null;

  private iObjectPersister< RoutingTable > myObjectPersister = new RoutingTablePersister();

  private boolean isPersistRoutingTable = false;

  private ExecutorService myChangeService = null;

  private String myLocalPeerId = null;

  public RoutingProtocol ( long anExchangeDelay, boolean isPersistRoutingTable) {
    this(null, anExchangeDelay, isPersistRoutingTable);
  }

  /**
   * 
   * @param aLocalPeerId
   * @param aRoutingTable
   * @param anExchangeDelay the delay in seconds between exchaning routing tables with other peers
   */
  public RoutingProtocol ( String aLocalPeerId, long anExchangeDelay, boolean isPersistRoutingTable) {
    super( ID );
    myLocalPeerId = aLocalPeerId;
    myExchangeDelay = anExchangeDelay;
    this.isPersistRoutingTable = isPersistRoutingTable;
    loadRoutingTable();
    resetRoutingTable();
//    new Thread(new ScanLocalSystem()).start();
    if(anExchangeDelay > 0 ) scheduleRoutingTableExchange();
    myChangeService = Executors.newFixedThreadPool( 5 );

    //TODO enable to test immediate change propagation
//    myRoutingTable.addRoutingTableListener( new RoutingTableListener() );
  }


  private void scheduleRoutingTableExchange(){
    mySheduledService = Executors.newScheduledThreadPool( 1 );
    mySheduledService.scheduleWithFixedDelay( new ScanLocalSystem(), 1, myExchangeDelay, TimeUnit.SECONDS);
    mySheduledService.scheduleWithFixedDelay( new ExchangeRoutingTable(), 2, myExchangeDelay, TimeUnit.SECONDS);
    mySheduledService.scheduleWithFixedDelay( new ScanRemoteSystem(), 10 , 10 * myExchangeDelay, TimeUnit.SECONDS);
  }

  @Override
  public String getDescription() {
    return "Routing protocol";
  }

  public List< String > getLocalUnreachablePeerIds() {
    return myLocalUnreachablePeers;
  }

  public List< String > getRemoteUnreachablePeerIds() {
    return myRemoteUnreachablePeers;
  }

  public String getLocalPeerId(){
    if(myLocalPeerId != null && !"".equals( myLocalPeerId )){
      return myLocalPeerId;
    } else if(myRoutingTable != null){
      return myRoutingTable.getLocalPeerId();
    } else {
      myLocalPeerId = UUID.randomUUID().toString();
      return myLocalPeerId;
    }
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    int theFirstIndexOfSpace = anInput.indexOf( " " );
    if(theFirstIndexOfSpace == -1) theFirstIndexOfSpace = anInput.length();
    String theCommandString = anInput.substring( 0,  theFirstIndexOfSpace);

    Command theCommand = Command.valueOf( theCommandString );
    if(theCommand == Command.REQUEST_TABLE){
      //another peer has send a request for the routing protocol send it
      return XMLTools.toXML( myRoutingTable );
    } else if(theCommand == Command.WHO_ARE_YOU){
      //another peer requested my peer id, send it to him, this is also used
      //to check if I'm still alive and kicking
      return myRoutingTable.getLocalPeerId();
    } else if(theCommand == Command.ANNOUNCEMENT_WITH_REPLY){
      //the announcement is of the peer which is sending the annoucement
      //so the peer id inside the routingtable entry is also the containing peer
      String thePeerEntry = anInput.substring( theFirstIndexOfSpace + 1 );
      RoutingTableEntry theEntry = ((RoutingTableEntry)XMLTools.fromXML( thePeerEntry )).incHopDistance();
      myRoutingTable.addRoutingTableEntry( theEntry.getPeer().getPeerId(), theEntry);
      return XMLTools.toXML( myRoutingTable );
    } else if(theCommand == Command.ANNOUNCEMENT){
      String thePeerEntry = anInput.substring( theFirstIndexOfSpace + 1 );
      RoutingTableEntry theEntry = ((RoutingTableEntry)XMLTools.fromXML( thePeerEntry )).incHopDistance();
      myRoutingTable.addRoutingTableEntry( theEntry.getPeer().getPeerId(), theEntry);
    }
    return Status.UNKNOWN_COMMAND.name();
  }

  public RoutingTable getRoutingTable(){
    return myRoutingTable;
  }

  private class ScanSystem implements Runnable{
    private List<String> myHosts;
    private int myPort;
    private List<String> myUnreachablePeers = null;

    public ScanSystem ( List<String> aHosts, int anPort, List<String> anUnreachablePeers) {
      super();
      myHosts = aHosts;
      myPort = anPort;
      myUnreachablePeers = anUnreachablePeers;
    }

    @Override
    public void run() {
      Peer thePeer = new Peer(null, myHosts, myPort);
      contactPeer( thePeer, myUnreachablePeers );
    }
  }

  private boolean contactPeer(Peer aPeer, List<String> anUnreachablePeers){
    try{
      String theId = aPeer.send( createMessage( Command.WHO_ARE_YOU.name() ));

      if(!anUnreachablePeers.contains( theId )){
        aPeer.setPeerId( theId );
        RoutingTableEntry theEntry = new RoutingTableEntry(aPeer, 1, aPeer);

        //only if we have detected our self we set the hop distance to 0
        if(theId.equals(myRoutingTable.getLocalPeerId())){
          theEntry = theEntry.derivedEntry( 0 );
        }
        myRoutingTable.addRoutingTableEntry( myRoutingTable.getLocalPeerId(), theEntry );
        return true;
      }
    }catch(Exception e){
    }
    return false;
  }

  public void scanLocalSystem(){
    try{
      LOGGER.debug( "Scanning local system" );
      List<String> theLocalHosts = NetTools.getLocalExposedIpAddresses();
      ExecutorService theService = Executors.newFixedThreadPool( 20 );
      for(int i=START_PORT;i<=END_PORT;i++){
        theService.execute( new  ScanSystem(theLocalHosts, i, myLocalUnreachablePeers));
      }
    }catch(SocketException e){
      LOGGER.error( "Could not get local ip addressed", e );
    }  
  }

  /**
   * this method will scan the routing table and find hosts which are unreachable
   * for this hosts a port scan will be started to detect if the peer is not online
   * on a different port, if one is found, the port scan stops
   */
  public void scanRemoteSystem(boolean isExcludeLocal){
    //first search all hosts which have no single peer
    Map<String, Boolean> theHosts = new HashMap< String, Boolean >();

    for(RoutingTableEntry theEntry : myRoutingTable){
      if(isExcludeLocal && !theEntry.getPeer().getPeerId().equals(myRoutingTable.getLocalPeerId())){
        for(String theHost : theEntry.getPeer().getHosts()){
          boolean isReachable = false;
          if(theHosts.containsKey( theHost )){
            isReachable = theHosts.get(theHost);
          }
          theHosts.put( theHost, isReachable | theEntry.isReachable() );
        }
      }
    }

    //now try to scan all hosts which are not reachable
    for(String theHost : theHosts.keySet()){
      if(!theHosts.get(theHost)){
        //this host is not reachable, scan it
        boolean isContacted = false;
        for(int i=START_PORT;i<=END_PORT && !isContacted;i++){
          try{
            if(!isExcludeLocal || i!=myRoutingTable.obtainLocalPeer().getPort()){
              LOGGER.debug("Scanning the following host: '" + theHost + "' on port '" + i + "'");
              isContacted = contactPeer( new Peer("", theHost, i), myRemoteUnreachablePeers );
            }
          }catch(Exception e){}
        }
      }
    }
  }

  private class ScanLocalSystem implements Runnable{
    public void run(){
      scanLocalSystem();
    }
  }

  private class ScanRemoteSystem implements Runnable{
    public void run(){
      scanRemoteSystem(false);
    }
  }

  /**
   * this method will send a request to all the peers in the routing table
   */
  public void exchangeRoutingTable(){
    LOGGER.debug("Exchanging routing table for peer: " + myRoutingTable.getLocalPeerId());

    for(RoutingTableEntry theEntry : myRoutingTable.getEntries()){
      Peer thePeer = theEntry.getPeer();

      //simulate that we can not contact this peer directly, but we can contact it trough a gateway
      //so check if hop distance = 1


      if(!thePeer.getPeerId().equals(myRoutingTable.getLocalPeerId())){
        try {
          if(myLocalUnreachablePeers.contains( thePeer.getPeerId())){
            //simulate that we cannot contact the peer
            throw new Exception("Simulate that we can not contact peer: " + thePeer.getPeerId());
          }
          String theTable = thePeer.send( createMessage( Command.ANNOUNCEMENT_WITH_REPLY.name() + " "  + XMLTools.toXML( myRoutingTable.getEntryForLocalPeer() ))) ;
//          String theTable = thePeer.send( createMessage( Command.REQUEST_TABLE.name() ));
          RoutingTable theRemoteTable = (RoutingTable)XMLTools.fromXML( theTable );
          myRoutingTable.merge( theRemoteTable );
          //we can connect directly to this peer, so the hop distance is 1
          //theEntry.setHopDistance( 1 );
          myRoutingTable.addRoutingTableEntry( theEntry.derivedEntry( 1 ) );
        } catch ( Exception e ) {
          //update all peers which have this peer as gateway to the max hop distance
          for(RoutingTableEntry theEntry2 : myRoutingTable.getEntries()){
            if(theEntry2.getGateway().getPeerId().equals( theEntry.getPeer().getPeerId())){
//              theEntry2.setHopDistance( RoutingTableEntry.MAX_HOP_DISTANCE );
              myRoutingTable.addRoutingTableEntry( theEntry2.derivedEntry( RoutingTableEntry.MAX_HOP_DISTANCE ) );
            }
          }
        }
      }
    }
    myExchangeCounter.incrementAndGet();
    LOGGER.debug("End exchanging routing table for peer: " + myRoutingTable.getLocalPeerId());

    //save the routing table
    if(isPersistRoutingTable) saveRoutingTable();
  }

  private void sendAnnoucement( RoutingTableEntry anEntry ) {
    for(RoutingTableEntry theEntry : myRoutingTable.getEntries()){
      Peer thePeer = theEntry.getPeer();
      if(thePeer.getPeerId() != myRoutingTable.getLocalPeerId() && theEntry.getHopDistance() <= 1){
        try {
          thePeer.send( createMessage( Command.ANNOUNCEMENT.name() + " "  + XMLTools.toXML( anEntry ))) ;
        } catch ( Exception e ) {
          //the peer can not be reached 
          //update all peers which have this peer as gateway to the max hop distance
          for(RoutingTableEntry theEntry2 : myRoutingTable.getEntries()){
            if(theEntry2.getGateway().getPeerId().equals( theEntry.getPeer().getPeerId())){
//              theEntry2.setHopDistance( RoutingTableEntry.MAX_HOP_DISTANCE );
              myRoutingTable.addRoutingTableEntry( theEntry2.derivedEntry( RoutingTableEntry.MAX_HOP_DISTANCE ) );
            }
          }
        }
      }
    }

  }

  public long getExchangeCounter(){
    return myExchangeCounter.longValue();
  }

  private class ExchangeRoutingTable implements Runnable{

    @Override
    public void run() {
      exchangeRoutingTable();
    }
  }

  private File getRoutingTableLocation(){
    String theFile = "RoutingTable";
    if(myLocalPeerId != null && !"".equals( myLocalPeerId )){
      theFile += "_" + myLocalPeerId;
    }
    theFile += ".csv";
    return new File(theFile);
  }

  private void loadRoutingTable(){
    if(isPersistRoutingTable && getRoutingTableLocation().exists()){
      File theFile = getRoutingTableLocation();
      try{
        FileInputStream theInputStream = new FileInputStream(theFile);
        myRoutingTable = myObjectPersister.loadObject( theInputStream );
        theInputStream.close();
      }catch(Exception e){
        LOGGER.error( "Could not load routing table", e );
      }
    } else {
      myRoutingTable = new RoutingTable(getLocalPeerId());
    }
  }

  public void resetRoutingTable(){
    for(RoutingTableEntry theEntry : myRoutingTable.getEntries()){
      myRoutingTable.addRoutingTableEntry( theEntry.derivedEntry( RoutingTableEntry.MAX_HOP_DISTANCE ) );
//      theEntry.setHopDistance( RoutingTableEntry.MAX_HOP_DISTANCE );
    }
  }

  private void saveRoutingTable(){
    File theFile = getRoutingTableLocation();
    try{
      FileOutputStream theStream = new FileOutputStream(theFile);
      myObjectPersister.persistObject( myRoutingTable, theStream );
      theStream.flush();
      theStream.close();
    }catch(Exception e){
      LOGGER.error("Unable to save routing table", e);
    }
  }

  @Override
  public void stop() {
    if(mySheduledService != null){
      mySheduledService.shutdown();
    }

    if(isPersistRoutingTable) saveRoutingTable();
  }

  private class RoutingTableListener implements IRoutingTableListener{
    @Override
    public void routingTableEntryChanged( RoutingTableEntry anEntry ) {
      myChangeService.execute( new SendAnnouncement(anEntry) );
    }
  }

  private class SendAnnouncement implements Runnable{
    private RoutingTableEntry myEntry = null;

    public SendAnnouncement(RoutingTableEntry anEntry){
      myEntry = anEntry;
    }

    public void run(){
      sendAnnoucement(myEntry); 
    }
  }
}
