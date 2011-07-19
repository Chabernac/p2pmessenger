package chabernac.protocol.asyncfiletransfer;

public class FileTransferState {
  public static enum State{NOT_STARTED, RUNNING, PAUSED, CANCELLED_OR_REMOVED, DONE};
  private final double myPercentageComplete;
  private final State myState;
  
  public FileTransferState(double anPercentageComplete, State anState) {
    super();
    myPercentageComplete = anPercentageComplete;
    myState = anState;
  }

  public double getPercentageComplete() {
    return myPercentageComplete;
  }

  public State getState() {
    return myState;
  }
}
