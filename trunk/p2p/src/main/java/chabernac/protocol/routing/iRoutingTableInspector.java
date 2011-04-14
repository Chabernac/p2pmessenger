/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

public interface iRoutingTableInspector {
  public RoutingTable inspectRoutingTable(String aSessionId, RoutingTable aRoutingTable);
}
