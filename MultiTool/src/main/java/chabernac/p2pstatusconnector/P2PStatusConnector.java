/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pstatusconnector;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.userinfo.UserInfoProtocol;
import chabernac.protocol.userinfo.UserInfo.Status;
import chabernac.task.Task;
import chabernac.task.event.TaskStartedEvent;

public class P2PStatusConnector implements iEventListener {
  private static final Logger LOGGER = Logger.getLogger(P2PStatusConnector.class);
  
  private final String myLocalUserId;
  private P2PFacade myP2PFacade;

  public P2PStatusConnector(String aLocalUserId) throws P2PFacadeException{
    myLocalUserId = aLocalUserId;
    startP2P();
    addListeners();
  }

  private void startP2P() throws P2PFacadeException{
    Set<String> theProtocols = new HashSet<String>();
    theProtocols.add( RoutingProtocol.ID );
    theProtocols.add( MessageProtocol.ID );

    myP2PFacade = new P2PFacade()
    .setChannel( "p2pclient" )
    .start( 10, theProtocols );
    
    myP2PFacade.forceProtocolStart( UserInfoProtocol.ID );
  }

  private void addListeners(){
    ApplicationEventDispatcher.addListener( this, TaskStartedEvent.class );
  }

  @Override
  public void eventFired( Event anEvent) {
    try{
      myP2PFacade.changeRemoteUserStatus( myLocalUserId, getStatusForEvent(anEvent) );
    }catch(P2PFacadeException e){
      LOGGER.error( "Unable to change status of user '" + myLocalUserId + "' remotely");
    }
  }

  private Status getStatusForEvent( Event anEvent ) {
   Task theTask = ((TaskStartedEvent)anEvent).getTask();
   if(theTask.getFullName().toUpperCase().contains( "PAUZE" )) return Status.AWAY;
   if(theTask.getFullName().toUpperCase().contains( "ETEN" )) return Status.AWAY;
   if(theTask.getFullName().toUpperCase().contains( "MEETING" )) return Status.BUSY;
   if(theTask.getFullName().toUpperCase().contains( "WC" )) return Status.AWAY;
   return Status.ONLINE;
  }
  
  public void finalize(){
    myP2PFacade.stop();
  }
}
