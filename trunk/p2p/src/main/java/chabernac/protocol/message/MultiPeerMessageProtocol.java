/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.DynamicSizeExecutor;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.DeliveryReport.Status;
import chabernac.protocol.routing.DummyPeer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

public class MultiPeerMessageProtocol extends Protocol{
  private static Logger LOGGER = Logger.getLogger( MultiPeerMessageProtocol.class );
  public static String ID = "MPH";

  private static enum STATUS_MESSAGE { FAILED, DELIVERED }

  private List< iDeliverReportListener > myDeliverReportListeners = new ArrayList< iDeliverReportListener >();
  private ExecutorService mySendService = DynamicSizeExecutor.getSmallInstance();
  private iObjectStringConverter< MultiPeerMessage > myObjectStringConverter = new Base64ObjectStringConverter< MultiPeerMessage >();
  private List<iMultiPeerMessageListener> myMessageListeners = new ArrayList< iMultiPeerMessageListener >();
  private ExecutorService myEventHandlerService = Executors.newSingleThreadExecutor();

  public MultiPeerMessageProtocol (  ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Multi peer message protocol";
  }

  @Override
  public String handleCommand( String aSessionId, String anInput ) {
    try {
      MultiPeerMessage theMessage = myObjectStringConverter.getObject( anInput );
      notifyListeners(theMessage);
      return STATUS_MESSAGE.DELIVERED.name();
    } catch ( IOException e ) {
      return STATUS_MESSAGE.FAILED.name();
    }
  }
  
  /**
   * we use a seperate thread to notify the listeners
   * just to make sure some code in the listeners does not get the protocol server stuck
   * @param aMessage
   */
  private void notifyListeners(final MultiPeerMessage aMessage){
    myEventHandlerService.execute(new Runnable(){
      public void run(){
        for(iMultiPeerMessageListener theListener : myMessageListeners){
          theListener.messageReceived( aMessage );
        }  
      }
    });
  }

  public void addMultiPeerMessageListener(iMultiPeerMessageListener aMultiPeerMessageListener){
    myMessageListeners.add( aMultiPeerMessageListener );
  }

  public void removeMultiPeerMessageListener(iMultiPeerMessageListener aMultiPeerMessageListener){
    myMessageListeners.remove(aMultiPeerMessageListener);
  }

  public void addDeliveryReportListener(iDeliverReportListener aDeliveryReportListener){
    myDeliverReportListeners.add( aDeliveryReportListener );
  }

  public void removeDeliveryReportListener(iDeliverReportListener aDeliveryReportListener){
    myDeliverReportListeners.remove( aDeliveryReportListener );
  }

  private void sendDeliveryReport(DeliveryReport aDeliveryReport){
    for(iDeliverReportListener theListener : myDeliverReportListeners){
      theListener.acceptDeliveryReport( aDeliveryReport );
    }
  }

  private MessageProtocol getMessageProtocol() throws ProtocolException{
    return (MessageProtocol)findProtocolContainer().getProtocol( MessageProtocol.ID);
  }

  public RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID)).getRoutingTable();
  }

  public MultiPeerMessage sendMessage(MultiPeerMessage aMultiPeerMessage) throws MessageException{
    MultiPeerMessage theMultiPeerMessage = null;
    try {
      if(aMultiPeerMessage.isLoopBack()){
        theMultiPeerMessage = aMultiPeerMessage.addDestination( getRoutingTable().getLocalPeerId() );
      } else {
        theMultiPeerMessage = aMultiPeerMessage.setSource( getRoutingTable().getLocalPeerId() )
         .removeDestination( getRoutingTable().getLocalPeerId() );
      }

      for(String theDestination : theMultiPeerMessage.getDestinations()){
        Message theMessage = new Message();
        try{
          theMessage.setSource( getRoutingTable().getEntryForPeer(theMultiPeerMessage.getSource()).getPeer() );
          theMessage.setProtocolMessage( true );
          theMessage.setIndicators( new ArrayList< MessageIndicator >(theMultiPeerMessage.getIndicators()) );
          theMessage.setMessage( createMessage( myObjectStringConverter.toString( theMultiPeerMessage ) ) );
          theMessage.setDestination( getRoutingTable().getEntryForPeer( theDestination ).getPeer() );
          sendMessage( theMultiPeerMessage, theMessage );
        }catch(Exception e){
          if(theMessage.getDestination() == null) theMessage.setDestination(new DummyPeer(theDestination) );
          sendDeliveryReport( new DeliveryReport(theMultiPeerMessage, Status.FAILED, theMessage));
          LOGGER.error( "Sending multi peer message to '"  +  theDestination + "' failed", e );
        }
      }
      return theMultiPeerMessage;
    } catch ( ProtocolException e1 ) {
      throw new MessageException("Could not send MultiPeerMessage", e1);
    }
  }
  
  void sendMessage(MultiPeerMessage aMultiPeerMessage, Message aMessage){
    mySendService.execute( new MessageSender(aMultiPeerMessage, aMessage) );
  }

  @Override
  public void stop() {
    mySendService.shutdownNow();
  }

  private class MessageSender implements Runnable{
    private final MultiPeerMessage myMultiPeerMessage;
    private final Message myMessage;

    public MessageSender(MultiPeerMessage aMultiPeerMessage, Message aMessage){
      myMultiPeerMessage = aMultiPeerMessage;
      myMessage = aMessage;
    }

    @Override
    public void run() {
      try {
        sendDeliveryReport( new DeliveryReport(myMultiPeerMessage, Status.IN_PROGRESS, myMessage) );
//        String theResult = getMessageProtocol().sendAndWaitForResponse( myMessage, 15, TimeUnit.SECONDS );
        String theResult = getMessageProtocol().sendMessage(myMessage );
        if(theResult.equalsIgnoreCase( STATUS_MESSAGE.DELIVERED.name() )){
          sendDeliveryReport( new DeliveryReport(myMultiPeerMessage, Status.DELIVERED, myMessage));
        } else {
          sendDeliveryReport( new DeliveryReport(myMultiPeerMessage, Status.FAILED, myMessage) );
        }
      } catch(MessageAlreadyDeliveredException e){
        sendDeliveryReport( new DeliveryReport(myMultiPeerMessage, Status.DELIVERED, myMessage) );
      } catch ( Throwable e ) {
        LOGGER.error( "Could not send message to peer '" + myMessage.getDestination().getPeerId() + "'", e );
        sendDeliveryReport( new DeliveryReport(myMultiPeerMessage, Status.FAILED, myMessage) );
      }
    }
  }


}
