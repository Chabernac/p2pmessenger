/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.control;

import java.io.Serializable;

public class SynchronizedEventRecording implements Serializable{
  private static final long serialVersionUID = -3978319965599650998L;
  
  private final iSynchronizedEvent myEvent;
  private final long myTime;
  
  public SynchronizedEventRecording( iSynchronizedEvent aEvent, long aTime ) {
    super();
    myEvent = aEvent;
    myTime = aTime;
  }
  
  public boolean play(long aCurrentTime){
    if(aCurrentTime >= myTime){
      myEvent.executeEvent( aCurrentTime );
      return true;
    }
    return false;
  }

  public iSynchronizedEvent getEvent() {
    return myEvent;
  }
}
