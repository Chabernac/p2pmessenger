/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;

import org.apache.log4j.Logger;

import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.encryption.EncryptionException;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.UnknownPeerException;
import chabernac.tools.PropertyMap;

/**
 * This protocol will accept a message and route it to the correct peer based on the routing table.
 * if the message if intended for the peer hosting this protocol a message received event will be fired. 
 *
 */
public class MessageProtocol extends AbstractMessageProtocol {
  private static Logger LOGGER = Logger.getLogger( MessageProtocol.class );
  public static final String ID = "MSG";

  public MessageProtocol ( ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Message protocol";
  }
  
  public int getImportance(){
    return 2;
  }

  @Override
  public String handleCommand( String aSessionId, PropertyMap aProperties, String anInput ) {
    Message theMessage;
    try {
      theMessage = myMessageConverter.getObject( anInput );
    } catch ( IOException e ) {
      return Response.UNCRECOGNIZED_MESSAGE.name();
    }

    return  handleMessage( aSessionId, aProperties, theMessage );
  }

  public String handleMessage(String aSessionId, PropertyMap aProperties, Message aMessage){
    MessageAndResponse theHistoryItem = new MessageAndResponse( aMessage );
    if(isKeepHistory){
      myHistory.put(aMessage.getMessageId().toString(), theHistoryItem);
      for(iMessageListener theListener : myHistoryListeners) theListener.messageReceived( aMessage );
    }

    String theResult = handleMessageInternal( aSessionId, aProperties, aMessage );

    if(isKeepHistory){
      theHistoryItem.setResponse( theResult );
      for(iMessageListener theListener : myHistoryListeners) theListener.messageUpdated( aMessage );
    }

    return theResult;
  }

  public String handleMessageInternal(String aSessionId, PropertyMap aProperties, Message aMessage){
    if(myProcessingMessages.contains(aMessage.getUniqueId())){
      return Response.MESSAGE_LOOP_DETECTED.name();
    } 

    checkMessage(aMessage);

    AbstractPeer theDestination = aMessage.getDestination();
    try {
      myProcessingMessages.add(aMessage.getUniqueId());
      if(theDestination.getPeerId().equals( getRoutingTable().getLocalPeerId() )){
        return handleMessageForUs(aSessionId, aProperties, aMessage);
      } else {
        //the message is not intented for us, it needs to be sended further.
        //only send the message further if the time to live (TTL) is not yet 0.
        return forwardMessage(aMessage);
      }
    } catch ( UnknownPeerException e ) {
      LOGGER.error( "Unknown peer", e );
      return Response.UNKNOWN_PEER.name();
    } catch ( UnknownHostException e ) {
      LOGGER.error( "Unknown host", e );
      return Response.UNKNOWN_HOST.name();
    } catch ( IOException e ) { 
      LOGGER.error( "Message could not be deliverd", e );
      return Response.UNDELIVERABLE.name() + " " + e.getMessage();
    } catch ( ProtocolException e ) {
      LOGGER.error( "Protocol excepotin", e );
      return ProtocolContainer.Response.UNKNOWN_PROTOCOL.name();
    } catch ( EncryptionException e ) {
      LOGGER.error("Could not decrypt message", e);
      return Response.COULD_NOT_DECRYPT.name() + " " + e.getReason().name();
    } finally {
      myProcessingMessages.remove(aMessage.getMessageId());
    }
  }


  public String sendMessage(Message aMessage) throws MessageException{
    int theRetries = 0;
    boolean isRetry = true;

    String theResult = null;
    while(isRetry){
      Message theMessage = aMessage.copy();
      inspectMessage(theMessage);
      theResult = handleMessage( UUID.randomUUID().toString(), null, theMessage );
      isRetry = isRetryResponse( theMessage, theResult ) && theRetries++ < 3;
    }
    return inspectResult(theResult);
  }


  private boolean isRetryResponse(Message aMessage, String aResponse){
    //if the encryption protocol was not able to decrypt the message it probably meant that the sender encoded the message with an old
    //public key, by now the encryption protocol will have send the new public key to the sending peer.  The sending peer can now retry
    //sending the encrypted message
    if(aResponse.startsWith( Response.COULD_NOT_DECRYPT.name() )) {
      if(obtainPublicKey(aMessage.getDestination())) return true;
    }

    return false;
  }

  @Override
  public void stop() {
    myHistory.clear();
  }
}
