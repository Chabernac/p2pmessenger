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
import chabernac.utils.NamedRunnable;

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

  private RoutingTableEntry getEntry(String aPeerId) throws UnknownPeerException, ProtocolException{
    return getRoutingTable().getEntryForPeer( aPeerId );
  }

  private String createMessage(String aFrom, String aTo, String anId, String aListenerId, Input aCommand, String aResponse){
    return createMessage( myConverter.toString(new Packet( aFrom, aTo, anId, aListenerId, aCommand.name() + aResponse, MAX_HOP_DISTANCE, false )));
  }

  private void sendResponse(String aTo, String anId, String aListenerId, Response aResponse){
    try{
      RoutingTableEntry theEntry = getEntry( aTo);
      if(theEntry.isReachable()) {
        getPeerSender().send( theEntry.getGateway(), createMessage( getRoutingTable().getLocalPeerId(), aTo, anId, aListenerId, Input.RESPONSE, aResponse.name() ) );
      }
    }catch(Exception e){
      LOGGER.error("Could not send response", e);
    }
  }
  
  private iPacketListener getPacketListener(String aListenerId){
    if(myPacketListeners.containsKey(aListenerId)) return myPacketListeners.get(aListenerId);
    LOGGER.error( "No packet listener for listener id '" + aListenerId + "'" );
    return myDummyListener;
  }

  private void processCommandForLocalPeer(Packet aPacket){

    if(aPacket.getBytesAsString().startsWith( Input.RESPONSE.name() )){
      String theResponse = aPacket.getBytesAsString().substring( Input.RESPONSE.name().length() );
      if(theResponse.equalsIgnoreCase(Response.DELIVERED.name())){
        getPacketListener(aPacket.getListenerId()).packetDelivered(aPacket.getId());
      } else {
        getPacketListener(aPacket.getListenerId()).packetDeliveryFailed(aPacket.getId());
      }
    } else  {
      getPacketListener( aPacket.getListenerId()).packetReceived(aPacket);
      if(aPacket.isSendResponse()){
        sendResponse( aPacket.getFrom(), aPacket.getId(), aPacket.getListenerId(), Response.DELIVERED );
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

    getExecutorService().execute( new NamedRunnable("Handling packet") {
      public void doRun(){
        try{
          if(getRoutingTable().getLocalPeerId().equals( thePacket.getTo() )){
            processCommandForLocalPeer(thePacket);
          } else {
            RoutingTableEntry theDestination = getRoutingTable().getEntryForPeer( thePacket.getTo() );
            if(thePacket.isSendResponse() && thePacket.getHopDistance() <= 0) {
              sendResponse( thePacket.getFrom(), thePacket.getId(), thePacket.getListenerId(), Response.MAX_HOPS_REACHED);
            } if(thePacket.isSendResponse() && !theDestination.isReachable()) {
              sendResponse( thePacket.getFrom(), thePacket.getId(), thePacket.getListenerId(), Response.UNREACHABLE);
            } else {
              getPeerSender().send( theDestination.getGateway(),  createMessage( myConverter.toString( thePacket )));
            }
          }    
        }catch(Exception e){
          LOGGER.error("An error occured while processing packet", e);
          if(thePacket.isSendResponse()){
            sendResponse( thePacket.getFrom(), thePacket.getId(), thePacket.getListenerId(), Response.NOK );
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
    public void packetDelivered(String aPacketId) {}

    @Override
    public void packetDeliveryFailed(String aPacketId) {}

    @Override
    public void packetReceived(Packet aPacket) {}
  }
}
