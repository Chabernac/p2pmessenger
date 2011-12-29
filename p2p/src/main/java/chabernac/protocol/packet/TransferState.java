package chabernac.protocol.packet;

import chabernac.protocol.packet.AbstractTransferState.Direction;
import chabernac.protocol.packet.AbstractTransferState.State;

public class TransferState {
  private final Direction myDirection;
  private final String myTransferId;
  private final State myState;
  private final PacketTransferState myPacketTransferState;
  
  public TransferState(String aTransferId, Direction aDirection, State aState,
      PacketTransferState aPacketTransferState) {
    super();
    myTransferId = aTransferId;
    myDirection = aDirection;
    myState = aState;
    myPacketTransferState = aPacketTransferState;
  }

  public String getTransferId() {
    return myTransferId;
  }

  public State getState() {
    return myState;
  }

  public PacketTransferState getPacketTransferState() {
    return myPacketTransferState;
  }

  public Direction getDirection() {
    return myDirection;
  }
}
