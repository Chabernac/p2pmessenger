/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.routing;

import java.util.HashMap;
import java.util.Map;

public class RoutingTable {
  private Map<Peer, RoutingTableEntry> myRoutingTable = new HashMap< Peer, RoutingTableEntry >();
  
  public void addRoutingTableEntry(RoutingTableEntry anEntry){
    if(myRoutingTable.containsKey( anEntry.getPeer() )){
      RoutingTableEntry thePeerEntry = myRoutingTable.get( anEntry.getPeer() );
      if(anEntry.closerThen( thePeerEntry )){
        myRoutingTable.put( anEntry.getPeer(), anEntry );
      }
    }
  }
}
