/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.util.List;

import chabernac.control.Recording;
import chabernac.control.SynchronizedEventRecording;
import chabernac.control.iRecordingRestorer;
import chabernac.space.Camera;
import chabernac.space.CameraMoveCommand;

public class RecordingRestorer implements iRecordingRestorer{
  private final Camera myCamera;

  public RecordingRestorer( Camera aCamera ) {
    super();
    myCamera = aCamera;
  }

  @Override
  public void restoreRecording( Recording aRecording ) {
    List<SynchronizedEventRecording> theRecordings = aRecording.getRecording();
    for(SynchronizedEventRecording theRecording : theRecordings){
      if(theRecording.getEvent() instanceof CameraMoveCommand){
        ((CameraMoveCommand)theRecording.getEvent()).setCamera( myCamera );
      }
    }
    
  }
  
  
}
