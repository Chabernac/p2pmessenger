/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import chabernac.protocol.packet.PacketTransferState.Direction;
import chabernac.protocol.packet.PacketTransferState.State;


public class PacketReceiver extends AbstractPacketTransfer{
  private static Logger LOGGER = Logger.getLogger(PacketReceiver.class);
  
  private final iDataPacketPersister myDataPacketPersister;
  private final String myTransferId;
  private final PacketProtocol myPacketProtocol;
  
  private List<String> myReceivedPackets = new ArrayList< String >();
  
  private boolean stop = false;

  public PacketReceiver(PacketProtocol aPacketProtocol, String aTransferId, iDataPacketPersister aDataPacketPersister){
    myDataPacketPersister = aDataPacketPersister;
    myTransferId = aTransferId;
    myPacketProtocol = aPacketProtocol;
    myPacketProtocol.addPacketListenr( myTransferId,  new PacketListener() );
  }

  public void start(){
    stop = false;
  }

  public void stop(){
    stop = true;
  }
  
  public void done(){
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
        myReceivedPackets.add(aPacket.getId());
        if(myDataPacketPersister.isComplete()) done();
        notifyListeners();
      }catch(IOException e){
        LOGGER.error( "an error occured while persisting data packet", e );
      }
    }

  }

  @Override
  public void waitUntillDone() {
    // TODO implemenet waitUntillDone();
  }

  @Override
  public synchronized PacketTransferState getTransferState() {
    //create copies to avoid concurrent modification exceptions
    //at the receiver side we can only know which packets that where successfully received
    final List<String> theSuccessPackets = new ArrayList< String >(myReceivedPackets);
    
    final State theStat = myDataPacketPersister.isComplete() ? State.DONE : stop ? State.STOPPED : State.STARTED;

    return new PacketTransferState(
        myTransferId, 
        new ArrayList< String >(),
        theSuccessPackets,
        new ArrayList< String >(),
        myDataPacketPersister.getNrOfPackets(),
        Direction.RECEIVING,
        theStat);
  }
}
