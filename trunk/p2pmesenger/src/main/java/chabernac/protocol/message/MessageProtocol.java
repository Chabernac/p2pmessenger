/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.UnkwownPeerException;
import chabernac.tools.XMLTools;

/**
 * This protocol will accept a message and route it to the correct peer based on the routing table.
 * if the message if intended for the peer hosting this protocol a message received event will be fired. 
 *
 */
public class MessageProtocol extends Protocol {
  public static final String ID = "MSG";

  private static enum STATUS_MESSAGE {UKWNONW_PEER, UNKNOWN_HOST, UNDELIVERABLE, DELIVERED, UNCRECOGNIZED_MESSAGE}

  private List<iMessageListener> myListeners = new ArrayList< iMessageListener >();

  public MessageProtocol ( ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Message protocol";
  }

  public RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    Object theMessage = XMLTools.fromXML( anInput );
    if(!(theMessage instanceof Message)){
      return STATUS_MESSAGE.UNCRECOGNIZED_MESSAGE.name();
    } 
    return handleMessage( aSessionId, (Message)theMessage );
  }

  public String handleMessage(long aSessionId, Message aMessage){
    Peer theDestionation = aMessage.getDestination();
    try {
      if(theDestionation.getPeerId().equals( getRoutingTable().getLocalPeerId() )){
        if(aMessage.isProtocolMessage()){
          //reoffer the content of the message to the handle method
          //this will cause sub protocols to handle the message if they are present
          return STATUS_MESSAGE.DELIVERED.name() + getMasterProtocol().handleCommand( aSessionId, aMessage.getMessage() );
        } else {
          for(iMessageListener theListener : myListeners){
            theListener.messageReceived( aMessage );
          }
          return STATUS_MESSAGE.DELIVERED.name();
        }
      } else {
        Peer theGateway = getRoutingTable().getGatewayForPeer( theDestionation );
        return theGateway.send( createMessage( XMLTools.toXML( aMessage ) ));
      }
    } catch ( UnkwownPeerException e ) {
      return STATUS_MESSAGE.UKWNONW_PEER.name();
    } catch ( UnknownHostException e ) {
      return STATUS_MESSAGE.UNKNOWN_HOST.name();
    } catch ( IOException e ) { 
      return STATUS_MESSAGE.UNDELIVERABLE.name();
    } catch ( ProtocolException e ) {
      return ProtocolContainer.Response.UNKNOWN_PROTOCOL.name();
    }
  }
  
  public String sendMessage(Message aMessage) throws MessageException{
    String theResult = handleMessage( 0, aMessage );
    if(theResult.startsWith( STATUS_MESSAGE.DELIVERED.name() )){
      return theResult.substring( STATUS_MESSAGE.DELIVERED.name().length() );
    }
    throw new MessageException("Message could not be delivered return code: '" + theResult + "'");
  }

  public void addMessageListener(iMessageListener aListener){
    myListeners.add( aListener );
  }

  public void removeMessageListener(iMessageListener aListener){
    myListeners.remove( aListener );
  }

  @Override
  public void stop() {
  }
}
