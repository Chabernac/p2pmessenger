/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class PacketTransferVisualizerPanel extends JPanel implements iPacketTransferListener{
  private static final long serialVersionUID = -3590938754899429749L;

  private int myCellSize = 10;
  private PacketTransferState myState;

  public PacketTransferVisualizerPanel(  ) {
    super();
  }

  public void paint(Graphics g){
    if(myState == null) return;
    int theNrOfHorizontallCells = (int)Math.floor((double)getWidth() / (double)myCellSize);
    //start drawing the cells

    for(int i=0;i<myState.getNrOfPackets();i++){
      int theRow = (int)Math.floor((double)i / (double)theNrOfHorizontallCells);
      int theColumn = i % theNrOfHorizontallCells;
      int theX = theColumn * myCellSize;
      int theY = theRow * myCellSize;
      String thePacket = Integer.toString( i );
      if(myState.getTransferredPackets().contains( thePacket )){
        g.setColor( Color.green );
      } else if(myState.getFailedPackets().contains( thePacket )){
        g.setColor( Color.orange );
      } else if(myState.getPacketsInProgress().contains( thePacket )){
        g.setColor( Color.blue );
      } else {
        g.setColor( Color.gray );
      }
      g.fillRect( theX, theY, myCellSize, myCellSize);
      g.setColor( Color.black );
      g.drawRect( theX, theY , myCellSize, myCellSize );
    }
  }

  public void transferUpdated( PacketTransferState aPacketTransferState ) {
    myState = aPacketTransferState;
    repaint();
  }
}
