/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.encryption.EncryptionException;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.iPeerSender;

public class AsyncMessageProcotol extends AbstractMessageProtocol {
  public static final String ID = "AMP";
  public static final int DEFAULT_WAIT_TIME = 30;

  private iObjectStringConverter< Message > myMessageConverter = new Base64ObjectStringConverter< Message >();
  //  private ExecutorService mySenderService = DynamicSizeExecutor.getSmallInstance();
  private Map<String, ArrayBlockingQueue<String>> myStatusQueues = new HashMap<String, ArrayBlockingQueue<String>> ();

  private ScheduledExecutorService myQueueCleanupService = Executors.newScheduledThreadPool( 1 );
  private ExecutorService myListenerService = Executors.newCachedThreadPool();

  private final String CANCEL = UUID.randomUUID().toString();

  public AsyncMessageProcotol( ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Asynchronous message protocol";
  }

  public int getImportance(){
    return 2;
  }

  public RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }

  public RoutingProtocol getRoutingProtocol() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID ));
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

    if(handleMessage( aSessionId, theMessage )){
      return Response.MESSAGE_PROCESSED.name();
    } else {
      return Response.MESSAGE_REJECTED.name();
    }

  }

  private void sendDeliveryStatus(String aPeerId, String aMessageId, String aResponse){
    try{
      Message theMessage = new Message();
      theMessage.setDestination( getRoutingTable().getEntryForPeer( aPeerId ).getPeer() );
      theMessage.setSource( getRoutingTable().getEntryForLocalPeer().getPeer() );
      theMessage.setProtocolMessage( true );
      theMessage.addHeader( "TYPE", "DeliveryStatus" );
      theMessage.addHeader( "MESSAGE-ID", aMessageId );
      theMessage.addHeader( "STATUS", aResponse );
      handleMessage( UUID.randomUUID().toString(), theMessage );
    }catch(Exception e){
      LOGGER.error("Unable to send delivery status", e);
    }
  }

  @Override
  public void stop() {
  }

  private synchronized BlockingQueue<String> getBlockingQueueForMessage(String aMessageId){
    if(!myStatusQueues.containsKey( aMessageId )){
      myStatusQueues.put( aMessageId, new ArrayBlockingQueue<String>( 1 ));
    }

    return myStatusQueues.get( aMessageId );
  }

  private void informListeners(final Message aMessage){
    for(iMessageListener theListener : myHistoryListeners) {
      final iMessageListener theList = theListener;
      myListenerService.execute( new Runnable(){
        public void run(){
          theList.messageReceived( aMessage ); 
        }
      });
    }
  }

  private void updateHistoryForDeliveryStatus(Message aMessage){
    try{
      if(isKeepHistory){
        myHistory.remove(aMessage.getMessageId().toString());
        MessageAndResponse theMR = myHistory.get(aMessage.getHeader( "MESSAGE-ID" ));
        if(theMR != null){
          theMR.setResponse(aMessage.getHeader("STATUS"));
          informListeners( aMessage );
        }
      }
    }catch(Exception e){
      LOGGER.error("Error occured while updating history", e);
    }
  }

  private void handleDeliveryStatus( final Message aMessage ) throws InterruptedException {
    updateHistoryForDeliveryStatus(aMessage);

    getBlockingQueueForMessage( aMessage.getHeader( "MESSAGE-ID" ) ).put( aMessage.getHeader( "STATUS" ) );
    myQueueCleanupService.schedule( new Runnable(){
      public void run(){
        //when no one picked up the status for this message, remove it after 5 minutes so that it does not stay cached for ever
        myStatusQueues.remove( aMessage.getHeader( "MESSAGE-ID" ) );
      }
    }, 5, TimeUnit.MINUTES);
  }

  public String getResponse(String aMessageId, long aTimeout, TimeUnit aTimeUnit) throws MessageException{
    try {
      String theResponse = getBlockingQueueForMessage( aMessageId ).poll( aTimeout, aTimeUnit );
      if(theResponse == null){
        throw new MessageException("No response received for message with id '" + aMessageId + "'");
      } else if(CANCEL.equals( theResponse )){
        throw new MessageException("Waiting for message with id '" + aMessageId +  "' has been cancelled");
      }
      return inspectResult( theResponse );
    } catch ( InterruptedException e ) {
      LOGGER.error("Could not wait for response", e);
      return null;
    } finally {
      myStatusQueues.remove( aMessageId );
    }
  }

  public void cancelResponse( String aMessageId ) throws InterruptedException {
    getBlockingQueueForMessage( aMessageId ).put( CANCEL );
  }


  public void sendMessage(Message aMessage) throws MessageException{
    inspectMessage(aMessage);
    handleMessage( UUID.randomUUID().toString(), aMessage );
  }

  public String sendAndWaitForResponse(Message aMessage) throws MessageException{
    return sendAndWaitForResponse(aMessage, DEFAULT_WAIT_TIME, TimeUnit.SECONDS);
  }

  public String sendAndWaitForResponse(Message aMesage, long aTimeout, TimeUnit aTimeUnit) throws MessageException{
    sendMessage( aMesage );
    return getResponse( aMesage.getMessageId().toString(), aTimeout, aTimeUnit );
  }

  private boolean handleMessage(String aSessionId, Message aMessage){
    MessageAndResponse theHistoryItem = new MessageAndResponse( aMessage );

    try{
      if(isKeepHistory && !"DeliveryStatus".equals( aMessage.getHeader( "TYPE" ) )){
        myHistory.put(aMessage.getMessageId().toString(), theHistoryItem);
        informListeners( aMessage );
      }
    }catch(Exception e){
      LOGGER.error("Error occured while updating history", e);
    }

    try{
      getExecutorService().execute( new MessageProcessor( aSessionId, aMessage ) );
      return true;
    } catch(RejectedExecutionException e){
      LOGGER.error("Message was rejected ", e);
      return false;
    }
  }

  private class MessageProcessor implements Runnable{
    private final Message myMessage;
    private final String mySessionId;

    public MessageProcessor( String aSessionId, Message aMessage ) {
      super();
      myMessage = aMessage;
      mySessionId = aSessionId;
    }

    @Override
    public void run() {
      LOGGER.debug("Handling message " + myMessage);

      if(myProcessingMessages.contains(myMessage.getMessageId())){
        sendDeliveryStatus( myMessage.getSource().getPeerId(), myMessage.getMessageId().toString(), Response.MESSAGE_LOOP_DETECTED.name());
        return;
      }

      checkMessage(myMessage);

      AbstractPeer theDestination = myMessage.getDestination();
      try {
        myProcessingMessages.add(myMessage.getMessageId());
        if(theDestination.getPeerId().equals( getRoutingTable().getLocalPeerId() )){
          if("DeliveryStatus".equalsIgnoreCase( myMessage.getHeader( "TYPE" ))){
            handleDeliveryStatus(myMessage);
          } else {
            String theResponse = handleMessageForUs(mySessionId, myMessage);

            if(isKeepHistory){
              myHistory.get( myMessage.getMessageId().toString() ).setResponse( theResponse );
              informListeners( myMessage );
            }

            //it might be that the routing table does not yet contain an entry for the peer from which the message comes
            //the entry should be put there any time from now becaus of the previous call to checkMessage
            //lets just wait max 10 seconds untill the entry is there
            getRoutingProtocol().getRoutingTable().getEntryForPeer(myMessage.getSource().getPeerId(), 10);

            //wathever the response is of the local processing, we want to send it back to the sender
            sendDeliveryStatus( myMessage.getSource().getPeerId(), myMessage.getMessageId().toString(), theResponse );
          }

        } else {
          //the message is not intented for us, it needs to be sended further.
          //only send the message further if the time to live (TTL) is not yet 0.
          String theResponse = forwardMessage(myMessage);
          //if the response is message processed it means the next peer was able to accept and process the message
          //in that case we do not need to do anything
          //in all other cases something went wrong and the message will not be delivered to the destination
          //send a delivery status message to indicate that something went wrong
          if(!Response.MESSAGE_PROCESSED.name().equalsIgnoreCase( theResponse )){
            sendDeliveryStatus( myMessage.getSource().getPeerId(), myMessage.getMessageId().toString(), theResponse ); 
          }
        }
      } catch(EncryptionException e){
        sendDeliveryStatus( myMessage.getSource().getPeerId(), myMessage.getMessageId().toString(), Response.COULD_NOT_DECRYPT.name() + " " + e.getReason().name());
      } catch(Exception e){
        LOGGER.error( "Unable to process message", e );
        sendDeliveryStatus( myMessage.getSource().getPeerId(), myMessage.getMessageId().toString(), Response.UNDELIVERABLE.name() );
      }
    }
  }
}
