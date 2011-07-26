/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

public class FilePacketVisualizerPanel extends JPanel {
  private static final long serialVersionUID = -3590938754899429749L;

  private ScheduledExecutorService myService;
  
  private final FilePacketIO myIO;
  private int myCellSize = 10;

  public FilePacketVisualizerPanel( FilePacketIO aIO ) {
    super();
    myIO = aIO;
    myService = Executors.newScheduledThreadPool( 1 );
    myService.scheduleAtFixedRate( new Repainter(), 0, 200, TimeUnit.MILLISECONDS );
  }
  
  public void paint(Graphics g){
    int theNrOfHorizontallCells = (int)Math.floor((double)getWidth() / (double)myCellSize);
    //start drawing the cells
    boolean[] theWrittenPacktes = myIO.getWrittenPackets();
    
    for(int i=0;i<theWrittenPacktes.length;i++){
      int theRow = (int)Math.floor((double)i / (double)theNrOfHorizontallCells);
      int theColumn = i % theNrOfHorizontallCells;
      int theX = theColumn * myCellSize;
      int theY = theRow * myCellSize;
      g.setColor( Color.black );
      g.drawRect( theX, theY , myCellSize, myCellSize );
      if(theWrittenPacktes[i]){
        g.setColor( Color.green );
      } else {
        g.setColor( Color.orange );
      }
      g.fillRect( theX + 1, theY + 1, myCellSize - 2, myCellSize - 2 );
    }
  }
  
  public class Repainter implements Runnable {
    @Override
    public void run() {
      if(myIO.isComplete()){
        myService.shutdownNow();
      }
      repaint();
    }
  }
}
