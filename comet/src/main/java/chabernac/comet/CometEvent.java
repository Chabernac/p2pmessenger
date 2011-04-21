package chabernac.comet;

import java.io.Serializable;

/**
 * a comet event is an event which is being passed from server to client by using the long polling technique
 * this involves the client sending a request to the server, the server blocks untill an event is available for this 
 * client (endpoint).  When an event is available for this endpoint it is streamed to client.  The client will respond
 * to the event in a next request. 
 */

public class CometEvent implements Serializable{
  private static final long serialVersionUID = 711823226739193623L;
  private final String myId;
  private final String myInput;
  private String myOutput;
  
  public CometEvent(String anId, String anInput) {
    super();
    myId = anId;
    myInput = anInput;
  }
  public String getId() {
    return myId;
  }
  public synchronized String getOutput(long aTimeout) throws CometException {
    if(myOutput == null){
      try {
        wait(aTimeout);
      } catch (InterruptedException e) {
      }
    }
    if(myOutput == null) throw new CometException("No output available");
    return myOutput;
  }
  public synchronized void setOutput(String anOutput) {
    myOutput = anOutput;
    notifyAll();
  }
  public String getInput() {
    return myInput;
  }
}
