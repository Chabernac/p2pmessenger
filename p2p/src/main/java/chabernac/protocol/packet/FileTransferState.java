/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.io.File;
import java.io.IOException;

public class FileTransferState extends AbstractTransferState{
  public static enum Direction{SEND, RECEIVE};
  
  private final int PACKETS_SIZE = 8096;
  private final int OUTSTANDING_PACKETS = 5;
  
  private final Direction myDirection;
  private final File myFile;
  private final PacketProtocol myPacketProtocl;
  private final int myNrOfPackets;

  private FileTransferState ( PacketProtocol aPacketProtocol, String aTransferId, File aFile, Direction aDirection, String aRemotePeer, int aNrOfPackets ) {
    super( aTransferId, aRemotePeer );
    myDirection = aDirection;
    myFile = aFile;
    myPacketProtocl = aPacketProtocol;
    myNrOfPackets = aNrOfPackets;
  }
  
  public static FileTransferState createForSend(PacketProtocol aPacketProtocol, String aTranferId, File aFile, String aRemotePeer){
    return new FileTransferState(aPacketProtocol, aTranferId, aFile, Direction.SEND, aRemotePeer, -1);
  }
  
  public static FileTransferState createForReceive(PacketProtocol aPacketProtocol, String aTranferId, File aFile, String aRemotePeer, int aNrOfPackets){
    return new FileTransferState(aPacketProtocol, aTranferId, aFile, Direction.RECEIVE, aRemotePeer, aNrOfPackets);
  }

  @Override
  protected iPacketTransfer createPacketTransfer() throws IOException{
    if(myDirection == Direction.SEND){
      return new PacketSender( new FileDataPacketProvider( myFile,  PACKETS_SIZE), getRemotePeer(), myPacketProtocl, getTransferId(), OUTSTANDING_PACKETS );
    } else if(myDirection == Direction.RECEIVE){
      return new PacketReceiver(myPacketProtocl,  getTransferId(), new FileDataPacketPersister( myFile,  PACKETS_SIZE, myNrOfPackets));
    }
    return null;
  }

}
