/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

public class FilePacketIO {
  private final File myFile;
  private final int myPacketSize;
  private int myNrOfPackets = 0;
  private long myFileSize;
  private RandomAccessFile myRandomAccess = null;
  private final String myId;
  private final boolean[] myWrittenPackets;
  
  public static FilePacketIO createForRead(File aFile, int aPacketSize){
    return new FilePacketIO( aFile, aPacketSize, null, -1 );
  }
  
  public static FilePacketIO createForWrite(File aFile, String anUUid, int aPacketSize, int aNumberOfPackets){
    return new FilePacketIO( aFile, aPacketSize, anUUid, aNumberOfPackets );
  }

  private FilePacketIO( File aFile, int aPacketSize, String anUUid, int aNumberOfPackets ) {
    super();
    myFile = aFile;
    myPacketSize = aPacketSize;
    myFileSize = myFile.length();
    if(aNumberOfPackets > 0){
      myNrOfPackets = aNumberOfPackets;
    } else if(myFileSize > 0){
      myNrOfPackets = (int)Math.ceil( (double)myFileSize / (double)myPacketSize );
    } 
    if(anUUid == null){
      myId = UUID.randomUUID().toString();
    } else {
      myId = anUUid;
    }
    myWrittenPackets = new boolean[myNrOfPackets];
  }
  
  private RandomAccessFile getRandomAccessFile() throws FileNotFoundException{
    if(myRandomAccess == null){
      myRandomAccess = new RandomAccessFile( myFile, "rw");
    }
    return myRandomAccess;
  }
  
  public FilePacket getPacket(int aNumber) throws IOException{
   RandomAccessFile theRandomAccess = getRandomAccessFile();
   
   long theStart = aNumber * myPacketSize;
   int theLength = (int)Math.min((long)myPacketSize, myFileSize - theStart );
   byte[] thePacket = new byte[theLength];
   theRandomAccess.seek( theStart );
//   System.out.println("read at " + theStart + " length " + theLength);
   theRandomAccess.read( thePacket, 0, theLength);
   return new FilePacket( myId, thePacket, aNumber );
  }
  
  public synchronized void writePacket(FilePacket aPacket) throws IOException{
    int theStart = aPacket.getPacket() * myPacketSize;
    RandomAccessFile theRandomAccess = getRandomAccessFile();
    theRandomAccess.seek( theStart );
//    System.out.println("write at " + theStart + " length " + aPacket.getBytes().length + " file size is " + myFile.length());
    theRandomAccess.write( aPacket.getBytes(), 0, aPacket.getBytes().length);
    myWrittenPackets[aPacket.getPacket()] = true;
  }
  
  public synchronized void close() throws IOException{
    if(myRandomAccess != null){
      myRandomAccess.close();
      myRandomAccess = null;
    }
  }
  
  public synchronized void clearWrittenPackets(){
    for(int i=0;i<myWrittenPackets.length;i++){
      myWrittenPackets[i] = false;
    }
  }
  
  public int getNrOfPackets(){
    return myNrOfPackets;
  }

  public File getFile() {
    return myFile;
  }

  public long getFileSize() {
    return myFileSize;
  }

  public int getPacketSize() {
    return myPacketSize;
  }

  public String getId() {
    return myId;
  }
  
  public Percentage getPercentageWritten(){
    int theWrittenPackets = 0;
    for(boolean isWritten : myWrittenPackets){
      if(isWritten) theWrittenPackets++;
    }
    return new Percentage( theWrittenPackets, myWrittenPackets.length );
  }

  public boolean[] getWrittenPackets() {
    return myWrittenPackets;
  }
  
  public boolean isComplete(){
    for(boolean theBlockComplet : myWrittenPackets){
      if(!theBlockComplet) return false;
    }
    return true;
  }
}
