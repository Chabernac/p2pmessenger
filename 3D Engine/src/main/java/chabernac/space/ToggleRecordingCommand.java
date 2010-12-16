/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import chabernac.control.KeyCommand;
import chabernac.control.SynchronizedEventManager;

public class ToggleRecordingCommand extends KeyCommand {
  private final SynchronizedEventManager myManager;
  
  private static final Logger LOGGER = Logger.getLogger(ToggleRecordingCommand.class);

  public ToggleRecordingCommand( SynchronizedEventManager aManager ) {
    super( "Record" );
    myManager = aManager;
  }

  @Override
  public void keyDown() {
    
  }

  @Override
  public void keyPressed() {
    myManager.setRecording( !myManager.isRecording() );
    if(!myManager.isRecording()){
      JFileChooser theChooser = new JFileChooser();
      int theReturn = theChooser.showOpenDialog( null );
      if(theReturn == JFileChooser.APPROVE_OPTION){
        File thefile = theChooser.getSelectedFile();
        try {
          myManager.saveRecording( new FileOutputStream( thefile ) );
        } catch ( Exception e ) {
          LOGGER.error("Could not save recording", e);
        }
      }
    }
  }

  @Override
  public void keyReleased() {
  }
}
