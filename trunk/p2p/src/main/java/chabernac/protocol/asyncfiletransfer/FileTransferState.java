package chabernac.protocol.asyncfiletransfer;

public class FileTransferState {
  public static enum State{NOT_STARTED, RUNNING, PAUSED, CANCELLED_OR_REMOVED, DONE};
  private final Percentage myPercentageComplete;
  private final State myState;
  
  public FileTransferState(Percentage anPercentageComplete, State anState) {
    super();
    myPercentageComplete = anPercentageComplete;
    myState = anState;
  }

  public Percentage getPercentageComplete() {
    return myPercentageComplete;
  }

  public State getState() {
    return myState;
  }
}
