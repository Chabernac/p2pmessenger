/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PacketInputStream extends InputStream implements iPacketTransfer {
  private final PacketProtocol myPacketProtocol;
  private final String myTransferId;
  private BlockingQueue<byte[]> myBufferedPackets = new LinkedBlockingQueue<byte[]>();
  private byte[] myCurrentPacket = null;
  private int myCurrentIndex;
  private boolean isStreamOpen = true;
  
  public PacketInputStream(PacketProtocol aPacketProtocol, String aTransferId){
    myPacketProtocol = aPacketProtocol;
    myTransferId = aTransferId;
    myPacketProtocol.addPacketListenr( myTransferId,  new PacketListener() );
  }

  @Override
  public void addPacketTransferListener( iPacketTransferListener aTransferListener ) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removePacketTransferListener( iPacketTransferListener aTransferListener ) {
    // TODO Auto-generated method stub

  }

  @Override
  public void start() {
    isStreamOpen = true;

  }

  @Override
  public void stop() {
    isStreamOpen = false;

  }

  @Override
  public void done() {

  }

  @Override
  public void waitUntillDone() {
    // TODO Auto-generated method stub

  }

  @Override
  public PacketTransferState getTransferState() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int read() throws IOException {
    if(!isStreamOpen) throw new IOException("The stream has been closed");
    if(myCurrentPacket == null || myCurrentIndex >= myCurrentPacket.length){
      try {
        myCurrentPacket = myBufferedPackets.take();
      } catch ( InterruptedException e ) {
        throw new IOException("No packet available");
      }
    }
    return myCurrentPacket[myCurrentIndex++];
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
      myBufferedPackets.add( aPacket.getBytes() );
    }
  }
}
