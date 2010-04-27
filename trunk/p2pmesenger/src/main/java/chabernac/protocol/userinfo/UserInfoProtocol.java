/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
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

  public static enum Command { GET, PUT };
  public static enum Response{ OK };

  private Map<String, UserInfo> myUserInfo = new HashMap< String, UserInfo >();

  private ExecutorService myRetrievalService = Executors.newFixedThreadPool( 5 );

  private iUserInfoProvider myUserInfoProvider = null;

  public UserInfoProtocol ( iUserInfoProvider aProvider ) throws UserInfoException{
    super( ID );
    if(aProvider == null) throw new UserInfoException("Must give a user info provider");
    myUserInfoProvider = aProvider;
    addUserInfoListener();
  }

  private void addUserInfoListener(){
    myUserInfoProvider.getUserInfo().addObserver( new MyUserInfoListener() );
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
          myRetrievalService.execute( new UserInfoRetriever(thePeer.getPeerId()) );
        }
      }
    }catch(Exception e){
      LOGGER.error( "Could not retrieve user info", e );
    }
  }

  public void announceMe(){
    try{
      RoutingTable theTable = getRoutingTable();
      for(RoutingTableEntry theEntry : theTable){
        Peer thePeer = theEntry.getPeer();
        myRetrievalService.execute( new UserInfoSender(thePeer.getPeerId()) );
      }
    }catch(Exception e){
      LOGGER.error("Could not announce my user info to peer", e);
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
    if(Command.PUT.name().equalsIgnoreCase( anInput )){
      String[] theParts = anInput.split( " " );
      String thePeerId = theParts[1];
      UserInfo theUserInfo = (UserInfo)XMLTools.fromXML( theParts[2]);
      myUserInfo.put( thePeerId, theUserInfo );
      return Response.OK.name();
    }

    return ProtocolContainer.Response.UNKNOWN_COMMAND.name();
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub
  }

  public UserInfo getUserInfoForPeer(String aPeerId) throws UserInfoException{
    try{
      Message theMessage = new Message(  );
      theMessage.setDestination( getRoutingTable().getEntryForPeer( aPeerId ).getPeer());
      theMessage.setSource( getRoutingTable().obtainLocalPeer() );
      theMessage.setMessage( createMessage( Command.GET.name() ) );
      theMessage.setProtocolMessage( true );
      String theResult = ((MessageProtocol)findProtocolContainer().getProtocol( MessageProtocol.ID )).handleMessage( 0, theMessage );
      return (UserInfo)XMLTools.fromXML( theResult );
    }catch(Exception e){
      throw new UserInfoException("Could not retrieve user info for peer '" + aPeerId + "'", e);
    }
  }

  public void sendUserInfoToPeer(String aPeerId) throws UserInfoException{
    try{
      Message theMessage = new Message(  );
      theMessage.setDestination( getRoutingTable().getEntryForPeer( aPeerId ).getPeer() );
      theMessage.setSource( getRoutingTable().obtainLocalPeer() );
      theMessage.setMessage( createMessage( Command.PUT.name() + " " + getRoutingTable().getLocalPeerId() + " " + myUserInfoProvider.getUserInfo()));
      theMessage.setProtocolMessage( true );
      String theResult = ((MessageProtocol)findProtocolContainer().getProtocol( MessageProtocol.ID )).handleMessage( 0, theMessage );
      if(!theResult.equals( Response.OK )){
        throw new UserInfoException("Could not send user info to peer '" + aPeerId + "'");
      }
    }catch(Exception e){
      throw new UserInfoException("Could not send user info to peer '" + aPeerId + "'", e);
    }
  }

  public Map<String, UserInfo> getUserInfo(){
    return Collections.unmodifiableMap( myUserInfo );
  }

  public UserInfo getPersonalInfo() {
    return myUserInfoProvider.getUserInfo();
  }

  private class UserInfoRetriever implements Runnable{
    private String myPeerId;

    public UserInfoRetriever(String aPeerId){
      myPeerId = aPeerId;
    }

    @Override
    public void run() {
      try{
        UserInfo theUserInfo = getUserInfoForPeer( myPeerId );
        myUserInfo.put(myPeerId, theUserInfo);
      }catch(Exception e){
        LOGGER.error("Could not retrieve user info", e);
      }
    }
  }

  private class UserInfoSender implements Runnable{
    private String myPeerId;

    public UserInfoSender(String aPeerId){
      myPeerId = aPeerId;
    }

    @Override
    public void run() {
      try{
        sendUserInfoToPeer( myPeerId );
      }catch(Exception e){
        LOGGER.error("Could not retrieve user info", e);
      }
    }
  }

  private class RoutingTableListener implements IRoutingTableListener{
    @Override
    public void routingTableEntryChanged( RoutingTableEntry anEntry ) {
      if(!myUserInfo.containsKey( anEntry.getPeer() )){
        myRetrievalService.execute( new UserInfoRetriever(anEntry.getPeer().getPeerId()) );
      }
    }
  }

  public class MyUserInfoListener implements Observer {

    @Override
    public void update( Observable anO, Object anArg ) {

    }
  }
}