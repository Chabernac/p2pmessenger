/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.encryption.EncryptionException;
import chabernac.protocol.encryption.EncryptionProtocol;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.UnknownPeerException;
import chabernac.protocol.routing.iPeerSender;

/**
 * This protocol will accept a message and route it to the correct peer based on the routing table.
 * if the message if intended for the peer hosting this protocol a message received event will be fired. 
 *
 */
public class MessageProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger( MessageProtocol.class );
  public static final String ID = "MSG";

  public static enum Response {
    UNKNOWN_PEER, 
    UNKNOWN_HOST, 
    UNDELIVERABLE, 
    DELIVERED, 
    UNCRECOGNIZED_MESSAGE, 
    COULD_NOT_DECRYPT, 
    TTL_EXPIRED, 
    MESSAGE_LOOP_DETECTED,
    MESSAGE_ALREADY_RECEIVED};

    private boolean isKeepHistory = false;

    private List< MessageAndResponse > myHistory = new ArrayList< MessageAndResponse >();

    private List<iMessageListener> myListeners = new ArrayList< iMessageListener >();
    private List<iMessageListener> myHistoryListeners = new ArrayList< iMessageListener >();

    private iObjectStringConverter< Message > myMessageConverter = new Base64ObjectStringConverter< Message >();

    private List<UUID> myProcessingMessages = Collections.synchronizedList(new ArrayList<UUID>());
    
    private Set<UUID> myProcessedMessages = Collections.synchronizedSet(new HashSet<UUID>());

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
    
    public iPeerSender getPeerSender() throws ProtocolException{
      return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getPeerSender();
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

    private String handleMessageForUs(String aSessionId, Message aMessage) throws EncryptionException{
      if(myProcessedMessages.contains(aMessage.getMessageId())){
        return Response.MESSAGE_ALREADY_RECEIVED.name();
      }
      
      myProcessedMessages.add(aMessage.getMessageId());
      
      checkEnctryption(aMessage);
      if(aMessage.isProtocolMessage()){
        //reoffer the content of the message to the handle method
        //this will cause sub protocols to handle the message if they are present
        return Response.DELIVERED.name() + getMasterProtocol().handleCommand( aSessionId, aMessage.getMessage() );
      } else {
        for(iMessageListener theListener : myListeners){
          theListener.messageReceived( aMessage );
        }
        return Response.DELIVERED.name();
      }
    }

    private String forwardMessage(Message aMessage) throws UnknownPeerException, ProtocolException, UnknownHostException, IOException{
      if(aMessage.isEndOfTTL()){
        LOGGER.error("This message can not be send further because its TTL has expired: " + aMessage.getMessageId() );
        return Response.TTL_EXPIRED.name();
      } else {
        aMessage.decreaseTTL();

        RoutingTableEntry theEntry = getRoutingTable().getEntryForPeer( aMessage.getDestination().getPeerId() );

        //only forward the message if this peer is reachable
        if(!theEntry.isReachable()) return Response.UNDELIVERABLE.name() + " the peer with peer id: '" + theEntry.getPeer().getPeerId() + "' is not reachable";

        AbstractPeer theGateway = getRoutingTable().getGatewayForPeer( aMessage.getDestination() );

        AbstractPeer theLocalPeer = getRoutingTable().getEntryForLocalPeer().getPeer();
        if(!theGateway.isSameEndPointAs( theLocalPeer )){
          aMessage.setLocked( true );
          String theText = myMessageConverter.toString( aMessage );
          aMessage.setLocked( false );
          return getPeerSender().send(theGateway, createMessage( theText ), aMessage.getMessageTimeoutInSeconds());
        } else {
          //TODO we should not come in this situation
          LOGGER.error("Peer with id: '" + theGateway.getPeerId() + "' has same host and port as local peer: '" + theLocalPeer.getPeerId() + "'");
          return Response.UNDELIVERABLE.name() + " gateway has same host and port of local peer";
        }
      }
    }

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

    private void inspectMessage(Message aMessage) throws MessageException{
      try {
        aMessage.setSource(getRoutingTable().getEntryForLocalPeer().getPeer());
      } catch (Exception e1) {
        throw new MessageException("Unable to set source peer in message", e1);
      }
      //check if both peers are on the same channel
      if(!aMessage.getSource().isOnSameChannel(aMessage.getDestination())){
        throw new MessageException("Can not send message to peer on another channel");
      }
      
      if(aMessage.isProtocolMessage()){
        //inspect if the protocol inside the message is supported by the other peer
        String theProtocol = aMessage.getMessage().substring( 0, 3 );
        if(!aMessage.getDestination().isProtocolSupported(  theProtocol )){
          throw new MessageException( "The protocol inside this message is '" + theProtocol + "' is not supported by this client" );
        }
      }

      if(aMessage.containsIndicator( MessageIndicator.TO_BE_ENCRYPTED)){
        try{
          EncryptionProtocol theEncryptionProtocol = ((EncryptionProtocol)findProtocolContainer().getProtocol( EncryptionProtocol.ID ));
          theEncryptionProtocol.encryptMessage( aMessage );
        }catch(Exception e){
          throw new MessageException("Could not send encrypted message", e);
        }
      }

      //we reset the TTL, it might be that the same message is reused.
      aMessage.resetTTL();

    }

    public String sendMessage(Message aMessage) throws MessageException{
        inspectMessage(aMessage);
        String theResult = handleMessage( UUID.randomUUID().toString(), aMessage );
        return inspectResult(theResult);
    }

    private String inspectResult(String aResult) throws MessageException{
      if(aResult.startsWith( Response.DELIVERED.name() )){
        return aResult.substring( Response.DELIVERED.name().length() );
      } else if(aResult.startsWith(Response.MESSAGE_ALREADY_RECEIVED.name())){
        throw new MessageAlreadyDeliveredException("This message was already delivered to this peer");
      }
      throw new MessageException("Message could not be delivered return code: '" + aResult + "'", Response.valueOf(aResult.split(" ")[0]));  
    }

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
