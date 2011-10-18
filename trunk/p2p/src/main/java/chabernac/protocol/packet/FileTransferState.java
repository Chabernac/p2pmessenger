/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.File;
import java.io.IOException;

public class FileTransferState extends AbstractTransferState{
  public static enum Direction{SEND, RECEIVE};
  
  private final Direction myDirection;
  private File myFile;
  private final PacketProtocol myPacketProtocl;
  private final int myNrOfPackets;
  private final int myOutstandingPackets;
  private final int myPacketSize;
  
  private FileDataPacketProvider myFileDataPacketProvider = null;

  private FileTransferState ( PacketProtocol aPacketProtocol, String aTransferId, File aFile, Direction aDirection, String aRemotePeer, int aNrOfPackets, int aPacketSize, int anOutstandingPacktes ) throws IOException {
    super( aTransferId, aRemotePeer );
    myDirection = aDirection;
    myFile = aFile;
    myPacketProtocl = aPacketProtocol;
    myNrOfPackets = aNrOfPackets;
    myOutstandingPackets = anOutstandingPacktes;
    myPacketSize = aPacketSize;
    initPacketProvider();
  }
  
  private void initPacketProvider() throws IOException {
    if(myDirection == Direction.SEND){
      myFileDataPacketProvider = new FileDataPacketProvider( myFile,  myPacketSize);
    }
  }

  public static FileTransferState createForSend(PacketProtocol aPacketProtocol, String aTranferId, File aFile, String aRemotePeer, int aPacketSize, int anOutstandingPacktes) throws IOException{
    return new FileTransferState(aPacketProtocol, aTranferId, aFile, Direction.SEND, aRemotePeer, -1, aPacketSize, anOutstandingPacktes);
  }
  
  public static FileTransferState createForReceive(PacketProtocol aPacketProtocol, String aTranferId, File aFile, String aRemotePeer, int aNrOfPackets, int aPacketSize) throws IOException{
    return new FileTransferState(aPacketProtocol, aTranferId, aFile, Direction.RECEIVE, aRemotePeer, aNrOfPackets, aPacketSize, -1);
  }
  
  public void start(File aFile) throws StateChangeException{
    myFile = aFile;
    start();
  }
  
  public int getNrOfPackets(){
    if(myDirection == Direction.SEND) return myFileDataPacketProvider.getNrOfPackets();
    return myNrOfPackets;
  }
  
  public int getOutstandingPackets() {
    return myOutstandingPackets;
  }

  public int getPacketSize() {
    return myPacketSize;
  }
  
  public File getFile(){
    return myFile;
  }

  @Override
  protected iPacketTransfer createPacketTransfer() throws IOException{
    if(myDirection == Direction.SEND){
      return new PacketSender( new FileDataPacketProvider( myFile,  myPacketSize), getRemotePeer(), myPacketProtocl, getTransferId(), myOutstandingPackets );
    } else if(myDirection == Direction.RECEIVE){
      if(myFile == null) throw new IOException("File has not yet been set, cannot initiate receiver");
      return new PacketReceiver(myPacketProtocl,  getTransferId(), new FileDataPacketPersister( myFile,  myPacketSize, myNrOfPackets));
    }
    return null;
  }

  @Override
  public String getTransferDescription() {
    return myFile.toString();
  }

}
