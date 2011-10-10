/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.IOException;

import org.apache.log4j.Logger;


public class PacketReceiver {
  private static Logger LOGGER = Logger.getLogger(PacketReceiver.class);
  
  private final iDataPacketPersister myDataPacketPersister;
  private final String myTransferId;
  private final PacketProtocol myPacketProtocol;

  public PacketReceiver(PacketProtocol aPacketProtocol, String aTransferId, iDataPacketPersister aDataPacketPersister){
    myDataPacketPersister = aDataPacketPersister;
    myTransferId = aTransferId;
    myPacketProtocol = aPacketProtocol;
  }

  public void start(){
    myPacketProtocol.addPacketListenr( myTransferId,  new PacketListener() ); 
  }

  public void stop(){
    myPacketProtocol.removePacketListener( myTransferId );
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
      DataPacket theDataPacket = new DataPacket( aPacket.getId(), aPacket.getBytes() );
      try{
        myDataPacketPersister.persistDataPacket( theDataPacket );
      }catch(IOException e){
        LOGGER.error( "an error occured while persisting data packet", e );
      }
    }

  }
}
