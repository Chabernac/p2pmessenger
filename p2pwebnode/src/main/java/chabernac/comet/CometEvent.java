package chabernac.comet;

import java.io.Serializable;

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
  public synchronized String getOutput() {
    while(myOutput == null){
      try {
        wait();
      } catch (InterruptedException e) {
      }
    }
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
