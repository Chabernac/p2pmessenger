/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolFactory;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.UnknownPeerException;
import chabernac.protocol.routing.iPeerSender;

public class PacketProtocol extends Protocol {
  private static final Logger LOGGER = Logger.getLogger(PacketProtocol.class);
  public static final String ID = "PCP";

  public static enum Input{ PACKET, REPSONSE };
  public static enum Response{ UNREACHABLE, NOK, DELIVERED, UNKNOWN_COMMAND, MAX_HOPS_REACHED };

  public static final int MAX_HOP_DISTANCE = 5;


  private PacketStringConverter myConverter = new PacketStringConverter();
  private List<iPacketListener> myPacketListeners = new ArrayList<iPacketListener>();
  
  private PacketProtocolFactory myProtocolFactory = new PacketProtocolFactory(this);

  public PacketProtocol(  ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Packet protocol";
  }

  public RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }

  public iPeerSender getPeerSender() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getPeerSender();
  }


  private void processPacketForLocalPeer(Packet aPacket){
    for(iPacketListener theListener : myPacketListeners){
      theListener.packetReceived( aPacket );
    }
    
    try {
      AbstractPacketProtocol thePacketProtocol = myProtocolFactory.getProtocol( aPacket.getId() );
      thePacketProtocol.handlePacket( aPacket );
    } catch ( PacketProtocolException e ) {
      LOGGER.error("Could not process packet ", e);
    }
  }

  private RoutingTableEntry getEntry(String aPeerId) throws UnknownPeerException, ProtocolException{
    return getRoutingTable().getEntryForPeer( aPeerId );
  }

  private String createMessage(String aFrom, String aTo, String anId, Input aCommand, String aResponse){
    return createMessage( myConverter.toString(new Packet( aFrom, aTo, Input.REPSONSE.name() + anId, aResponse, MAX_HOP_DISTANCE, false )));
  }

  private void sendResponse(String aTo, String anId, Response aResponse){
    try{
      RoutingTableEntry theEntry = getEntry( aTo);
      if(theEntry.isReachable()) {
        getPeerSender().send( theEntry.getGateway(), createMessage( getRoutingTable().getLocalPeerId(), aTo, anId, Input.REPSONSE, aResponse.name() ) );
      }
    }catch(Exception e){
      LOGGER.error("Could not send response", e);
    }
  }

  private void processCommandForLocalPeer(Packet aPacket){

    if(aPacket.getId().startsWith( Input.REPSONSE.name() )){
      String thePacketId = aPacket.getId().substring( Input.REPSONSE.name().length() );
      Response theResponse = Response.valueOf( aPacket.getBytesAsString() );
      if(Response.DELIVERED == theResponse){
        for(iPacketListener theListener : myPacketListeners) theListener.packetDelivered( thePacketId );
      } else {
        for(iPacketListener theListener : myPacketListeners) theListener.packetDeliveryFailed( thePacketId );
      }
    } else  {
      processPacketForLocalPeer( aPacket );
      if(aPacket.isSendResponse()){
        sendResponse( aPacket.getFrom(), aPacket.getId(), Response.DELIVERED );
      }
    }
  }

  public void sendPacket(Packet aPacket) throws PacketProtocolException{
    try {
      handleCommand( null, myConverter.toString(aPacket.setFrom( getRoutingTable().getLocalPeerId() ) ));
    } catch ( ProtocolException e ) {
      throw new PacketProtocolException("Could not get local peer id", e);
    }
  }

  @Override
  public String handleCommand( String aSessionId, final String anInput ) {
    final Packet thePacket = myConverter.getObject( anInput ).decreaseHopDistance();

    getExecutorService().execute( new Runnable(){
      public void run(){
        try{
          if(getRoutingTable().getLocalPeerId().equals( thePacket.getTo() )){
            processCommandForLocalPeer(thePacket);
          } else {
            RoutingTableEntry theDestination = getRoutingTable().getEntryForPeer( thePacket.getTo() );
            if(thePacket.isSendResponse() && thePacket.getHopDistance() <= 0) {
              sendResponse( thePacket.getFrom(), thePacket.getId(), Response.MAX_HOPS_REACHED);
            } if(thePacket.isSendResponse() && !theDestination.isReachable()) {
              sendResponse( thePacket.getFrom(), thePacket.getId(), Response.UNREACHABLE);
            } else {
              getPeerSender().send( theDestination.getGateway(),  createMessage( myConverter.toString( thePacket )));
            }
          }    
        }catch(Exception e){
          LOGGER.error("An error occured while processing packet", e);
          if(thePacket.isSendResponse()){
            sendResponse( thePacket.getFrom(), thePacket.getId(), Response.NOK );
          }
        }
      }
    });
    return Response.UNKNOWN_COMMAND.name();
  }

  @Override
  public void stop() {
    for(AbstractPacketProtocol theProtocol : myProtocolFactory.getPacketProtocols()){
      theProtocol.stop();
    }
  }

  public void addPacketListenr(iPacketListener aPacketListener){
    myPacketListeners.add(aPacketListener);
  }

  public void removePacketListener(iPacketListener aPacketListener){
    myPacketListeners.remove( aPacketListener );
  }
  
  public PacketProtocolFactory getPacketProtocolFactory(){
    return myProtocolFactory;
  }
}
