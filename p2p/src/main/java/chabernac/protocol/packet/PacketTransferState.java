/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.util.List;

public class PacketTransferState {
  public static enum Direction{RECEIVING, SENDING};
  public static enum State{STARTED,STOPPED,DONE,FAILED};
  
  private final String myTransferId;
  private final List<String> myPacketsInProgress;
  private final List<String> mySendPackets;
  private final List<String> myFailedPackets;
  private final int myNrOfPackets;
  private final Direction myDirection;
  private final State myState;
  
  public PacketTransferState ( String aTransferId , List< String > aPacketsInProgress , List< String > aSendPackets ,
      List< String > aFailedPackets , int aNrOfPackets , Direction aDirection , State aState ) {
    super();
    myTransferId = aTransferId;
    myPacketsInProgress = aPacketsInProgress;
    mySendPackets = aSendPackets;
    myFailedPackets = aFailedPackets;
    myNrOfPackets = aNrOfPackets;
    myDirection = aDirection;
    myState = aState;
  }

  public String getTransferId() {
    return myTransferId;
  }

  public List< String > getPacketsInProgress() {
    return myPacketsInProgress;
  }

  public List< String > getSendPackets() {
    return mySendPackets;
  }

  public List< String > getFailedPackets() {
    return myFailedPackets;
  }

  public int getNrOfPackets() {
    return myNrOfPackets;
  }

  public Direction getDirection() {
    return myDirection;
  }

  public State getState() {
    return myState;
  }
  
  public String toString(){
    return "transferid '" + myTransferId + "' packets '" + myNrOfPackets + "' state '" + myState + "' send '" + mySendPackets.size() + "' sending '" + myPacketsInProgress.size() + "' failed '" + myFailedPackets.size() + "'";
  }
}
