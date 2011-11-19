/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

public interface IRoutingTableListener {
  public void routingTableEntryChanged(RoutingTableEntry anEntry);
  public void routingTableEntryRemoved(RoutingTableEntry anEntry);
}
