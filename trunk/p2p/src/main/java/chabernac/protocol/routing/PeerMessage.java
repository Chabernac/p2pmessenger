package chabernac.protocol.routing;

public class PeerMessage {
  public static enum State{INIT, SEND, OK, NOK};
  
  private final String myMessage;
  private final AbstractPeer myPeer;
  private String myResult;
  private final long myCreationTimestamp;
  private long myResponseTime;
  private State myState = State.INIT;
  
  public PeerMessage(String anMessage, AbstractPeer anPeer) {
    super();
    myMessage = anMessage;
    myPeer = anPeer;
    myCreationTimestamp = System.currentTimeMillis();
  }
  
  public String getMessage() {
    return myMessage;
  }
  public AbstractPeer getPeer() {
    return myPeer;
  }
  public String getResult() {
    return myResult;
  }
  public void setResult(String anResult) {
    myResult = anResult;
    myResponseTime = System.currentTimeMillis()- myCreationTimestamp;
    myState = State.OK;
  }
  public long getCreationTimestamp() {
    return myCreationTimestamp;
  }
  public long getResponseTime() {
    return myResponseTime;
  }

  public State getState() {
    return myState;
  }

  public void setState(State anState) {
    myState = anState;
  }
}
