/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recording implements Serializable{
  private static final long serialVersionUID = -2434860083253300526L;
  private long myOffsset;
  private final List<SynchronizedEventRecording> myRecording = new ArrayList<SynchronizedEventRecording>();
  
  public Recording(long anOffset){
    myOffsset = anOffset;
  }
  
  public void add(long aTime, iSynchronizedEvent anEvent){
    if(anEvent.isRecordable()){
      myRecording.add(new SynchronizedEventRecording(anEvent, aTime - myOffsset));
    }
  }
  
  public void play(long aCurrentTime){
    while(myRecording.size() > 0 && myRecording.get( 0 ).play( aCurrentTime - myOffsset)){
      myRecording.remove( 0 );
    }
  }
  
  public int size(){
    return myRecording.size();
  }

  public List<SynchronizedEventRecording> getRecording() {
    return myRecording;
  }

  public long getOffsset() {
    return myOffsset;
  }

  public void setOffsset( long aOffsset ) {
    myOffsset = aOffsset;
  }
}
