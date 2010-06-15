/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
  public static enum Response{ OK, NOK };

  private Map<String, UserInfo> myUserInfo = new HashMap< String, UserInfo >();

  private ExecutorService myRetrievalService = Executors.newFixedThreadPool( 5 );

  private iUserInfoProvider myUserInfoProvider = null;

  private List< iUserInfoListener > myListeners = new ArrayList< iUserInfoListener >();

  private MyUserInfoListener myUserInfoListener = new MyUserInfoListener();

  public UserInfoProtocol ( iUserInfoProvider aProvider ) throws UserInfoException{
    super( ID );
    if(aProvider == null) throw new UserInfoException("Must give a user info provider");
    myUserInfoProvider = aProvider;
    addUserInfoListener();
  }

  private void addUserInfoListener() throws UserInfoException{
    myUserInfoProvider.getUserInfo().addObserver( myUserInfoListener );
  }

  public void setUserInfoProvider(iUserInfoProvider aUserInfoProvider){
    try {
      if(myUserInfoProvider != null){
        myUserInfoProvider.getUserInfo().deleteObserver( myUserInfoListener );
      }
      myUserInfoProvider = aUserInfoProvider;
      addUserInfoListener();
      announceMe();
    } catch ( UserInfoException e ) {
      LOGGER.error("Unable to retrieve user info", e);
    }
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
      try {
        return XMLTools.toXML( getPersonalInfo() );
      } catch ( UserInfoException e ) {
        LOGGER.error( "Could not retrieve personal user info", e );
        return Response.NOK.name();
      }
    }
    if(anInput.startsWith( Command.PUT.name() ) ){
      String[] theParts = anInput.split( ";" );
      String thePeerId = theParts[1];
      UserInfo theUserInfo = (UserInfo)XMLTools.fromXML( theParts[2]);
      myUserInfo.put( thePeerId, theUserInfo );
      notifyUserInfoChanged( theUserInfo );
      return Response.OK.name();
    }

    return ProtocolContainer.Response.UNKNOWN_COMMAND.name();
  }

  private void notifyUserInfoChanged(UserInfo aUserInfo){
    for(iUserInfoListener theListener : myListeners){
      theListener.userInfoChanged( aUserInfo, Collections.unmodifiableMap( myUserInfo ));
    }
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub
  }

  public UserInfo getUserInfoForPeer(String aPeerId) throws UserInfoException{
    try{
      Message theMessage = new Message(  );
      theMessage.setDestination( getRoutingTable().getEntryForPeer( aPeerId ).getPeer());
      theMessage.setSource( getRoutingTable().getEntryForLocalPeer().getPeer() );
      theMessage.setMessage( createMessage( Command.GET.name() ) );
      theMessage.setProtocolMessage( true );
      String theResult = ((MessageProtocol)findProtocolContainer().getProtocol( MessageProtocol.ID )).sendMessage( theMessage );
      return (UserInfo)XMLTools.fromXML( theResult );
    }catch(Exception e){
      throw new UserInfoException("Could not retrieve user info for peer '" + aPeerId + "'", e);
    }
  }

  public void sendUserInfoToPeer(String aPeerId) throws UserInfoException{
    try{
      Message theMessage = new Message(  );
      theMessage.setDestination( getRoutingTable().getEntryForPeer( aPeerId ).getPeer() );
      theMessage.setSource( getRoutingTable().getEntryForLocalPeer().getPeer() );
      theMessage.setMessage( createMessage( Command.PUT.name() + ";" + getRoutingTable().getLocalPeerId() + ";" + XMLTools.toXML( myUserInfoProvider.getUserInfo())));
      theMessage.setProtocolMessage( true );
      String theResult = ((MessageProtocol)findProtocolContainer().getProtocol( MessageProtocol.ID )).sendMessage( theMessage );
      if(!theResult.equals( Response.OK.name() )){
        throw new UserInfoException("Could not send user info to peer '" + aPeerId + "' response: '" + theResult + "'");
      }
    }catch(Exception e){
      throw new UserInfoException("Could not send user info to peer '" + aPeerId + "'", e);
    }
  }

  public Map<String, UserInfo> getUserInfo(){
    return Collections.unmodifiableMap( myUserInfo );
  }

  public UserInfo getPersonalInfo() throws UserInfoException{
    return myUserInfoProvider.getUserInfo();
  }

  public void addUserInfoListener(iUserInfoListener aListener){
    myListeners.add(aListener);
  }

  public void removeUserInfoListener(iUserInfoListener aListener){
    myListeners.remove( aListener );
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
        notifyUserInfoChanged( theUserInfo );
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
      announceMe();
    }
  }
}