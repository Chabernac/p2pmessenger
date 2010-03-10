/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

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
 *  //TODO if a peer is not reachable and no other peers on the same host are reachable
 *  a periodically port scan must happen on this host. If one peer is found the scan must stop.
 *  all the peers no the remote system will be found because of the local port scan on the other host.
 * 
 */

public class RoutingProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger( RoutingProtocol.class );

  public static final int START_PORT = 12700;
  public static final int END_PORT = 12720;

  private static enum Command { REQUEST_TABLE, WHO_ARE_YOU, ANNOUNCEMENT };
  private static enum Status { UNKNOWN_COMMAND };

  private RoutingTable myRoutingTable = null;

  private long myExchangeDelay;
  
  //this counter has just been added for unit testing reasons
  private AtomicLong myExchangeCounter = new AtomicLong(0);
  
  //this list is for test reasons to simulate peers which can not reach each other
  private List<Long> myUnreachablePeerIds = new ArrayList< Long >();
  
  private ScheduledExecutorService mySheduledService = null;

  /**
   * 
   * @param aLocalPeerId
   * @param aRoutingTable
   * @param anExchangeDelay the delay in seconds between exchaning routing tables with other peers
   */
  public RoutingProtocol ( RoutingTable aRoutingTable, long anExchangeDelay ) {
    super( "ROU" );
    myRoutingTable = aRoutingTable;
    myExchangeDelay = anExchangeDelay;
    loadRoutingTable();
    new Thread(new ScanLocalSystem()).start();
    if(anExchangeDelay > 0 ) scheduleRoutingTableExchange();
  }


  private void scheduleRoutingTableExchange(){
    mySheduledService = Executors.newScheduledThreadPool( 1 );
    mySheduledService.scheduleWithFixedDelay( new ScanLocalSystem(), 1, myExchangeDelay, TimeUnit.SECONDS);
    mySheduledService.scheduleWithFixedDelay( new ExchangeRoutingTable(), 2, myExchangeDelay, TimeUnit.SECONDS);

  }

  @Override
  public String getDescription() {
    return "Routing protocol";
  }
  
  public List< Long > getUnreachablePeerIds() {
    return myUnreachablePeerIds;
  }

  public void setUnreachablePeerIds( List< Long > anUnreachablePeerIds ) {
    myUnreachablePeerIds = anUnreachablePeerIds;
  }

  @Override
  protected String handleCommand( long aSessionId, String anInput ) {
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
      return Long.toString( myRoutingTable.getLocalPeerId() );
    } else if(theCommand == Command.ANNOUNCEMENT){
      //the announcement is of the peer which is sending the annoucement
      //so the peer id inside the routingtable entry is also the containing peer
      String thePeerEntry = anInput.substring( theFirstIndexOfSpace + 1 );
      RoutingTableEntry theEntry = (RoutingTableEntry)XMLTools.fromXML( thePeerEntry );
      theEntry.incrementHopDistance();
      myRoutingTable.addRoutingTableEntry( theEntry.getPeer().getPeerId(), theEntry);
      return XMLTools.toXML( myRoutingTable );
    }
    return Status.UNKNOWN_COMMAND.name();
  }

  public RoutingTable getRoutingTable(){
    return myRoutingTable;
  }

  private class ScanSystem implements Runnable{
    private List<String> myHosts;
    private int myPort;

    public ScanSystem ( List<String> aHosts, int anPort) {
      super();
      myHosts = aHosts;
      myPort = anPort;
    }

    @Override
    public void run() {
      try{
        Peer thePeer = new Peer(-1, myHosts, myPort);
        String theId = thePeer.send( createMessage( Command.WHO_ARE_YOU.name() ));
        long thePeerdId = Long.parseLong( theId );
        if(!myUnreachablePeerIds.contains( thePeerdId )){
          thePeer.setPeerId( thePeerdId );
          RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer);
          
          //only if we have detected our self we set the hop distance to 0
          if(thePeerdId == myRoutingTable.getLocalPeerId()){
            theEntry.setHopDistance( 0 );
          }
          myRoutingTable.addRoutingTableEntry( myRoutingTable.getLocalPeerId(), theEntry );
        }
      }catch(Exception e){
      }
    }
  }
  
  public void scanLocalSystem(){
    try{
      List<String> theLocalHosts = NetTools.getLocalExposedIpAddresses();
      ExecutorService theService = Executors.newFixedThreadPool( 20 );
      for(int i=START_PORT;i<=END_PORT;i++){
        theService.execute( new  ScanSystem(theLocalHosts, i));
      }
    }catch(SocketException e){
      LOGGER.error( "Could not get local ip addressed", e );
    }  
  }
  
  private class ScanLocalSystem implements Runnable{
    public void run(){
      scanLocalSystem();
    }
  }

  /**
   * this method will send a request to all the peers in the routing table
   */
  public void exchangeRoutingTable(){
    LOGGER.debug("Exchanging routing table for peer: " + myRoutingTable.getLocalPeerId());
    
    for(RoutingTableEntry theEntry : myRoutingTable.getEntries()){
      Peer thePeer = theEntry.getPeer();
      if(myUnreachablePeerIds.contains( thePeer.getPeerId())){
        //simulate an unreachable peer, set the responding indicator to false
        theEntry.setHopDistance(  RoutingTableEntry.MAX_HOP_DISTANCE );
      } else if(thePeer.getPeerId() != myRoutingTable.getLocalPeerId()){
        try {
          String theTable = thePeer.send( createMessage( Command.ANNOUNCEMENT.name() + " "  + XMLTools.toXML( myRoutingTable.getEntryForLocalPeer() ))) ;
//          String theTable = thePeer.send( createMessage( Command.REQUEST_TABLE.name() ));
          RoutingTable theRemoteTable = (RoutingTable)XMLTools.fromXML( theTable );
          myRoutingTable.merge( theRemoteTable );
          //we can connect directly to this peer, so the hop distance is 1
          theEntry.setHopDistance( 1 );
        } catch ( Exception e ) {
          //update all peers which have this peer as gateway to the max hop distance
          for(RoutingTableEntry theEntry2 : myRoutingTable.getEntries()){
            if(theEntry2.getGateway().getPeerId() == theEntry.getPeer().getPeerId()){
              theEntry2.setHopDistance( RoutingTableEntry.MAX_HOP_DISTANCE );
            }
          }
        }
      }
    }
    myExchangeCounter.incrementAndGet();
    LOGGER.debug("End exchanging routing table for peer: " + myRoutingTable.getLocalPeerId());
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
  
  private void loadRoutingTable(){
    
  }
  
  private void saveRoutingTable(){
    
  }

  @Override
  protected void stopProtocol() {
    if(mySheduledService != null){
      mySheduledService.shutdown();
    }
    
    saveRoutingTable();
  }
}
