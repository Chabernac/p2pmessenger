/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.File;
import java.io.IOException;

import chabernac.protocol.asyncfiletransfer.FilePacket;
import chabernac.protocol.asyncfiletransfer.FilePacketIO;

public class FileDataPacketPersister implements iDataPacketPersister {
  
  private final FilePacketIO myFilePacketIo;
  
  public FileDataPacketPersister(File aFile, int aPacketSize, int aNumberOfPackets){
    myFilePacketIo = FilePacketIO.createForWrite( aFile, null, aPacketSize, aNumberOfPackets );
  }

  @Override
  public void persistDataPacket( DataPacket aPacket ) throws IOException{
    FilePacket theFilePacket = new FilePacket( null, aPacket.getBytes(), Integer.parseInt(aPacket.getId())); 
    myFilePacketIo.writePacket( theFilePacket );
  }
}
