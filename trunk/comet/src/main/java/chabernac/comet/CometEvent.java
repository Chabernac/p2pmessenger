package chabernac.comet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
  private CometException myOutputException;
  private final long myCreationTime = System.currentTimeMillis();
  private transient List<iCometEventExpirationListener> myExpirationListeners = new ArrayList<iCometEventExpirationListener>();
  private boolean isExpired = false;
  private int myPendingEvents;

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
      if(isExpired) throw new CometException("This comet event has already been expired");
      try {
        wait(aTimeout);
      } catch (InterruptedException e) {
      }
    }
    isExpired = true;
    notifyExpired();
    if(myOutputException != null) throw myOutputException;
    if(myOutput == null) throw new CometException("No output available");
    return myOutput;
  }
  public synchronized void setOutput(String anOutput) throws CometException {
    if(isExpired) throw new CometException("This comet event has already been expired");
    myOutput = anOutput;
    notifyAll();
  }

  public synchronized void setOutput(CometException anOutput) throws CometException {
    if(isExpired) throw new CometException("This comet event has already been expired");
    myOutputException = anOutput;
    notifyAll();
  }

  public String getInput() {
    return myInput;
  }
  public long getCreationTime() {
    return myCreationTime;
  }
  
  public void setPendingEvents( int aPendingEvents ) {
    myPendingEvents = aPendingEvents;
  }
  
  public int getPendingEvents() {
    return myPendingEvents;
  }
  
  private void notifyExpired(){
    if(myExpirationListeners != null){
      for(iCometEventExpirationListener theListener : myExpirationListeners){
        theListener.cometEventExpired(this);
      }
    }
  }

  public void addExpirationListener(iCometEventExpirationListener aListener){
    myExpirationListeners.add(aListener);
  }

  public boolean isExpired() {
    return isExpired;
  }
}