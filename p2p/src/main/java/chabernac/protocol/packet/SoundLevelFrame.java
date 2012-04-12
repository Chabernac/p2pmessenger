/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class SoundLevelFrame extends JFrame {
  private static final long serialVersionUID = -5106253090320467034L;
  private SoundLevelPanel mySoundLevelPanel;
  
  public SoundLevelFrame(){
    buildGUI();
  }
  
  private void buildGUI(){
    getContentPane().setLayout( new BorderLayout(20,20) );
    
    mySoundLevelPanel = new SoundLevelPanel();
    getContentPane().add( mySoundLevelPanel, BorderLayout.NORTH );
    
    setSize( 200, 100 );
  }
  
  public iSoundLevelTreshHoldProvider getSoundLevelThreshHoldProvider(){
    return mySoundLevelPanel;
  }
}
