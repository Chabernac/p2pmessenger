/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
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

  public static enum Input{ PACKET, REPSONSE };
  public static enum Response{ UNREACHABLE, NOK, DELIVERED, UNKNOWN_COMMAND, MAX_HOPS_REACHED };
  
  
  private List<iPacketListener> myPacketListeners = new ArrayList<iPacketListener>();

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
  }
  
  private RoutingTableEntry getEntry(String aPeerId) throws UnknownPeerException, ProtocolException{
    return getRoutingTable().getEntryForPeer( aPeerId );
  }
  
  private String createMessage(String aFrom, String aTo, String anId, Input aCommand, String aResponse){
    return createMessage( aFrom + ";" + aTo + ";" + anId + ";" + aCommand.name() + ";" + aResponse );
  }

  private void sendResponse(String aTo, String anId, Response aResponse){
    try{
      RoutingTableEntry theEntry = getEntry( aTo);
      if(theEntry.isReachable()) getPeerSender().send( theEntry.getGateway(), createMessage( getRoutingTable().getLocalPeerId(), aTo, anId, Input.REPSONSE, aResponse.name() ) );
    }catch(Exception e){
      LOGGER.error("Could not send response", e);
    }
  }
  
  private void processCommandForLocalPeer(String aFrom, String aTo, String anId, String aCommand, String aData){
    if(Input.PACKET.name().equalsIgnoreCase( aCommand )) {
      processPacketForLocalPeer( new Packet( aFrom, aTo, anId,  Base64.decodeBase64( aData.getBytes() )) );
    } else if(Input.REPSONSE.name().equalsIgnoreCase(aCommand)){
      
      if(Response.DELIVERED.name().equalsIgnoreCase( aData )){
        for(iPacketListener theListener : myPacketListeners) theListener.packetDelivered( anId );
      } else {
        for(iPacketListener theListener : myPacketListeners) theListener.packetDeliveryFailed( anId );
      }
    }
    
  }

  @Override
  public String handleCommand( String aSessionId, final String anInput ) {
    final String[] theParts = anInput.split( ";" );
    final String theFrom = theParts[0];
    final String theTo = theParts[1];
    final String theId = theParts[2];
    final String theCommand = theParts[3];
    final String theData = theParts[4];
    final int theHops = Integer.parseInt( theParts[5] );
    final String theNewInput = theFrom + ";" + theTo + ";" + theId + ";" + theCommand + ";" + theData + ";" + (theHops - 1);

    final boolean isSendResponse = Input.PACKET.name().equalsIgnoreCase( theCommand );
    
    getExecutorService().execute( new Runnable(){
      public void run(){
        try{
          if(getRoutingTable().getLocalPeerId().equals( theTo )){
            processCommandForLocalPeer(theFrom, theTo, theId, theCommand, theData);
          } else {
            RoutingTableEntry theDestination = getRoutingTable().getEntryForPeer( theTo );
            if(theHops - 1 <= 0) {
              sendResponse( theFrom, theId, Response.MAX_HOPS_REACHED);
            } if(isSendResponse && !theDestination.isReachable()) {
              sendResponse( theFrom, theId, Response.UNREACHABLE);
            } else {
              getPeerSender().send( theDestination.getGateway(),  theNewInput);
            }
          }    
        }catch(Exception e){
          sendResponse( theFrom, theId, Response.NOK );
        }
      }
    });
    return Response.UNKNOWN_COMMAND.name();
  }

  @Override
  public void stop() {
  }
  
  public void addPacketListenr(iPacketListener aPacketListener){
    myPacketListeners.add(aPacketListener);
  }
  
  public void removePacketListener(iPacketListener aPacketListener){
    myPacketListeners.remove( aPacketListener );
  }

}
