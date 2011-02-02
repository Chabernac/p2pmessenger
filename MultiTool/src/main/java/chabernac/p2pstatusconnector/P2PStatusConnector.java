/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pstatusconnector;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.userinfo.EmptyUserInfoProvider;
import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.UserInfoProtocol;
import chabernac.protocol.userinfo.iUserInfoListener;
import chabernac.protocol.userinfo.UserInfo.Status;
import chabernac.task.Task;
import chabernac.task.TaskTools;
import chabernac.task.event.ApplicationSaveEvent;
import chabernac.task.event.PeriodChangedEvent;
import chabernac.task.event.TaskRemovedEvent;
import chabernac.task.event.TaskStartedEvent;

public class P2PStatusConnector implements iEventListener, iUserInfoListener {
  private static final Logger LOGGER = Logger.getLogger(P2PStatusConnector.class);

  private final String myLocalUserId;
  private P2PFacade myP2PFacade;

  private Map<String, Status> myStatusMapping = new HashMap<String, Status>();

  public P2PStatusConnector(String aLocalUserId) throws P2PFacadeException{
    myLocalUserId = aLocalUserId;
    try{
      startP2P();
      addListeners();
      loadStatusMapping();
    }catch(Exception e){
      throw new P2PFacadeException("Unable to start p2p status connector", e);
    }
  }

  private void startP2P() throws P2PFacadeException{
    Set<String> theProtocols = new HashSet<String>();
    theProtocols.add( RoutingProtocol.ID );
    theProtocols.add( MessageProtocol.ID );

    myP2PFacade = new P2PFacade()
    .setChannel( "p2pclient" )
    .setPersist( true )
    .setSocketReuse( false )
    .setUserInfoProvider( new EmptyUserInfoProvider() )
    .start( 256, theProtocols );

    myP2PFacade.forceProtocolStart( UserInfoProtocol.ID );
  }

  private void addListeners() throws P2PFacadeException{
    ApplicationEventDispatcher.addListener( this, new Class[]{TaskStartedEvent.class, TaskRemovedEvent.class, PeriodChangedEvent.class, ApplicationSaveEvent.class} );
    myP2PFacade.addUserInfoListener( this );
  }

  private void loadStatusMapping() throws IOException{
    BufferedReader theReader = null;
    try{
      theReader = new BufferedReader( new InputStreamReader( new FileInputStream( "statusmapping.txt" ) ) );
      String theLine = null;
      while((theLine = theReader.readLine()) != null){
        String[] theParts = theLine.split( "=" );
        if(theParts.length == 2){
          myStatusMapping.put( theParts[0], Status.valueOf( theParts[1] ) );
        }
      }
    } finally {
      theReader.close();
    }
  }

  @Override
  public void eventFired( Event anEvent) {
    if(anEvent instanceof ApplicationSaveEvent){
      myP2PFacade.stop();
    } else {
      updateStatus();
    }
  }

  private void updateStatus(){
    try{
      Task theRunningTask = TaskTools.getRunningTask();
      myP2PFacade.changeRemoteUserStatus( myLocalUserId, getStatusForTask( theRunningTask), theRunningTask != null ? theRunningTask.getName() : null );
    }catch(P2PFacadeException e){
      LOGGER.error( "Unable to change status of user '" + myLocalUserId + "' remotely");
    }
  }

  private Status getStatusForTask(Task aTask){
    if(aTask == null) return Status.ONLINE;

    for(String theWord : myStatusMapping.keySet()){
      if(aTask.getFullName().toUpperCase().contains( theWord.toUpperCase() )){
        return myStatusMapping.get( theWord );
      }
    }

    return Status.ONLINE;
  }

  public void finalize(){
    myP2PFacade.stop();
  }

  @Override
  public void userInfoChanged( UserInfo aUserInfo, Map<String, UserInfo> aFullUserInfoList ) {
    if((aUserInfo != null && aUserInfo.getId().equalsIgnoreCase( myLocalUserId )) ||
        aFullUserInfoList.containsKey( myLocalUserId )){
      updateStatus();
    }
  }
}
