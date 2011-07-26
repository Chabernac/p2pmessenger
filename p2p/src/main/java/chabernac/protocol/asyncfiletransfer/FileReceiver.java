/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.File;
import java.io.IOException;


public class FileReceiver implements iFileIO{
  
  private final String myPeer;
  private final AsyncFileTransferProtocol myProtocol;
  private final FilePacketIO myFilePacketIO;
  private int myLastPacketReceived = -1;
  private boolean isTransferring = false;
  
  public FileReceiver(String anPeer, FilePacketIO anIo, AsyncFileTransferProtocol anProtocol) {
    super();
    myPeer = anPeer;
    myProtocol = anProtocol;
    myFilePacketIO = anIo;
  }

  @Override
  public void reset() throws AsyncFileTransferException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void start() throws AsyncFileTransferException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void stop() throws AsyncFileTransferException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void waitTillDone() throws AsyncFileTransferException {
    // TODO Auto-generated method stub
    
  }
  
  public void writePacket(FilePacket aPacket) throws IOException{
    myFilePacketIO.writePacket( aPacket );
  }

  @Override
  public double getPercentageComplete() {
    return myFilePacketIO.getPercentageWritten();
  }

  @Override
  public boolean isComplete() {
    return myFilePacketIO.isComplete();
  }

  @Override
  public boolean isTransferring() {
    return isTransferring;
  }

  public File getFile() {
    return myFilePacketIO.getFile();
  }
  
  public FilePacketIO getIO(){
    return myFilePacketIO;
  }

}
