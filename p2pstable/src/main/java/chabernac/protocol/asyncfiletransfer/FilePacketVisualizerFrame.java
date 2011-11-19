/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class FilePacketVisualizerFrame extends JFrame {
  private static final long serialVersionUID = 8477681824036423333L;
  private final FileTransferHandler myIO;
  
  public FilePacketVisualizerFrame(FileTransferHandler anIO){
    myIO = anIO;
    buildGUI();
  }
  
  private void buildGUI(){
    setLayout( new BorderLayout() );
    setSize( 400,400 );
    add(new FilePacketVisualizerPanel( myIO ), BorderLayout.CENTER);
    add(new FileTransferPanel( myIO ), BorderLayout.SOUTH);
    setVisible( true );
    try {
      setTitle(myIO.getState().getDirection().name() + " " + myIO.getFile().getName());
    } catch (AsyncFileTransferException e) {
    }
  }
}
