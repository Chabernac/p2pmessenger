/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.File;
import java.io.IOException;

import chabernac.protocol.asyncfiletransfer.FilePacket;
import chabernac.protocol.asyncfiletransfer.FilePacketIO;

public class FileDataPacketProvider implements iDataPacketProvider {
  
  private final FilePacketIO myFilePacketIo;
  private int myCurrentPacket = 0;
  
  public FileDataPacketProvider ( File aFile, int aPacketSize ) throws IOException {
    super();
    myFilePacketIo = FilePacketIO.createForRead( aFile, aPacketSize );
  }

  @Override
  public DataPacket getNextPacket() throws IOException{
    DataPacket thePacket = getPacket( Integer.toString( myCurrentPacket ));
    myCurrentPacket++;
    return thePacket;
  }

  @Override
  public int getNrOfPackets() {
   return myFilePacketIo.getNrOfPackets();
  }

  @Override
  public DataPacket getPacket( String aPacketId ) throws IOException {
    FilePacket theFilePacket = myFilePacketIo.getPacket( Integer.parseInt(aPacketId) );
    return new DataPacket( Integer.toString(theFilePacket.getPacket()), theFilePacket.getBytes() );
  }

  @Override
  public boolean hasNextPacket() {
    return myCurrentPacket < myFilePacketIo.getNrOfPackets();
  }

  @Override
  public void releasePacket( String aPacketId ) {
    // TODO Auto-generated method stub

  }

}
