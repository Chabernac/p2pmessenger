/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class PacketVisualizerFrame extends JFrame {
  private static final long serialVersionUID = 8477681824036423333L;
  private final AbstractTransferState myIO;

  public PacketVisualizerFrame(AbstractTransferState anIO){
    myIO = anIO;
    buildGUI();
  }

  private void buildGUI(){
    setLayout( new BorderLayout() );
    setSize( 400,400 );
    PacketTransferVisualizerPanel thePanel = new PacketTransferVisualizerPanel( );
    myIO.addPacketTransferListener( thePanel );
    add(thePanel, BorderLayout.CENTER);
    add(new TransferPanel( myIO ), BorderLayout.SOUTH);
    setVisible( true );
    setTitle(myIO.getPacketTransferState().getDirection() .name() + " " + myIO.getTransferDescription());
  }
}
