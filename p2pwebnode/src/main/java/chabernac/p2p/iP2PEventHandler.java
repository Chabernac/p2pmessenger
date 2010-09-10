/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p;

public interface iP2PEventHandler {

  public void handleEvent( RoutingTableEvent anRoutingTableMessage );

  public void handleEvent( MessageEvent anMessageEvent );
}
