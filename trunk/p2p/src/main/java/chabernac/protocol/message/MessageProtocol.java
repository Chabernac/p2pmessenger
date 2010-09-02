/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.encryption.EncryptionException;
import chabernac.protocol.encryption.EncryptionProtocol;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.UnknownPeerException;
import chabernac.tools.XMLTools;

/**
 * This protocol will accept a message and route it to the correct peer based on the routing table.
 * if the message if intended for the peer hosting this protocol a message received event will be fired. 
 *
 */
public class MessageProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger( MessageProtocol.class );
  public static final String ID = "MSG";
  
  private static enum STATUS_MESSAGE {UNKNOWN_PEER, UNKNOWN_HOST, UNDELIVERABLE, DELIVERED, UNCRECOGNIZED_MESSAGE, COULD_NOT_DECRYPT, TTL_EXPIRED};

  private boolean isKeepHistory = false;
  
  private List< Message > myHistory = new ArrayList< Message >();

  private List<iMessageListener> myListeners = new ArrayList< iMessageListener >();
  private List<iMessageListener> myHistoryListeners = new ArrayList< iMessageListener >();

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
    if(isKeepHistory){
      myHistory.add(aMessage);
      for(iMessageListener theListener : myHistoryListeners) theListener.messageReceived( aMessage );
    }
    
    Peer theDestionation = aMessage.getDestination();
    try {
      if(theDestionation.getPeerId().equals( getRoutingTable().getLocalPeerId() )){
        checkEnctryption(aMessage);
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
        //the message is not intented for us, it needs to be sended further.
        //only send the message further if the time to live (TTL) is not yet 0.
        if(aMessage.isEndOfTTL()){
          LOGGER.error("This message can not be send further because its TTL has expired: " + XMLTools.toXML( aMessage ) );
          return STATUS_MESSAGE.TTL_EXPIRED.name();
        } else {
          //decrease the TTL
          aMessage.decreaseTTL();
          Peer theGateway = getRoutingTable().getGatewayForPeer( theDestionation );

          Peer theLocalPeer = getRoutingTable().getEntryForLocalPeer().getPeer();
          if(!theGateway.isSameHostAndPort( theLocalPeer )){
            return theGateway.send( createMessage( XMLTools.toXML( aMessage ) ));
          } else {
            //TODO we should not come in this situation
            LOGGER.error("Peer with id: '" + theGateway.getPeerId() + "' has same host and port as local peer: '" + theLocalPeer.getPeerId() + "'");
            return STATUS_MESSAGE.UNDELIVERABLE.name() + " gateway has same host and port of local peer";
          }
        }

      }
    } catch ( UnknownPeerException e ) {
      LOGGER.error( "Unknown peer", e );
      return STATUS_MESSAGE.UNKNOWN_PEER.name() + " " + e.getPeer().getPeerId();
    } catch ( UnknownHostException e ) {
      LOGGER.error( "Unknown host", e );
      return STATUS_MESSAGE.UNKNOWN_HOST.name();
    } catch ( IOException e ) { 
      LOGGER.error( "Message could not be deliverd", e );
      return STATUS_MESSAGE.UNDELIVERABLE.name() + " " + e.getMessage();
    } catch ( ProtocolException e ) {
      LOGGER.error( "Protocol excepotin", e );
      return ProtocolContainer.Response.UNKNOWN_PROTOCOL.name();
    } catch ( EncryptionException e ) {
      LOGGER.error("Could not decrypt message", e);
      return STATUS_MESSAGE.COULD_NOT_DECRYPT.name();
    }
  }

