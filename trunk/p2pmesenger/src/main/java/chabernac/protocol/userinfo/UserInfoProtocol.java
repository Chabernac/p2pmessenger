/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import chabernac.protocol.IProtocol;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.routing.IRoutingTableListener;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.tools.XMLTools;

public class UserInfoProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger(UserInfoProtocol.class);
  
  public static final String ID = "UIP";
  
  public static enum Command { GET };
  
  private Map<Peer, UserInfo> myUserInfo = new HashMap< Peer, UserInfo >();
  
  private ExecutorService myRetrievalService = Executors.newFixedThreadPool( 5 );
  
  private iUserInfoProvider myUserInfoProvider = null;
  
  public UserInfoProtocol ( iUserInfoProvider aProvider ) throws UserInfoException{
    super( ID );
    if(aProvider == null) throw new UserInfoException("Must give a user info provider");
    myUserInfoProvider = aProvider;
  }
  
  public void setMasterProtocol( IProtocol aProtocol ) {
    super.setMasterProtocol( aProtocol );
    
    try{
      fullRetrieval();
      addListeners();
    }catch(Exception e){
      LOGGER.error( "Could not fully initialize UserInfoProtocol", e );
    }
  }
  
  private void fullRetrieval() {
    try{
      RoutingTable theTable = getRoutingTable();
      for(RoutingTableEntry theEntry : theTable){
        Peer thePeer = theEntry.getPeer();
        if(!myUserInfo.containsKey( thePeer )){
          myRetrievalService.execute( new UserInfoRetriever(thePeer) );
        }
      }
    }catch(Exception e){
      LOGGER.error( "Could not retrieve user info", e );
    }
  }
  
  private void addListeners() throws ProtocolException{
    getRoutingTable().addRoutingTableListener( new RoutingTableListener() );
  }

  private RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }

  @Override
  public String getDescription() {
    return "User Info Protocol";
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    if(Command.GET.name().equalsIgnoreCase( anInput )){
      return XMLTools.toXML( getPersonalInfo() );
    }
    
    return ProtocolContainer.Response.UNKNOWN_COMMAND.name();
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub
  }
  
  public UserInfo getUserInfoForPeer(Peer aPeer) throws UserInfoException{
    try{
      Message theMessage = new Message(  );
      theMessage.setDestination( aPeer );
      theMessage.setSource( getRoutingTable().obtainLocalPeer() );
      theMessage.setMessage( createMessage( Command.GET.name() ) );
      String theResult = ((MessageProtocol)findProtocolContainer().getProtocol( MessageProtocol.ID )).handleMessage( 0, theMessage );
      return (UserInfo)XMLTools.fromXML( theResult );
    }catch(Exception e){
      throw new UserInfoException("Could not retrieve user info for peer '" + aPeer.getPeerId() + "'", e);
    }
  }
  
  public Map<Peer, UserInfo> getUserInfo(){
    return Collections.unmodifiableMap( myUserInfo );
  }

  public UserInfo getPersonalInfo() {
    return myUserInfoProvider.getUserInfo();
  }
  
  private class UserInfoRetriever implements Runnable{
    private Peer myPeer = null;
    
    public UserInfoRetriever(Peer aPeer){
      myPeer = aPeer;
    }

    @Override
    public void run() {
      try{
      UserInfo theUserInfo = getUserInfoForPeer( myPeer );
      myUserInfo.put(myPeer, theUserInfo);
      }catch(Exception e){
        LOGGER.error("Could not retrieve user info", e);
      }
    }
  }
  
  private class RoutingTableListener implements IRoutingTableListener{
    @Override
    public void routingTableEntryChanged( RoutingTableEntry anEntry ) {
      if(!myUserInfo.containsKey( anEntry.getPeer() )){
        myRetrievalService.execute( new UserInfoRetriever(anEntry.getPeer()) );
      }
    }
  }
}