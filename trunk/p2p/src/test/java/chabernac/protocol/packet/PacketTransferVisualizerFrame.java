/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class PacketTransferVisualizerFrame extends JFrame implements iPacketTransferListener{
  private static final long serialVersionUID = 8477681824036423333L;
  private PacketTransferState myState;
  private PacketTransferVisualizerPanel myPanel = new PacketTransferVisualizerPanel( );
  
  public PacketTransferVisualizerFrame(){
    buildGUI();
  }
  
  private void buildGUI(){
    setLayout( new BorderLayout() );
    setSize( 400,400 );
    add(myPanel, BorderLayout.CENTER);
    setVisible( true );
//    try {
//      setTitle(myState.getDirection().name() + " " + myState.get
//    } catch (AsyncFileTransferException e) {
//    }
  }

  @Override
  public void transferUpdated( PacketTransferState aPacketTransferState ) {
    myState = aPacketTransferState;
    myPanel.transferUpdated( aPacketTransferState );
  }
}
