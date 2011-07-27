/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.text.NumberFormat;

import javax.swing.JPanel;

public class FileTransferProgressPanel extends JPanel implements iFileTransferListener {
  private static final long serialVersionUID = -2109149737877351501L;
  private final FileTransferHandler myHandler;
  final static int HEIGHT = 20;
  private final static int CELLS = 20;
  private final static int ROUNDING_RADIUS = 5;
  private final static Font FONT = new Font("Serif", Font.BOLD, 14);
  private final static NumberFormat FORMAT = NumberFormat.getInstance();
  private final static Color CELL_COLOR = new Color(140,250,140);
  
  static{
    FORMAT.setMaximumFractionDigits( 0 );
    FORMAT.setMinimumFractionDigits( 0 );
  }
  


  public FileTransferProgressPanel( FileTransferHandler aHandler ) {
    super();
    myHandler = aHandler;
    addListeners();
  }
  
  private void addListeners(){
      try {
        myHandler.addFileTransferListener( this );
      } catch ( AsyncFileTransferException e ) {
      }
  }

  @Override
  public void transferStateChanged() {
    repaint();
  }
  
  public Dimension getPreferredSize(){
    return new Dimension(super.getPreferredSize().width, HEIGHT);
  }
  
  public void paint(Graphics g){
    int theWidth = getWidth();
    
//    g.setColor( Color.lightGray );
    g.clearRect(  0, 0, getWidth(), getHeight() );

    double theCellWidth = (double)theWidth / (double)CELLS;
    
    FileTransferState theState = myHandler.getState();
    
    Percentage thePercentage = theState.getPercentageComplete();
    
    double theNrOfCompletedCells = (double)CELLS * (double)thePercentage.getDenominator() / (double)thePercentage.getDivisor();
    int theIntNrOfCompletedCells = (int)Math.floor(theNrOfCompletedCells);
    int theHeightOfCurrentCell = (int)Math.floor((theNrOfCompletedCells - theIntNrOfCompletedCells) * (HEIGHT - 2));
    
    for(int i=0;i<CELLS;i++){
      if(i<theIntNrOfCompletedCells){
        g.setColor( CELL_COLOR );
        g.fillRoundRect( (int)Math.floor(i * theCellWidth + 1), 1, (int)Math.floor(theCellWidth - 2), HEIGHT - 2, ROUNDING_RADIUS, ROUNDING_RADIUS);
      } else if(i==theIntNrOfCompletedCells){
        g.setColor( CELL_COLOR );
        g.fillRoundRect( (int)Math.floor(i * theCellWidth + 1), HEIGHT - 2 - theHeightOfCurrentCell, (int)Math.floor(theCellWidth - 2), theHeightOfCurrentCell, ROUNDING_RADIUS, ROUNDING_RADIUS);
        
      }
      g.setColor( Color.gray );
      g.drawRoundRect( (int)Math.floor( i * theCellWidth + 1), 1, (int)Math.floor(theCellWidth - 2), HEIGHT - 2, ROUNDING_RADIUS, ROUNDING_RADIUS);
    }
    
    g.setColor( Color.black );
    
    try {
      g.setFont( FONT );
      g.drawString( theState.getDirection().name() + " " + myHandler.getFile().getName() + " " + FORMAT.format( theState.getPercentageComplete().getPercentage() * 100) + " %", 10, HEIGHT - 4 );
    } catch ( AsyncFileTransferException e ) {
    }
  }
}
