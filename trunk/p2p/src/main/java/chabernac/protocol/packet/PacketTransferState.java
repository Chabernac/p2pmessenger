/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.util.List;

import chabernac.tools.Percentage;

public class PacketTransferState {
  public static enum Direction{RECEIVING, SENDING};
  public static enum State{STARTED,STOPPED,DONE,FAILED};
  
  private final String myTransferId;
  private final List<String> myPacketsInProgress;
  private final List<String> myTransferredPackets;
  private final List<String> myFailedPackets;
  private final int myNrOfPackets;
  private final Direction myDirection;
  private final State myState;
  
  public PacketTransferState ( String aTransferId , List< String > aPacketsInProgress , List< String > aTransferredPacktes ,
      List< String > aFailedPackets , int aNrOfPackets , Direction aDirection , State aState ) {
    super();
    myTransferId = aTransferId;
    myPacketsInProgress = aPacketsInProgress;
    myTransferredPackets = aTransferredPacktes;
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

  public List< String > getTransferredPackets() {
    return myTransferredPackets;
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
    return "transferid '" + myTransferId + "' direction '" + myDirection + "' packets '" + myNrOfPackets + "' state '" + myState + "' transferred '" + myTransferredPackets.size() + "' transferring '" + myPacketsInProgress.size() + "' failed '" + myFailedPackets.size() + "'";
  }

  public Percentage getPercentageComplete() {
    return new Percentage( myTransferredPackets.size(), myNrOfPackets );
  }
}
