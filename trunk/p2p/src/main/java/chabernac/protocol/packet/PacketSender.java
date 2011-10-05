/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

public class PacketSender {
  private final iPacketProvider myPacketProvider;
  private final String myDestination;
  private final PacketProtocol myPacketProtocol;
  private final String myTransferId;
  
  
  public PacketSender ( iPacketProvider myPacketProvider , String myDestination , PacketProtocol myPacketProtocol ,
      String myTransferId ) {
    super();
    this.myPacketProvider = myPacketProvider;
    this.myDestination = myDestination;
    this.myPacketProtocol = myPacketProtocol;
    this.myTransferId = myTransferId;
    myPacketProtocol.addPacketListenr( myTransferId, new PacketListener() );
  }


  public void start(){
    
  }
  
  private class PacketListener implements iPacketListener {

    @Override
    public void packetDelivered( String aPacketId ) {
      // TODO Auto-generated method stub

    }

    @Override
    public void packetDeliveryFailed( String aPacketId ) {
      // TODO Auto-generated method stub

    }

    @Override
    public void packetReceived( Packet aPacket ) {
      // TODO Auto-generated method stub

    }

  }
}
