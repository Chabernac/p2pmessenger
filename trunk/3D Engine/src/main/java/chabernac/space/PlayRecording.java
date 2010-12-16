/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space;

import java.io.File;
import java.io.FileInputStream;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import chabernac.control.KeyCommand;
import chabernac.control.SynchronizedEventManager;

public class PlayRecording extends KeyCommand {
  private static Logger LOGGER = Logger.getLogger(PlayRecording.class);
  private final SynchronizedEventManager myManager;
  
  public PlayRecording( SynchronizedEventManager aManager ) {
    super( "Play recording" );
    myManager = aManager;
  }

  @Override
  public void keyDown() {
  }

  @Override
  public void keyPressed() {
    JFileChooser theChooser = new JFileChooser();
    int theReturn = theChooser.showOpenDialog( null );
    if(theReturn == JFileChooser.APPROVE_OPTION){
      File thefile = theChooser.getSelectedFile();
      try {
        myManager.loadRecording( new FileInputStream( thefile ) );
      } catch ( Exception e ) {
        LOGGER.error("Could not load recording", e);
      }
    }
  }

  @Override
  public void keyReleased() {
  }
}
