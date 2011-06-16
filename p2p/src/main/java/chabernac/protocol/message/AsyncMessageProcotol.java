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
import java.util.concurrent.TimeUnit;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.DynamicSizeExecutor;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.iPeerSender;

public class AsyncMessageProcotol extends AbstractMessageProtocol {
  public static enum Response{MESSAGE_PROCESSED, NOK, UNCRECOGNIZED_MESSAGE, MESSAGE_LOOP_DETECTED, DELIVERED};

  private iObjectStringConverter< Message > myMessageConverter = new Base64ObjectStringConverter< Message >();
  private ExecutorService mySenderService = DynamicSizeExecutor.getMediumInstance();
  private Map<String, ArrayBlockingQueue<String>> myStatusQueues = new HashMap<String, ArrayBlockingQueue<String>> ();

  public AsyncMessageProcotol( ) {
    super( "AMP" );
  }

  @Override
  public String getDescription() {
    return "Asynchronous message protocol";
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

    handleMessage( aSessionId, theMessage );

    return Response.MESSAGE_PROCESSED.name();
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
    // TODO Auto-generated method stub

  }
  
  private synchronized BlockingQueue<String> getBlockingQueueForMessage(String aMessageId){
    if(!myStatusQueues.containsKey( aMessageId )){
      myStatusQueues.put( aMessageId, new ArrayBlockingQueue<String>( 1 ));
    }
    
    return myStatusQueues.get( aMessageId );
  }
  
  private void handleDeliveryStatus( Message aMessage ) throws InterruptedException {
    getBlockingQueueForMessage( aMessage.getHeader( "MESSAGE-ID" ) ).put( aMessage.getHeader( "STATUS" ) );
    //TODO what if the delivery status response comes after the timeout of the getResponse?
    //in that case the response will stay forever on the queue, we should find a way to clean it up
    
  }
  
  public String getResponse(String aMessageId, long aTimeout, TimeUnit aTimeUnit) throws InterruptedException{
    String theResponse = getBlockingQueueForMessage( aMessageId ).poll( aTimeout, aTimeUnit );
    myStatusQueues.remove( aMessageId );
    return theResponse;
  }
  
  public void sendMessage(Message aMessage) throws MessageException{
    inspectMessage(aMessage);
    handleMessage( UUID.randomUUID().toString(), aMessage );
  }

  private void handleMessage(String aSessionId, Message aMessage){
    mySenderService.execute( new MessageProcessor( aSessionId, aMessage ) );
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
      if(myProcessingMessages.contains(myMessage.getMessageId())){
        sendDeliveryStatus( myMessage.getSource().getPeerId(), myMessage.getMessageId().toString(), Response.MESSAGE_LOOP_DETECTED.name()); 
      }

      AbstractPeer theDestination = myMessage.getDestination();
      try {
        myProcessingMessages.add(myMessage.getMessageId());
        if(theDestination.getPeerId().equals( getRoutingTable().getLocalPeerId() )){
          if("DeliveryStatus".equalsIgnoreCase( myMessage.getHeader( "TYPE" ))){
            handleDeliveryStatus(myMessage);
          } else {
            String theResponse = handleMessageForUs(mySessionId, myMessage);
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
          if(Response.MESSAGE_PROCESSED.name().equalsIgnoreCase( theResponse )){
            sendDeliveryStatus( myMessage.getSource().getPeerId(), myMessage.getMessageId().toString(), theResponse ); 
          }
        }
      } catch(Exception e){
        LOGGER.error( "Unable to process message", e );
        sendDeliveryStatus( myMessage.getSource().getPeerId(), myMessage.getMessageId().toString(), Response.NOK.name() );
      }
    }
  }
}
