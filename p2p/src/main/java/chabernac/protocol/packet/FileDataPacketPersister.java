/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import chabernac.protocol.asyncfiletransfer.FilePacket;
import chabernac.protocol.asyncfiletransfer.FilePacketIO;

public class FileDataPacketPersister implements iDataPacketPersister {
//  private static final Logger LOGGER = Logger.getLogger(FileDataPacketPersister.class);
  
  private final FilePacketIO myFilePacketIo;
  
  public FileDataPacketPersister(File aFile, int aPacketSize, int aNumberOfPackets){
    myFilePacketIo = FilePacketIO.createForWrite( aFile, null, aPacketSize, aNumberOfPackets );
  }

  @Override
  public void persistDataPacket( DataPacket aPacket ) throws IOException{
    FilePacket theFilePacket = new FilePacket( null, aPacket.getBytes(), Integer.parseInt(aPacket.getId())); 
    myFilePacketIo.writePacket( theFilePacket );
  }

  @Override
  public List<String> listMissingPackets() {
    boolean[] thePacketsWritten = myFilePacketIo.getWrittenPackets();
  
    List<String> theMissingPackets =new ArrayList< String >();
    
    for(int i=0;i<thePacketsWritten.length;i++){
      if(!thePacketsWritten[i]) theMissingPackets.add(Integer.toString(i));
    }
    
    return theMissingPackets;
  }

  @Override
  public void close() throws IOException {
    myFilePacketIo.close();
  }
}
