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
import java.util.UUID;

import org.apache.log4j.Logger;

import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.encryption.EncryptionException;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.UnknownPeerException;

/**
 * This protocol will accept a message and route it to the correct peer based on the routing table.
 * if the message if intended for the peer hosting this protocol a message received event will be fired. 
 *
 */
public class MessageProtocol extends AbstractMessageProtocol {
  private static Logger LOGGER = Logger.getLogger( MessageProtocol.class );
  public static final String ID = "MSG";

  private boolean isKeepHistory = false;

  private List< MessageAndResponse > myHistory = new ArrayList< MessageAndResponse >();

  private List<iMessageListener> myHistoryListeners = new ArrayList< iMessageListener >();

  public MessageProtocol ( ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Message protocol";
  }

  @Override
  public String handleCommand( String aSessionId, String anInput ) {
    Message theMessage;
    try {
      theMessage = myMessageConverter.getObject( anInput );
    } catch ( IOException e ) {
      return Response.UNCRECOGNIZED_MESSAGE.name();
    }

    return  handleMessage( aSessionId, theMessage );
  }

  public String handleMessage(String aSessionId, Message aMessage){
    MessageAndResponse theHistoryItem = new MessageAndResponse( aMessage );
    if(isKeepHistory){
      myHistory.add(theHistoryItem);
      for(iMessageListener theListener : myHistoryListeners) theListener.messageReceived( aMessage );
    }

    String theResult = handleMessageInternal( aSessionId, aMessage );

    if(isKeepHistory){
      theHistoryItem.setResponse( theResult );
      for(iMessageListener theListener : myHistoryListeners) theListener.messageUpdated( aMessage );
    }

    return theResult;
  }

  public String handleMessageInternal(String aSessionId, Message aMessage){
    if(myProcessingMessages.contains(aMessage.getMessageId())){
      return Response.MESSAGE_LOOP_DETECTED.name();
    } 

    checkMessage(aMessage);

    AbstractPeer theDestination = aMessage.getDestination();
    try {
      myProcessingMessages.add(aMessage.getMessageId());
      if(theDestination.getPeerId().equals( getRoutingTable().getLocalPeerId() )){
        return handleMessageForUs(aSessionId, aMessage);
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


  /**
   * in this method we ask the routing protocol to check the sending peer.
   * When the peer is not yet in the routing table, the routing protocol will try to contact the peer
   * and update the routing table
   * @param anMessage
   */
  private void checkMessage( Message anMessage ) {
    try{
      RoutingProtocol theProtocol = ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID ));
      theProtocol.checkPeer( anMessage.getSource() );
    }catch(Exception e){
      LOGGER.error("An error occured while checking for peer ", e);
    }
  }

  public String sendMessage(Message aMessage) throws MessageException{
    int theRetries = 0;
    boolean isRetry = true;

    String theResult = null;
    while(isRetry){
      Message theMessage = aMessage.copy();
      inspectMessage(theMessage);
      theResult = handleMessage( UUID.randomUUID().toString(), theMessage );
      isRetry = isRetryResponse( theResult ) && theRetries++ < 3;
    }
    return inspectResult(theResult);
  }


  private boolean isRetryResponse(String aResponse){
    //if the encryption protocol was not able to decrypt the message it probably meant that the sender encoded the message with an old
    //public key, by now the encryption protocol will have send the new public key to the sending peer.  The sending peer can now retry
    //sending the encrypted message
    if(aResponse.startsWith( Response.COULD_NOT_DECRYPT.name() )) {
      return true;
    }

    return false;
  }

  public void addMessageHistoryListener(iMessageListener aListener){
    myHistoryListeners.add( aListener );
  }

  public void removeMessageHistoryListener(iMessageListener aListener){
    myHistoryListeners.remove( aListener );
  }

  public List<MessageAndResponse> getHistory(){
    return Collections.unmodifiableList( myHistory );
  }

  public boolean isKeepHistory() {
    return isKeepHistory;
  }

  public void setKeepHistory( boolean anKeepHistory ) {
    isKeepHistory = anKeepHistory;
  }

  public void clearHistory(){
    myHistory.clear();
  }

  @Override
  public void stop() {
    myHistory.clear();
  }
}
