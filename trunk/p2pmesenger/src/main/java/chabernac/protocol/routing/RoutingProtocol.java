/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chabernac.protocol.Protocol;
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
  public static final int START_PORT = 12700;
  public static final int END_PORT = 12720;
  
  private static enum Command { REQUEST_TABLE, WHO_ARE_YOU };
  private static enum Status { UNKNOWN_COMMAND };

	private RoutingTable myRoutingTable = null;
	private long myLocalPeerId;

	public RoutingProtocol ( long aLocalPeerId, RoutingTable aRoutingTable ) {
		super( "ROU" );
		myRoutingTable = aRoutingTable;
		myLocalPeerId = aLocalPeerId;
		scanLocalSystem();
	}
	
	public void scanLocalSystem(){
	  new Thread(new ScanLocalSystem()).start();
	}

	@Override
	public String getDescription() {
		return "Routing protocol";
	}

	@Override
	protected String handleCommand( long aSessionId, String anInput ) {
	  Command theCommand = Command.valueOf( anInput );
	  if(theCommand == Command.REQUEST_TABLE){
	    return XMLTools.toXML( myRoutingTable );
	  } else if(theCommand == Command.WHO_ARE_YOU){
	    return Long.toString( myLocalPeerId );
	  }
	  return Status.UNKNOWN_COMMAND.name();
	}
	
	public RoutingTable getRoutingTable(){
	  return myRoutingTable;
	}
	
	private class ScanSystem implements Runnable{
	  private String myHost;
	  private int myPort;
	  
    public ScanSystem ( String anHost , int anPort ) {
      super();
      myHost = anHost;
      myPort = anPort;
    }

    @Override
    public void run() {
      try{
        Peer thePeer = new Peer(-1, myHost, myPort);
        String theId = thePeer.send( createMessage( Command.WHO_ARE_YOU.name() ));
        thePeer.setPeerId( Long.parseLong( theId ) );
        myRoutingTable.addRoutingTableEntry( new RoutingTableEntry(thePeer, 1, thePeer) );
      }catch(Exception e){
      }
    }
	}
	
	private class ScanLocalSystem implements Runnable{
	  public void run(){
	    ExecutorService theService = Executors.newFixedThreadPool( 20 );
	    for(int i=START_PORT;i<=END_PORT;i++){
	      theService.execute( new  ScanSystem("localhost", i));
	    }
	  }
	}
}
