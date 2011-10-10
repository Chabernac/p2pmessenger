/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import chabernac.protocol.packet.PacketTransferState.Direction;
import chabernac.protocol.packet.PacketTransferState.State;


public class PacketReceiver {
  private static Logger LOGGER = Logger.getLogger(PacketReceiver.class);
  
  private final iDataPacketPersister myDataPacketPersister;
  private final String myTransferId;
  private final PacketProtocol myPacketProtocol;
  
  private List<iPacketTransferListener> myListeners = new ArrayList< iPacketTransferListener >();
  private ExecutorService myListenerService = Executors.newFixedThreadPool( 1 );
  private List<String> myReceivedPackets = new ArrayList< String >();
  
  private boolean stop = false;

  public PacketReceiver(PacketProtocol aPacketProtocol, String aTransferId, iDataPacketPersister aDataPacketPersister){
    myDataPacketPersister = aDataPacketPersister;
    myTransferId = aTransferId;
    myPacketProtocol = aPacketProtocol;
  }

  public void start(){
    stop = false;
    myPacketProtocol.addPacketListenr( myTransferId,  new PacketListener() ); 
  }

  public void stop(){
    stop = true;
    myPacketProtocol.removePacketListener( myTransferId );
  }
  
  public void addPacketTransferListener(iPacketTransferListener aListener){
    myListeners.add( aListener );
  }

  public void remotePacketTransferListener(iPacketTransferListener aListener){
    myListeners.remove( aListener );
  }
  
  private synchronized void informListeners(){
    //create copies to avoid concurrent modification exceptions
    //at the receiver side we can only know which packets that where successfully received
    final List<String> theSuccessPackets = new ArrayList< String >(myReceivedPackets);
    
    final State theStat = myDataPacketPersister.isComplete() ? State.DONE : stop ? State.STOPPED : State.STARTED;
    
    myListenerService.execute( new Runnable(){
      public void run(){
        PacketTransferState theState = new PacketTransferState(
            myTransferId, 
            new ArrayList< String >(),
            theSuccessPackets,
            new ArrayList< String >(),
            myDataPacketPersister.getNrOfPackets(),
            Direction.RECEIVING,
            theStat);

        for(iPacketTransferListener theListener : myListeners){
          theListener.transferUpdated( theState );
        }
      }
    });
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
        informListeners();
      }catch(IOException e){
        LOGGER.error( "an error occured while persisting data packet", e );
      }
    }

  }
}