//  private void checkEnctryption( Message anMessage ) throws EncryptionException {
//    if(anMessage.containsIndicator( MessageIndicator.ENCRYPTED )){
//      byte[] theEncryptedData = Base64.decodeBase64( anMessage.getMessage().getBytes() );
//
//      try {
//        KeyExchangeProtocol theKeyExchangeProtocol= ((KeyExchangeProtocol)findProtocolContainer().getProtocol( KeyExchangeProtocol.ID ));
//
//        iPublicPrivateKeyEncryption theEncryption = theKeyExchangeProtocol.getEncryption();
//
//        String theMessage = new String(theEncryption.decryptMessage( theEncryptedData ));
//        anMessage.setMessage( theMessage );
//        anMessage.removeMessageIndicator( MessageIndicator.ENCRYPTED );
//      } catch ( ProtocolException e ) {
//        throw new EncryptionException("Could not decrypt message", e);
//      }
//
//    }
//  }

  private void checkEnctryption( Message anMessage ) throws EncryptionException {
    if(anMessage.containsIndicator( MessageIndicator.ENCRYPTED )){
      try{
        EncryptionProtocol theEncryptionProtocol = ((EncryptionProtocol)findProtocolContainer().getProtocol( EncryptionProtocol.ID ));
        theEncryptionProtocol.decrypteMessage( anMessage );
      }catch(Exception e){
        throw new EncryptionException("Could not decrypt message", e);
      }
    }
  }

  public String sendMessage(Message aMessage) throws MessageException{
    if(aMessage.containsIndicator( MessageIndicator.TO_BE_ENCRYPTED)){
      try{
        EncryptionProtocol theEncryptionProtocol = ((EncryptionProtocol)findProtocolContainer().getProtocol( EncryptionProtocol.ID ));
        theEncryptionProtocol.encryptMessage( aMessage );
      }catch(Exception e){
        throw new MessageException("Could not send encrypted message", e);
      }
    }

    String theResult = handleMessage( 0, aMessage );
    if(theResult.startsWith( STATUS_MESSAGE.DELIVERED.name() )){
      return theResult.substring( STATUS_MESSAGE.DELIVERED.name().length() );
    }
    throw new MessageException("Message could not be delivered return code: '" + theResult + "'");
  }

//  public String sendEncryptedMessage(Message aMessage) throws MessageException{
//    KeyExchangeProtocol theKeyExchangeProtocol;
//    try {
//      theKeyExchangeProtocol = ((KeyExchangeProtocol)findProtocolContainer().getProtocol( KeyExchangeProtocol.ID ));
//    } catch ( ProtocolException e1 ) {
//      throw new MessageException("Could not send encryped message because keyexchangeprotocol was not found", e1);
//    }
//
//    iPublicPrivateKeyEncryption theEncryption = theKeyExchangeProtocol.getEncryption();
//
//    if(!theKeyExchangeProtocol.assurePublicKeyForPeer( aMessage.getDestination().getPeerId() )){
//      throw new MessageException("Could not encrypt message, public key for peer could not be retrieved");
//    }
//
//    try {
//      byte[] theEncryptedMessage = theEncryption.encryptMessageForUser( aMessage.getDestination().getPeerId(), aMessage.getMessage().getBytes() );
//
//      aMessage.setMessage( new String(Base64.encodeBase64( theEncryptedMessage )) );
//      aMessage.addMessageIndicator( MessageIndicator.ENCRYPTED );
//      return sendMessage( aMessage );
//    } catch ( EncryptionException e ) {
//      throw new MessageException("Could not enctrypt message", e);
//    }
//  }

  public void addMessageListener(iMessageListener aListener){
    myListeners.add( aListener );
  }

  public void removeMessageListener(iMessageListener aListener){
    myListeners.remove( aListener );
  }
  
  public void addMessageHistoryListener(iMessageListener aListener){
    myHistoryListeners.add( aListener );
  }

  public void removeMessageHistoryListener(iMessageListener aListener){
    myHistoryListeners.remove( aListener );
  }
  
  public List<Message> getHistory(){
    return Collections.unmodifiableList( myHistory );
  }
  
  public boolean isKeepHistory() {
    return isKeepHistory;
  }

  public void setKeepHistory( boolean anKeepHistory ) {
    isKeepHistory = anKeepHistory;
  }

  @Override
  public void stop() {
    myHistory.clear();
  }
}
