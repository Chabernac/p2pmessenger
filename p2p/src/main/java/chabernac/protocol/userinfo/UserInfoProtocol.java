/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.IProtocol;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.IRoutingTableListener;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.userinfo.UserInfo.Status;

public class UserInfoProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger(UserInfoProtocol.class);

  public static final String ID = "UIP";

  public static enum Command { GET, PUT };
  public static enum Response{ OK, NOK };

  private Map<String, UserInfo> myUserInfo = Collections.synchronizedMap( new HashMap< String, UserInfo >());

  private ExecutorService myRetrievalService = Executors.newFixedThreadPool( 5 );
  private ScheduledExecutorService myService = Executors.newScheduledThreadPool( 1 );

  private iUserInfoProvider myUserInfoProvider = null;

  private List< iUserInfoListener > myListeners = new ArrayList< iUserInfoListener >();

  private MyUserInfoListener myUserInfoListener = new MyUserInfoListener();

  private final UserInfo myPersonalUserInfo = new UserInfo();

  private iObjectStringConverter< UserInfo > myConverter = new Base64ObjectStringConverter< UserInfo >();


  public UserInfoProtocol ( iUserInfoProvider aProvider ) throws UserInfoException{
    super( ID );
    if(aProvider == null) throw new UserInfoException("Must give a user info provider");
    myUserInfoProvider = aProvider;
    obtainUserInfo();
    addUserInfoListener();
  }

  private void obtainUserInfo() throws UserInfoException{
    if(myUserInfoProvider != null){
      myUserInfoProvider.fillUserInfo( myPersonalUserInfo );
    }
  }

  private void addUserInfoListener() throws UserInfoException{
    myPersonalUserInfo.addObserver( myUserInfoListener );
  }

  public void setUserInfoProvider(iUserInfoProvider aUserInfoProvider){
    try {
      myUserInfoProvider = aUserInfoProvider;
      obtainUserInfo();
      addUserInfoListener();
      announceMe();
    } catch ( UserInfoException e ) {
      LOGGER.error("Unable to retrieve user info", e);
    }
  }

  public void setMasterProtocol( IProtocol aProtocol ) {
    super.setMasterProtocol( aProtocol );

    try{
      myService.scheduleAtFixedRate( new Runnable(){
        public void run(){
          fullRetrieval();
        }
      }, 1, 60, TimeUnit.SECONDS);
      addListeners();
    }catch(Exception e){
      LOGGER.error( "Could not fully initialize UserInfoProtocol", e );
    }
  }

  public void fullRetrieval() {
    LOGGER.debug( "Doing full retrieval of user info based on routing table" );
    try{
      RoutingTable theTable = getRoutingTable();
      AbstractPeer theLocalPeer = theTable.getEntryForLocalPeer().getPeer();
      for(RoutingTableEntry theEntry : theTable){
        if(theEntry.isReachable()){
          AbstractPeer thePeer = theEntry.getPeer();
          if(thePeer.isOnSameChannel(theLocalPeer) 
              && (!myUserInfo.containsKey( thePeer.getPeerId() ) 
                  || myUserInfo.get(thePeer.getPeerId()).getStatus() == Status.OFFLINE)){
            myRetrievalService.execute( new UserInfoRetriever(thePeer.getPeerId()) );
          }
        }
      }
      notifyUserInfoChanged( null );
    }catch(Exception e){
      LOGGER.error( "Could not retrieve user info", e );
    }
  }

  public void announceMe(){
    try{
      RoutingTable theTable = getRoutingTable();
      AbstractPeer theLocalPeer = theTable.getEntryForLocalPeer().getPeer();

      for(RoutingTableEntry theEntry : theTable){
        if(theEntry.isReachable() && theEntry.getPeer().isOnSameChannel(theLocalPeer)){
          myRetrievalService.execute( new UserInfoSender(theEntry.getPeer().getPeerId()) );
        }
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
        return myConverter.toString( getPersonalInfo() );
      } catch ( UserInfoException e ) {
        LOGGER.error( "Could not retrieve personal user info", e );
        return Response.NOK.name();
      } catch ( IOException e ) {
        LOGGER.error( "Could not send personal user info", e );
        return Response.NOK.name();
      }
    }
    if(anInput.startsWith( Command.PUT.name() ) ){
      String[] theParts = anInput.split( ";" );
      String thePeerId = theParts[1];
      try{
        UserInfo theUserInfo = myConverter.getObject( theParts[2] );
        myUserInfo.put( thePeerId, theUserInfo );
        notifyUserInfoChanged( theUserInfo );
        return Response.OK.name();
      }catch(IOException e){
        LOGGER.error("Could not get user info from input", e);
        return Response.NOK.name();
      }
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
    try {
      getPersonalInfo().setStatus( Status.OFFLINE );
    } catch ( UserInfoException e ) {
      LOGGER.error( "Error occured while stopping user info protocol", e );
    }
  }

  public UserInfo getUserInfoForPeer(String aPeerId) throws UserInfoException{
    try{
      LOGGER.debug("Trying to retrieve user info for peer: '" + aPeerId + "'");
      Message theMessage = new Message(  );
      theMessage.setDestination( getRoutingTable().getEntryForPeer( aPeerId ).getPeer());
      theMessage.setSource( getRoutingTable().getEntryForLocalPeer().getPeer() );
      theMessage.setMessage( createMessage( Command.GET.name() ) );
      theMessage.setProtocolMessage( true );
      String theResult = ((MessageProtocol)findProtocolContainer().getProtocol( MessageProtocol.ID )).sendMessage( theMessage );
      LOGGER.debug("User info retrieved: '" + theResult + "'");
      return myConverter.getObject( theResult );
    }catch(Exception e){
      throw new UserInfoException("Could not retrieve user info for peer '" + aPeerId + "'", e);
    }
  }

  public void sendUserInfoToPeer(String aPeerId) throws UserInfoException{
    try{
      Message theMessage = new Message(  );
      theMessage.setDestination( getRoutingTable().getEntryForPeer( aPeerId ).getPeer() );
      theMessage.setSource( getRoutingTable().getEntryForLocalPeer().getPeer() );
      theMessage.setMessage( createMessage( Command.PUT.name() + ";" + getRoutingTable().getLocalPeerId() + ";" + myConverter.toString( myPersonalUserInfo )));
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
    return myPersonalUserInfo;
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
        //if we already knew this user, than change it's status to online
        if(myUserInfo.containsKey( myPeerId )){
          UserInfo theUserInfo = myUserInfo.get( myPeerId ); 
          theUserInfo.setStatus( Status.OFFLINE );
          notifyUserInfoChanged( theUserInfo );
        }
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
      try{
        if(!myUserInfo.containsKey( anEntry.getPeer().getPeerId() ) || myUserInfo.get(anEntry.getPeer().getPeerId()).getStatus() == Status.OFFLINE){
          if(anEntry.isReachable() && anEntry.getPeer().isOnSameChannel(getRoutingTable().getEntryForLocalPeer().getPeer())){
            myRetrievalService.execute( new UserInfoRetriever(anEntry.getPeer().getPeerId()) );
          }
        } else {
          //we have user information for this peer
          //if the entry is not reachable any more than set the status of the user to offline
          if(!anEntry.isReachable()){
            UserInfo theUserInfo = myUserInfo.get(anEntry.getPeer().getPeerId());
            theUserInfo.setStatus( Status.OFFLINE );
            notifyUserInfoChanged(theUserInfo);
          }
        }
      }catch(Exception e){
        LOGGER.error("Could not retrieve user info", e);
      }
    }

    @Override
    public void routingTableEntryRemoved( RoutingTableEntry anEntry ) {
      if(myUserInfo.containsKey( anEntry.getPeer().getPeerId() )){
        UserInfo theInfo = myUserInfo.get( anEntry.getPeer().getPeerId() );
        theInfo.setStatus( Status.OFFLINE );
        myUserInfo.remove( anEntry.getPeer().getPeerId() );
        notifyUserInfoChanged(theInfo);
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