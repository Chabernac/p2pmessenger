/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.InputStream;

public class InputStreamDataPacketProvider implements iDataPacketProvider {
  
  private final InputStream myInputStream;
  private final int myPacketSize;
  
  public InputStreamDataPacketProvider ( InputStream anInputStream, int aPacketSize ) {
    super();
    myInputStream = anInputStream;
    myPacketSize = aPacketSize;
  }

  @Override
  public DataPacket getNextPacket() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public DataPacket getPacket( String aPacketId ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasNextPacket() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public int getNrOfPackets() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void releasePacket( String aPacketId ) {
    // TODO Auto-generated method stub
    
  }

}
