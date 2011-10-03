/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.UnknownPeerException;
import chabernac.protocol.routing.iPeerSender;

public class PacketProtocol extends Protocol {
  private static final Logger LOGGER = Logger.getLogger(PacketProtocol.class);
  public static final String ID = "PCP";

  public static enum Input{ PACKET, RESPONSE };
  public static enum Response{ UNREACHABLE, NOK, DELIVERED, UNKNOWN_COMMAND, MAX_HOPS_REACHED, PACKET_HANDLED };

  public static final int MAX_HOP_DISTANCE = 5;

  private PacketStringConverter myConverter = new PacketStringConverter();
  private Map<String, iPacketListener> myPacketListeners = new HashMap<String, iPacketListener>();
  
  private iPacketListener myDummyListener = new DummyPacketListener();

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
    if(!myPacketListeners.containsKey(aPacket.getId())){
      LOGGER.error("No packet listener found for id '" + aPacket.getId() + "'");
    }
    
    myPacketListeners.get(aPacket.getId()).packetReceived(aPacket);
  }

  private RoutingTableEntry getEntry(String aPeerId) throws UnknownPeerException, ProtocolException{
    return getRoutingTable().getEntryForPeer( aPeerId );
  }

  private String createMessage(String aFrom, String aTo, String anId, Input aCommand, String aResponse){
    return createMessage( myConverter.toString(new Packet( aFrom, aTo, anId, Input.RESPONSE.name(), aResponse, MAX_HOP_DISTANCE, false )));
  }

  private void sendResponse(String aTo, String anId, Response aResponse){
    try{
      RoutingTableEntry theEntry = getEntry( aTo);
      if(theEntry.isReachable()) {
        getPeerSender().send( theEntry.getGateway(), createMessage( getRoutingTable().getLocalPeerId(), aTo, anId, Input.RESPONSE, aResponse.name() ) );
      }
    }catch(Exception e){
      LOGGER.error("Could not send response", e);
    }
  }
  
  private iPacketListener getPacketListener(String aListenerId){
    if(myPacketListeners.containsKey(aListenerId)) return myPacketListeners.get(aListenerId);
    return myDummyListener;
  }

  private void processCommandForLocalPeer(Packet aPacket){

    if(aPacket.getId().startsWith( Input.RESPONSE.name() )){
      if(aPacket.getListenerId().equalsIgnoreCase(Input.RESPONSE.name())){
        getPacketListener(aPacket.getListenerId()).packetDelivered(aPacket.getId());
      } else {
        getPacketListener(aPacket.getListenerId()).packetDeliveryFailed(aPacket.getId());
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
    return Response.PACKET_HANDLED.name();
  }

  @Override
  public void stop() {
  }

  public void addPacketListenr(String aListenerId, iPacketListener aPacketListener){
    myPacketListeners.put(aListenerId, aPacketListener);
  }

  public void removePacketListener(String aListenerId){
    myPacketListeners.remove( aListenerId );
  }
  
  private class DummyPacketListener implements iPacketListener{

    @Override
    public void packetDelivered(String aPacketId) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void packetDeliveryFailed(String aPacketId) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void packetReceived(Packet aPacket) {
      // TODO Auto-generated method stub
      
    }
    
  }
}
