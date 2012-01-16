/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.newcomet;

import java.util.ArrayList;
import java.util.List;

import chabernac.comet.CometEvent;
import chabernac.comet.iCometEventExpirationListener;

public class EndPoint2 {
  private final String myId;
  
  private List< CometEvent > myEvents = new ArrayList< CometEvent >();
  
  private Object LOCK = new Object();
  
  private boolean isActive = false;
  
  private Object myOwner = null;
  
  public EndPoint2 ( String aId ) {
    super();
    myId = aId;
  }
  
  public String getId() {
    return myId;
  }
  
  public boolean isActive() {
    return isActive;
  }

  public void setActive( boolean aActive ) {
    isActive = aActive;
  }

  public void setOwner(Object aOwner) {
    synchronized(LOCK){
      myOwner = aOwner;
      LOCK.notifyAll();
    }
  }

  public void addCometEvent(CometEvent anEvent){
    anEvent.addExpirationListener(new iCometEventExpirationListener() {
      @Override
      public void cometEventExpired(CometEvent anEvent) {
       myEvents.remove(anEvent);
      }
    });

    myEvents.add(anEvent);
    synchronized(LOCK) { LOCK.notifyAll(); }
  }
  
  public void waitForEvent(Object anOwner) throws InterruptedException{
    setOwner(anOwner);
    while(!hasEvents()){
      synchronized ( LOCK ) { 
        LOCK.wait();  
        if(myOwner != anOwner) throw new InterruptedException("Waiting interrupted because endpoint owner has changed");
      }
    }
  }
  
  public boolean hasEvents(){
    return !myEvents.isEmpty();
  }
  
  public synchronized CometEvent getFirstEvent(){
    if(hasEvents()){
      return myEvents.remove(0);
    }
    return null;
  }
  
  public boolean containsEventWithId(String anId){
    for(CometEvent theEvent : myEvents){
      if(theEvent.getId().equals( anId )) return true;
    }
    return false;
  }
  
  public String toString(){
    return "<Endpoint id='" + myId + "' isactive='" + isActive + "' events='" + myEvents.size() + "'/>";
  }

  public boolean containsEvent(CometEvent anEvent) {
    return myEvents.contains(anEvent);
  }
}
