/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.comet;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class EndPoint {
  private final String myId;
  private BlockingQueue<CometEvent> myEventQueue = new ArrayBlockingQueue<CometEvent>(1);
  
  public EndPoint ( String anId ) {
    super();
    myId = anId;
  }

  public String getId() {
    return myId;
  }
  
  public void setEvent(CometEvent anEvent){
    myEventQueue.add(anEvent);
  }
  
  public CometEvent getEvent(){
    return myEventQueue.poll();
  }
}
