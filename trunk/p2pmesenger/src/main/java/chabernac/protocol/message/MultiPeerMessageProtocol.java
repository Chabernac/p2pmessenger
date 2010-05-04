/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.DeliveryReport.Status;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

public class MultiPeerMessageProtocol extends Protocol{
  private static Logger LOGGER = Logger.getLogger( MultiPeerMessageProtocol.class );
  public static String ID = "MPH";

  private List< iDeliverReportListener > myDeliverReportListeners = new ArrayList< iDeliverReportListener >();
  private ExecutorService mySendService = Executors.newFixedThreadPool( 10 );

  public MultiPeerMessageProtocol (  ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Multi peer message protocol";
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    // TODO Auto-generated method stub
    return null;
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

  public void sendMessage(MultiPeerMessage aMultiPeerMessage){
    for(String theDestination : aMultiPeerMessage.getDestinations()){
      Message theMessage = new Message();
      try{
        theMessage.setDestination( getRoutingTable().getEntryForPeer( theDestination ).getPeer() );
        theMessage.setSource( getRoutingTable().getEntryForPeer(aMultiPeerMessage.getSource()).getPeer() );
        theMessage.setProtocolMessage( false );
        theMessage.setIndicators( aMultiPeerMessage.getIndicators() );
        mySendService.execute( new MessageSender(theMessage) );
      }catch(Exception e){
        LOGGER.error( "Sending multi peer message failed", e );
      }
    }
  }

  @Override
  public void stop() {
  }

  private class MessageSender implements Runnable{
    private final Message myMessage;

    public MessageSender(Message aMessage){
      myMessage = aMessage;
    }

    @Override
    public void run() {
      sendDeliveryReport( new DeliveryReport(Status.IN_PROGRESS, myMessage) );
      try {
        getMessageProtocol().sendMessage( myMessage );
        sendDeliveryReport( new DeliveryReport(Status.DELIVERED, myMessage));
      } catch ( Exception e ) {
        LOGGER.error( "Could not send message", e );
        sendDeliveryReport( new DeliveryReport(Status.FAILED, myMessage) );
      }
    }
  }


}
