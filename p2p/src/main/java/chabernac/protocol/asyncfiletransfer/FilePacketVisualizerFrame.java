/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class FilePacketVisualizerFrame extends JFrame {
  private final FilePacketIO myIO;
  
  public FilePacketVisualizerFrame(FilePacketIO anIO){
    myIO = anIO;
    buildGUI();
  }
  
  private void buildGUI(){
    setLayout( new BorderLayout() );
    setSize( 400,400 );
    add(new FilePacketVisualizerPanel( myIO ));
    setVisible( true );
  }
}
