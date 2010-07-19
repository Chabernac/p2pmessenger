/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.eventadapter;

import java.util.Map;

import chabernac.events.EventDispatcher;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.infoexchange.InfoObject;
import chabernac.protocol.infoexchange.iInfoListener;
import chabernac.protocol.message.DeliveryReport;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iDeliverReportListener;
import chabernac.protocol.message.iMultiPeerMessageListener;
import chabernac.protocol.pipe.IPipeListener;
import chabernac.protocol.pipe.Pipe;
import chabernac.protocol.pipe.PipeException;
import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.iUserInfoListener;

public class P2PEventAdapter implements iUserInfoListener, iInfoListener< InfoObject >, iDeliverReportListener, iMultiPeerMessageListener, IPipeListener {
  private final P2PFacade myFacade;
  
  public P2PEventAdapter(P2PFacade aFacade){
    myFacade = aFacade;
  }
  
  public void link() throws P2PFacadeException{
    myFacade.addUserInfoListener( this );
    myFacade.addInfoListener( this );
    myFacade.addDeliveryReportListener( this );
    myFacade.addMessageListener( this );
    myFacade.addPipeListener( this );
  }
  
  public void unLink() throws P2PFacadeException{
    myFacade.removeDeliveryReportListener( this );
    myFacade.removeMessageListener( this );
    myFacade.removeUserInfoListener( this );
    myFacade.removeInfoListener( this );
    myFacade.removePipeListener( this );
  }

  @Override
  public void userInfoChanged( UserInfo aUserInfo, Map< String, UserInfo > aFullUserInfoList ) {
    EventDispatcher.getInstance( UserInfoChangeEvent.class ).fireEvent( new UserInfoChangeEvent(aUserInfo, aFullUserInfoList) );     
  }

  @Override
  public void infoChanged( String aPeerId, Map< String, InfoObject > aInfoMap ) {
    EventDispatcher.getInstance( InfoChangedEvent.class ).fireEvent( new InfoChangedEvent(aPeerId, aInfoMap));
  }

  @Override
  public void acceptDeliveryReport( DeliveryReport aDeliverReport ) {
    EventDispatcher.getInstance( DeliveryReportEvent.class ).fireEvent( new DeliveryReportEvent(aDeliverReport));
  }

  @Override
  public void messageReceived( MultiPeerMessage aMessage ) {
    EventDispatcher.getInstance( MultiPeerMessageEvent.class ).fireEvent( new MultiPeerMessageEvent(aMessage));
  }

  @Override
  public void incomingPipe( Pipe aPipe ) throws PipeException {
    EventDispatcher.getInstance( PipeEvent.class ).fireEvent( new PipeEvent(aPipe));
  }

}
