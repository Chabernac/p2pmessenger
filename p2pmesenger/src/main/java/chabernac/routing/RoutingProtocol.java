/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.routing;

import chabernac.protocol.Protocol;

public class RoutingProtocol extends Protocol {

	private RoutingTable myRoutingTable = new RoutingTable();

	public RoutingProtocol (  ) {
		super( "ROU" );
	}

	@Override
	public String getDescription() {
		return "Routing protocol";
	}

	@Override
	protected String handleCommand( long aSessionId, String anInput ) {
		// TODO Auto-generated method stub
		return null;
	}

}
