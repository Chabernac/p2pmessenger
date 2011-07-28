package chabernac.protocol.asyncfiletransfer;

public class FileTransferState {
  public static enum State{NOT_STARTED, RUNNING, PAUSED, CANCELLED_OR_REMOVED, DONE};
  public static enum Direction{RECEIVING, SENDING, UNKNOWN};
  private final Percentage myPercentageComplete;
  private final State myState;
  private final boolean[] myCompletedPackets;
  private final Direction myDirection;
  
  public FileTransferState(Percentage anPercentageComplete, State anState, Direction aDirection, boolean[] aCompletedPackets) {
    super();
    myPercentageComplete = anPercentageComplete;
    myCompletedPackets = aCompletedPackets;
    myDirection = aDirection;
    myState = anState;
  }

  public Percentage getPercentageComplete() {
    return myPercentageComplete;
  }

  public State getState() {
    return myState;
  }

  public boolean[] getCompletedPackets() {
    return myCompletedPackets;
  }

  public Direction getDirection() {
    return myDirection;
  }
  
  public String toString(){
    return myDirection.name() + " " + myState.name() + " " + myPercentageComplete.getDenominator() + " / " + myPercentageComplete.getDivisor();
  }
}
