/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.application;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.iProtocolDelegate;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

public class ApplicationProtocol extends Protocol {
  public static final String ID = "APP";
  
  private enum Response{NO_DELEGATE}; 
  
  private iProtocolDelegate myDelegate;

  public ApplicationProtocol ( ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Application protocol";
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    if(myDelegate != null){
      return myDelegate.handleCommand( aSessionId, anInput );
    }
    return Response.NO_DELEGATE.name();
  }

  @Override
  public void stop() {

  }

  public iProtocolDelegate getDelegate() {
    return myDelegate;
  }

  public void setDelegate( iProtocolDelegate anDelegate ) {
    myDelegate = anDelegate;
  }
  
  private RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }
  
  public String sendMessage(String aPeerId, String aMessage) throws ApplicationProtocolException{
    try{
      Message theMessage = new Message(  );
      theMessage.setDestination( getRoutingTable().getEntryForPeer( aPeerId ).getPeer() );
      theMessage.setSource( getRoutingTable().getEntryForLocalPeer().getPeer() );
      theMessage.setMessage( createMessage( aMessage ));
      theMessage.setProtocolMessage( true );
      return ((MessageProtocol)findProtocolContainer().getProtocol( MessageProtocol.ID )).sendMessage( theMessage );
    }catch(Exception e){
      throw new ApplicationProtocolException("Could not send user info to peer '" + aPeerId + "'", e);
    }
  }
}
