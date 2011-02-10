/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.DeliveryReport.Status;
import chabernac.protocol.routing.IRoutingTableListener;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTableEntry;

public class FailedMessageResender {
  private static final Logger LOGGER = Logger.getLogger(FailedMessageResender.class);

  private final MultiPeerMessageProtocol myMultipeerMessageProtocol;
  private final RoutingProtocol myRoutingProtocol;

  private List<DeliveryReport> myDeliveryReports = new ArrayList<DeliveryReport>();
  
  private ScheduledExecutorService myService = null;
  private MyDeliveryReportListener myDeliveryReportListener;
  private MyRoutingTableListener myRoutingTableListener;

  public FailedMessageResender( ProtocolContainer aProtocolContainer ) throws ProtocolException {
    super();
    myMultipeerMessageProtocol = (MultiPeerMessageProtocol) aProtocolContainer.getProtocol( MultiPeerMessageProtocol.ID );
    myRoutingProtocol = (RoutingProtocol) aProtocolContainer.getProtocol( RoutingProtocol.ID );
  }
  
  public void start(){
    addListeners();
    startTimer();
  }
  
  public void stop(){
    myService.shutdownNow();
    myDeliveryReports.clear();
    if(myDeliveryReportListener != null) myMultipeerMessageProtocol.removeDeliveryReportListener( myDeliveryReportListener );
    if(myRoutingTableListener != null ) myRoutingProtocol.getRoutingTable().removeRoutingTableListener( myRoutingTableListener );
  }
  

  private void addListeners(){
    myDeliveryReportListener = new MyDeliveryReportListener();
    myMultipeerMessageProtocol.addDeliveryReportListener( myDeliveryReportListener );
    
    myRoutingTableListener = new MyRoutingTableListener();
    myRoutingProtocol.getRoutingTable().addRoutingTableListener( myRoutingTableListener );
  }
  
  private void startTimer(){
    //normally the timer is not needed, but you never know a message does not get send for some reason even if the
    //peer is indicated as being reachable, just retry every x minutes
    
    myService = Executors.newScheduledThreadPool( 1 );
    myService.scheduleAtFixedRate( new Resender(), 1, 1, TimeUnit.MINUTES );
  }

  public void resend(){
    for(Iterator<DeliveryReport> i = myDeliveryReports.iterator();i.hasNext();){
      DeliveryReport theReport = i.next();
      try{
        if(myRoutingProtocol.getRoutingTable().getEntryForPeer( theReport.getMessage().getDestination().getPeerId() ).isReachable()){
          i.remove();
          myMultipeerMessageProtocol.sendMessage( theReport.getMultiPeerMessage(), theReport.getMessage() );
        }
      }catch(Exception e){
        LOGGER.error( "Could not resend message", e );
      }
    }
  }
  
  public int getNrOfMessagesWaitingForResend(){
    return myDeliveryReports.size();
  }

  public class MyDeliveryReportListener implements iDeliverReportListener {

    @Override
    public void acceptDeliveryReport( DeliveryReport aDeliverReport ) {
      if(aDeliverReport.getDeliveryStatus() == Status.FAILED){
        myDeliveryReports.add( aDeliverReport );
      }
    }
  }

  public class MyRoutingTableListener implements IRoutingTableListener {
    @Override
    public void routingTableEntryChanged( RoutingTableEntry anEntry ) {
      resend();
    }

    @Override
    public void routingTableEntryRemoved( RoutingTableEntry anEntry ) {
      // TODO Auto-generated method stub
    }
  }
  
  private class Resender implements Runnable{
    public void run(){
      resend();
    }
  }
}
