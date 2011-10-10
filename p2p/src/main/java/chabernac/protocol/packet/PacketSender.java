/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.protocol.packet.PacketTransferState.Direction;
import chabernac.protocol.packet.PacketTransferState.State;

public class PacketSender {
  private static final Logger LOGGER = Logger.getLogger(PacketSender.class);
  private static final int PACKET_TIMEOUT = 10000;

  private final iDataPacketProvider myPacketProvider;
  private final String myDestination;
  private final PacketProtocol myPacketProtocol;
  private final String myTransferId;
  private final int myOutstandingPackets;
  private boolean stop = false;
  private ScheduledExecutorService myTimeOutService = null;

  private Map<String, PacketContainer> mySendPackets = new HashMap< String, PacketContainer >();

  private List<String> mySuccessPackets = new ArrayList< String >();
  private List<String> myFailedPackets = new ArrayList< String >();

  private List<iPacketTransferListener> myListeners = new ArrayList< iPacketTransferListener >();

  private ExecutorService myListenerService = Executors.newFixedThreadPool( 1 );

  public PacketSender ( iDataPacketProvider aPacketProvider , String aDestination , PacketProtocol aPacketProtocol ,
      String aTransferId, int anOutstandingPackets ) {
    super();
    myPacketProvider = aPacketProvider;
    myDestination = aDestination;
    myPacketProtocol = aPacketProtocol;
    myTransferId = aTransferId;
    myOutstandingPackets = anOutstandingPackets;
  }

  public synchronized void start(){
    stop = false;
    myPacketProtocol.addPacketListenr( myTransferId, new PacketListener() );
    sendPacketsUntillSlotsFull();
    myTimeOutService = Executors.newScheduledThreadPool( 1 );
    myTimeOutService.scheduleAtFixedRate( new CheckPacketSendTimeout(), 5, 5, TimeUnit.SECONDS);
  }

  public synchronized void stop(){
    stop = true;
    myPacketProtocol.removePacketListener( myTransferId );
    myTimeOutService.shutdownNow();
    myTimeOutService = null;
  }

  public synchronized boolean isFinished(){
    return !myPacketProvider.hasNextPacket() && myFailedPackets.isEmpty() && mySendPackets.isEmpty();
  }

  public Set<String> getSendingPackets(){
    return Collections.unmodifiableSet( mySendPackets.keySet() );
  }

  public List<String> getSuccessPackets(){
    return Collections.unmodifiableList( mySuccessPackets );
  }

  public List<String> getFailedPackets(){
    return Collections.unmodifiableList( myFailedPackets );
  }

  public void addPacketTransferListener(iPacketTransferListener aListener){
    myListeners.add( aListener );
  }

  public void remotePacketTransferListener(iPacketTransferListener aListener){
    myListeners.remove( aListener );
  }

  private synchronized void informListeners(){
    //create copies to avoid concurrent modification exceptions
    final List<String> theSendingPackets = new ArrayList< String >(mySendPackets.keySet());
    final List<String> theSuccessPackets = new ArrayList< String >(mySuccessPackets);
    final List<String> theFailedPackets = new ArrayList< String >(myFailedPackets);
    final State theStat = isFinished() ? State.DONE : stop ? State.STOPPED : State.STARTED;
    
    myListenerService.execute( new Runnable(){
      public void run(){
        PacketTransferState theState = new PacketTransferState(
            myTransferId, 
            theSendingPackets,
            theSuccessPackets,
            theFailedPackets,
            myPacketProvider.getNrOfPackets(),
            Direction.SENDING,
            theStat);

        for(iPacketTransferListener theListener : myListeners){
          theListener.transferUpdated( theState );
        }
      }
    });
  }

  private synchronized void sendPacket(DataPacket aPacket){
    try{
      Packet thePacket = new Packet( myDestination, aPacket.getId(), myTransferId, aPacket.getBytes(), PacketProtocol.MAX_HOP_DISTANCE, true );
      mySendPackets.put(aPacket.getId(), new PacketContainer( thePacket ));
      myPacketProtocol.sendPacket( thePacket );
      informListeners();
    }catch(PacketProtocolException e){
      LOGGER.error("An error occured while sending packet with id '" + aPacket.getId() + "'", e);
      mySendPackets.remove( aPacket.getId() );
      myFailedPackets.add(aPacket.getId());
    }
  }

  private synchronized void sendPacketsUntillSlotsFull(){
    if(stop) return;

    try{
      while(mySendPackets.size() < myOutstandingPackets && myPacketProvider.hasNextPacket()){
        sendPacket( myPacketProvider.getNextPacket() );
      }

      //resend the failed packets
      while(mySendPackets.size() < myOutstandingPackets &&  myFailedPackets.size() > 0){
        sendPacket( myPacketProvider.getPacket( myFailedPackets.remove( 0 ) ));
      }
    }catch(IOException e){
      LOGGER.error("Unable to read packet", e);
    }
  }

  private class PacketListener implements iPacketListener {
    @Override
    public void packetDelivered( String aPacketId ) {
      synchronized(PacketSender.this){
        mySuccessPackets.add( aPacketId );
        mySendPackets.remove( aPacketId );
        informListeners();
        sendPacketsUntillSlotsFull();
        myPacketProvider.releasePacket( aPacketId );
      }
    }

    @Override
    public void packetDeliveryFailed( String aPacketId ) {
      synchronized(PacketSender.this){
        myFailedPackets.add(aPacketId);
        mySendPackets.remove(aPacketId);
        informListeners();
        sendPacketsUntillSlotsFull();
      }
    }

    @Override
    public void packetReceived( Packet aPacket ) {
      // TODO Auto-generated method stub
    }
  }

  private class PacketContainer{
    private final Packet myPacket;
    private final long mySendTime = System.currentTimeMillis();

    public PacketContainer ( Packet aPacket ) {
      super();
      myPacket = aPacket;
    }

    public Packet getPacket() {
      return myPacket;
    }

    public long getSendTime() {
      return mySendTime;
    }
  }

  private class CheckPacketSendTimeout implements Runnable {
    @Override
    public void run() {
      synchronized(PacketSender.this){
        //make a copy of the sendpackets to avoid concurrent modification exception
        List<PacketContainer> theSendPackets = new ArrayList< PacketContainer >(mySendPackets.values());

        for(PacketContainer theContainer : theSendPackets){
          if(System.currentTimeMillis() - theContainer.getSendTime() > PACKET_TIMEOUT){
            mySendPackets.remove( theContainer.getPacket().getId() );
            myFailedPackets.add(theContainer.getPacket().getId());
          }
        }
      }
      //TODO we must detect if something is wrong with the communication or the sender will keep trying to send all packets forever
      sendPacketsUntillSlotsFull();
    }
  }
}
