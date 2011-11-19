/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JPanel;

public class FilePacketVisualizerPanel extends JPanel {
  private static final long serialVersionUID = -3590938754899429749L;

  private ScheduledExecutorService myService;
  
  private final FileTransferHandler myIO;
  private int myCellSize = 10;

  public FilePacketVisualizerPanel( FileTransferHandler aIO ) {
    super();
    myIO = aIO;
    myService = Executors.newScheduledThreadPool( 1 );
    addListeners();
  }
  
  private void addListeners(){
    try {
      myIO.addFileTransferListener( new Repainter() );
    } catch ( AsyncFileTransferException e ) {
    }
  }
  
  
  public void paint(Graphics g){
    int theNrOfHorizontallCells = (int)Math.floor((double)getWidth() / (double)myCellSize);
    //start drawing the cells
    boolean[] theWrittenPacktes = myIO.getState().getCompletedPackets();
    
    for(int i=0;i<theWrittenPacktes.length;i++){
      int theRow = (int)Math.floor((double)i / (double)theNrOfHorizontallCells);
      int theColumn = i % theNrOfHorizontallCells;
      int theX = theColumn * myCellSize;
      int theY = theRow * myCellSize;
      if(theWrittenPacktes[i]){
        g.setColor( Color.green );
      } else {
        g.setColor( Color.orange );
      }
      g.fillRect( theX, theY, myCellSize, myCellSize);
      g.setColor( Color.black );
      g.drawRect( theX, theY , myCellSize, myCellSize );
    }
  }
  
  public class Repainter implements iFileTransferListener {
    @Override
    public void transferStateChanged() {
      repaint();
    }
  }
}
