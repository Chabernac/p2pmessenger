/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.comet;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The endpoint represents a client which has send a request to the server  
 *
 */

public class EndPoint {
  private final String myId;
  private BlockingQueue<CometEvent> myEventQueue = new ArrayBlockingQueue<CometEvent>(128);
  private boolean isClosed = false;

  public EndPoint ( String anId) {
    super();
    myId = anId;
  }

  public String getId() {
    return myId;
  }

  public boolean isClosed() {
    return isClosed;
  }

  public void setClosed( boolean aClosed ) {
    isClosed = aClosed;
  }

  public void setEvent(CometEvent anEvent) throws CometException{
    if(isClosed()) throw new CometException("This end point has been closed");

    anEvent.addExpirationListener(new iCometEventExpirationListener() {
      @Override
      public void cometEventExpired(CometEvent anEvent) {
       myEventQueue.remove(anEvent);
      }
    });
    
    try {
      myEventQueue.put(anEvent);
    } catch (InterruptedException e) {
      throw new CometException("Unable to store event", e);
    }
  }

  public CometEvent getEvent() throws CometException{
    try {
      CometEvent theEvent = myEventQueue.take();
      if(theEvent instanceof EndPointDestroyedCometEvent){
        throw new CometException("The end point was destroyed");
      }
      return theEvent;
    } catch (InterruptedException e) {
      throw new CometException("No event available", e);
    }
  }

  public boolean hasEvent(){
    return !myEventQueue.isEmpty();
  }

  public CometEvent getFirstEvent(){
    return myEventQueue.poll();
  }

  public void destroy(){
    try {
      myEventQueue.put(new EndPointDestroyedCometEvent());
    } catch (InterruptedException e) {
    }
  }
  
  public boolean containsEvent(CometEvent anEvent){
    return myEventQueue.contains(anEvent);
  }

  private class EndPointDestroyedCometEvent extends CometEvent{
    private static final long serialVersionUID = 5673639232344865324L;

    public EndPointDestroyedCometEvent() {
      super(null, null);
    }

  }
}
