/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import chabernac.protocol.Protocol;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;


public class UserInfoProtocol extends Protocol {
  public static final String ID = "UIP";
  
  private RoutingTable myRoutingTable = null;
  
  public UserInfoProtocol ( String anId ) {
    super( ID );
  }

  private Map<Peer, UserInfo> myUserInfo = new HashMap< Peer, UserInfo >();
  
  public UserInfoProtocol(){
    getRoutingTable();
  }
  
  private void getRoutingTable(){
    myRoutingTable = ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }

  @Override
  public String getDescription() {
    return "User Info Protocol";
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub

  }
  
  public Map<Peer, UserInfo> getUserInfo(){
    return Collections.unmodifiableMap( myUserInfo );
  }

}
