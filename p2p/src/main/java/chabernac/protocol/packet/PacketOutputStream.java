/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class PacketOutputStream extends OutputStream implements iPacketTransfer {
  private final static int BUFFER_SIZE = 1024;
  private final static Logger LOGGER = Logger.getLogger(PacketOutputStream.class);
  
  private final PacketProtocol myPacketProtocol;
  private final String myDestination;
  private final String myTransferId;
  private final byte[] myByte = new byte[BUFFER_SIZE];
  private int myCurrentIndex;
  private int myCurrentPacket = 0;
  private boolean isStreamOpen = true;

  public PacketOutputStream( String aDestination, String aTransferId, PacketProtocol aPacketProtocol ) {
    super();
    myDestination = aDestination;
    myTransferId = aTransferId;
    myPacketProtocol = aPacketProtocol;
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
  public synchronized void write( int aB ) throws IOException {
    if(!isStreamOpen) throw new IOException("The stream has been closed");
    myByte[myCurrentIndex++] = (byte)aB;
    if(myCurrentIndex >= BUFFER_SIZE){
      flush();
    }
    
  }
  
  public void flush() throws IOException {
    try{
      byte[] theBytes = new byte[myCurrentIndex];
      System.arraycopy( myByte, 0, theBytes, 0, myCurrentIndex );
      Packet thePacket = new Packet( myDestination, Integer.toString(myCurrentPacket++), myTransferId, theBytes, PacketProtocol.MAX_HOP_DISTANCE, true );
      myPacketProtocol.sendPacket( thePacket );
      myCurrentIndex = 0;
    }catch(PacketProtocolException e){
      LOGGER.error("An error occured while sending packet with id '" + myCurrentPacket + "'", e);
    }
  }
}
