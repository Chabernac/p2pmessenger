/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.SocketException;
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
 */

public class RoutingProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger( RoutingProtocol.class );

  public static final int START_PORT = 12700;
  public static final int END_PORT = 12720;

  private static enum Command { REQUEST_TABLE, WHO_ARE_YOU };
  private static enum Status { UNKNOWN_COMMAND };

  private RoutingTable myRoutingTable = null;
  private long myLocalPeerId;
  
  private long myExchangeDelay;
  
  //this counter has just been added for unit testing reasons
  private AtomicLong myExchangeCounter = new AtomicLong(0);

  /**
   * 
   * @param aLocalPeerId
   * @param aRoutingTable
   * @param anExchangeDelay the delay in seconds between exchaning routing tables with other peers
   */
  public RoutingProtocol ( long aLocalPeerId, RoutingTable aRoutingTable, long anExchangeDelay ) {
    super( "ROU" );
    myRoutingTable = aRoutingTable;
    myLocalPeerId = aLocalPeerId;
    myExchangeDelay = anExchangeDelay;
    scanLocalSystem();
    scheduleRoutingTableExchange();
  }

  public void scanLocalSystem(){
    new Thread(new ScanLocalSystem()).start();
  }

  private void scheduleRoutingTableExchange(){
    ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );
    theService.scheduleWithFixedDelay( new ExchangeRoutingTable(), 2, myExchangeDelay, TimeUnit.SECONDS);

  }

  @Override
  public String getDescription() {
    return "Routing protocol";
  }

  @Override
  protected String handleCommand( long aSessionId, String anInput ) {
    Command theCommand = Command.valueOf( anInput );
    if(theCommand == Command.REQUEST_TABLE){
      //another peer has send a request for the routing protocol send it
      return XMLTools.toXML( myRoutingTable );
    } else if(theCommand == Command.WHO_ARE_YOU){
      //another peer requested my peer id, send it to him, this is also used
      //to check if I'm still alive and kicking
      return Long.toString( myLocalPeerId );
    }
    return Status.UNKNOWN_COMMAND.name();
  }

  public RoutingTable getRoutingTable(){
    return myRoutingTable;
  }

  private class ScanSystem implements Runnable{
    private List<String> myHosts;
    private int myPort;

    public ScanSystem ( List<String> aHosts, int anPort ) {
      super();
      myHosts = aHosts;
      myPort = anPort;
    }

    @Override
    public void run() {
      try{
        Peer thePeer = new Peer(-1, myHosts, myPort);
        String theId = thePeer.send( createMessage( Command.WHO_ARE_YOU.name() ));
        thePeer.setPeerId( Long.parseLong( theId ) );
        RoutingTableEntry theEntry = new RoutingTableEntry(thePeer, 1, thePeer);
        theEntry.setResponding( true );
        myRoutingTable.addRoutingTableEntry( theEntry );
      }catch(Exception e){
      }
    }
  }

  private class ScanLocalSystem implements Runnable{
    public void run(){
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
  }

  /**
   * this method will send a request to all the peers in the routing table
   */
  public void exchangeRoutingTable(){
    LOGGER.debug("Exchanging routing table for peer: " + myLocalPeerId);
    for(RoutingTableEntry theEntry : myRoutingTable.getEntries()){
      Peer thePeer = theEntry.getPeer();
      if(thePeer.getPeerId() != myLocalPeerId){
        try {
          String theTable = thePeer.send( createMessage( Command.REQUEST_TABLE.name() ) );
          RoutingTable theRemoteTable = (RoutingTable)XMLTools.fromXML( theTable );
          myRoutingTable.merge( theRemoteTable );
          theEntry.setResponding( true );
        } catch ( Exception e ) {
          //we cannot reach this peer, set it to non responding
          theEntry.setResponding( false );
        }
      }
    }
    myExchangeCounter.incrementAndGet();
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
}
