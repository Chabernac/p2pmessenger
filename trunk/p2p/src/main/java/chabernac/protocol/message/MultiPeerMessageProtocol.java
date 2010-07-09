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
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.DeliveryReport.Status;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

public class MultiPeerMessageProtocol extends Protocol{
  private static Logger LOGGER = Logger.getLogger( MultiPeerMessageProtocol.class );
  public static String ID = "MPH";

  private static enum STATUS_MESSAGE { FAILED, DELIVERED }

  private List< iDeliverReportListener > myDeliverReportListeners = new ArrayList< iDeliverReportListener >();
  private ExecutorService mySendService = Executors.newFixedThreadPool( 10 );
  private iObjectStringConverter< MultiPeerMessage > myObjectStringConverter = new Base64ObjectStringConverter< MultiPeerMessage >();
  private List<iMultiPeerMessageListener> myMessageListeners = new ArrayList< iMultiPeerMessageListener >();

  public MultiPeerMessageProtocol (  ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Multi peer message protocol";
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    try {
      MultiPeerMessage theMessage = myObjectStringConverter.getObject( anInput );
      for(iMultiPeerMessageListener theListener : myMessageListeners){
        theListener.messageReceived( theMessage );
      }
      return STATUS_MESSAGE.DELIVERED.name();
    } catch ( IOException e ) {
      return STATUS_MESSAGE.FAILED.name();
    }
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

  private RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID)).getRoutingTable();
  }

  public MultiPeerMessage sendMessage(MultiPeerMessage aMultiPeerMessage) throws MessageException{
    MultiPeerMessage theMultiPeerMessage = null;
    try {
      theMultiPeerMessage = aMultiPeerMessage.setSource( getRoutingTable().getLocalPeerId() );
      theMultiPeerMessage = theMultiPeerMessage.removeDestination( getRoutingTable().getLocalPeerId() );

      for(String theDestination : theMultiPeerMessage.getDestinations()){
        Message theMessage = new Message();
        try{
          theMessage.setDestination( getRoutingTable().getEntryForPeer( theDestination ).getPeer() );
          theMessage.setSource( getRoutingTable().getEntryForPeer(theMultiPeerMessage.getSource()).getPeer() );
          theMessage.setProtocolMessage( true );
          theMessage.setIndicators( new ArrayList< MessageIndicator >(theMultiPeerMessage.getIndicators()) );
          theMessage.setMessage( createMessage( myObjectStringConverter.toString( theMultiPeerMessage ) ) );
          mySendService.execute( new MessageSender(theMultiPeerMessage, theMessage) );
        }catch(Exception e){
          sendDeliveryReport( new DeliveryReport(theMultiPeerMessage, Status.FAILED, theMessage));
          LOGGER.error( "Sending multi peer message to '"  +  theDestination + "' failed", e );
        }
      }
      return theMultiPeerMessage;
    } catch ( ProtocolException e1 ) {
      throw new MessageException("Could not send MultiPeerMessage", e1);
    }
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
      sendDeliveryReport( new DeliveryReport(myMultiPeerMessage, Status.IN_PROGRESS, myMessage) );
      try {
        String theResult = getMessageProtocol().sendMessage( myMessage );
        if(theResult.equalsIgnoreCase( STATUS_MESSAGE.DELIVERED.name() )){
          sendDeliveryReport( new DeliveryReport(myMultiPeerMessage, Status.DELIVERED, myMessage));
        } else {
          sendDeliveryReport( new DeliveryReport(myMultiPeerMessage, Status.FAILED, myMessage) );
        }
      } catch ( Exception e ) {
        LOGGER.error( "Could not send message", e );
        sendDeliveryReport( new DeliveryReport(myMultiPeerMessage, Status.FAILED, myMessage) );
      }
    }
  }


}
