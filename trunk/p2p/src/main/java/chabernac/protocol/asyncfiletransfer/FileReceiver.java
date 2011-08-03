/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import chabernac.protocol.routing.AbstractPeer;


public class FileReceiver extends AbstractFileIO{
  private static final Logger LOGGER = Logger.getLogger(FileReceiver.class);
  
  private final String myPeer;
  private final AsyncFileTransferProtocol myProtocol;
  private final FilePacketIO myFilePacketIO;
  private boolean isTransferring = false;
  
  public FileReceiver(String anPeer, FilePacketIO anIo, AsyncFileTransferProtocol anProtocol) {
    super();
    myPeer = anPeer;
    myProtocol = anProtocol;
    myFilePacketIO = anIo;
  }

  @Override
  public void reset() throws AsyncFileTransferException {
    myFilePacketIO.clearWrittenPackets();
  }

  @Override
  /**
   * in file receiver start() is actually a resume() since there can not be a filereceiver
   * withouth the file transfer has already been started, so when executing the start() method
   * we send a command to the sending peer to resume the sending of the file
   */
  public void start(){
    try{
      myProtocol.testReachable( myPeer );
      AbstractPeer theDestination = myProtocol.getRoutingTable().getEntryForPeer(myPeer).getPeer();
      myProtocol.sendMessageTo( theDestination, AsyncFileTransferProtocol.Command.RESUME_TRANSFER.name() + " " + myFilePacketIO.getId());
    }catch(Exception e){
      LOGGER.error("An error occured while sending resume command", e);
    }
  }

  @Override
  public void stop() {
    try{
      myProtocol.testReachable( myPeer );
      AbstractPeer theDestination = myProtocol.getRoutingTable().getEntryForPeer(myPeer).getPeer();
      myProtocol.sendMessageTo( theDestination, AsyncFileTransferProtocol.Command.STOP_TRANSFER.name() + " " + myFilePacketIO.getId());
    }catch(Exception e){
      LOGGER.error("An error occured while sending stop command", e);
    }
  }
  
  @Override
  public void cancel() {
    try{
      myProtocol.testReachable( myPeer );
      AbstractPeer theDestination = myProtocol.getRoutingTable().getEntryForPeer(myPeer).getPeer();
      myProtocol.sendMessageTo( theDestination, AsyncFileTransferProtocol.Command.CANCEL_TRANSFER.name() + " " + myFilePacketIO.getId());
    }catch(Exception e){
      LOGGER.error("An error occured while sending stop command", e);
    }
  }
  

  @Override
  public void waitTillDone() throws AsyncFileTransferException {
    while(!isComplete()){
      try {
        synchronized(this){
          wait();
        }
      } catch ( InterruptedException e ) {
      }
    }
  }
  
  public void writePacket(FilePacket aPacket) throws IOException {
    myFilePacketIO.writePacket( aPacket );
    //automatically close the io if the transfer if the file is complete
    if(myFilePacketIO.isComplete()){
      myFilePacketIO.close();
    }
    synchronized(this){
      notifyAll();
    }
    notifyListeners();
  }

  @Override
  public Percentage getPercentageComplete() {
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
  
  public void setTransferring(boolean isTransferring){
    this.isTransferring = isTransferring;
    notifyListeners();
  }

  public File getFile() {
    return myFilePacketIO.getFile();
  }
  
  public FilePacketIO getIO(){
    return myFilePacketIO;
  }

  @Override
  public void startAsync( ExecutorService aService ) throws AsyncFileTransferException {
    aService.execute( new Runnable(){
      public void run(){
        start();
      }
    });
  }

  @Override
  public boolean[] getCompletedPackets() {
    return myFilePacketIO.getWrittenPackets();
  }

  @Override
  public boolean isRefused() {
    return false;
  }

  @Override
  public boolean isFailed() {
    return false;
  }

  @Override
  public boolean isPending() {
    return false;
  }
}
