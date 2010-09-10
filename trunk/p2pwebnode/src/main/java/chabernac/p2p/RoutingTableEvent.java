/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p;

import chabernac.protocol.routing.RoutingTable;

public class RoutingTableEvent extends P2PEvent {
  private final RoutingTable myRoutingTable;
  
  public RoutingTableEvent ( RoutingTable anRoutingTable ) {
    super();
    myRoutingTable = anRoutingTable;
  }

  @Override
  public void handle( iP2PEventHandler aHandler ) {
    aHandler.handleEvent(this);
  }

  public RoutingTable getRoutingTable() {
    return myRoutingTable;
  }
}
