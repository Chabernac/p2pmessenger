/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

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
  private static enum Command { REQUEST_TABLE, WHO_ARE_YOU };
  private static enum Status { UNKNOWN_COMMAND };

	private RoutingTable myRoutingTable = null;
	private long myLocalPeerId;

	public RoutingProtocol ( long aLocalPeerId, RoutingTable aRoutingTable ) {
		super( "ROU" );
		myRoutingTable = aRoutingTable;
		myLocalPeerId = aLocalPeerId;
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

}
