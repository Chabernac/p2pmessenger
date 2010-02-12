/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import chabernac.protocol.Protocol;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.UnkwownPeerException;
import chabernac.tools.XMLTools;

/**
 * This protocol will accept a message and route it to the correct peer based on the routing table.
 * if the message if intended for the peer hosting this protocol a message received event will be fired. 
 *
 */
public class MessageProtocol extends Protocol {
  private static enum STATUS_MESSAGE {UKWNONW_PEER, UNKNOWN_HOST, UNDELIVERABLE, DELIVERED};
  
  private RoutingTable myRoutingTable = null;
  private Peer myMyself = null;
  private Set<iMessageReceiveListener> myListeners = new HashSet< iMessageReceiveListener >();

  public MessageProtocol ( Peer aMySelf, RoutingTable aRoutingTable ) {
    super( "MSG" );
    myRoutingTable = aRoutingTable;
    myMyself = aMySelf;
  }

  @Override
  public String getDescription() {
    return "Message protocol";
  }
  
  public void addMessageReceiveListener(iMessageReceiveListener aListener){
    myListeners.add( aListener );
  }
  
  public void removeMessageReceiveListener(iMessageReceiveListener aListener){
    myListeners.remove( aListener );
  }

  @Override
  protected String handleCommand( long aSessionId, String anInput ) {
    Message theMessage = (Message)XMLTools.fromXML( anInput );
    return handleMessage( theMessage );
  }
  
  public String handleMessage(Message aMessage){
    Peer theDestionation = aMessage.getDestination();
    if(theDestionation.equals( myMyself )){
      for(iMessageReceiveListener theListener : myListeners){
        theListener.messageReceived( aMessage );
      }
      return STATUS_MESSAGE.DELIVERED.name();
    } else {
      try {
        Peer theGateway = myRoutingTable.getGatewayForPeer( theDestionation );
        return theGateway.send( createMessage( XMLTools.toXML( aMessage ) ));
      } catch ( UnkwownPeerException e ) {
        return STATUS_MESSAGE.UKWNONW_PEER.name();
      } catch ( UnknownHostException e ) {
        return STATUS_MESSAGE.UNKNOWN_HOST.name();
      } catch ( IOException e ) {
        return STATUS_MESSAGE.UNDELIVERABLE.name();
      }
    } 
  }

}
