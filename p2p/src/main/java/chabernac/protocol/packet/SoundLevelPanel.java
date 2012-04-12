/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class SoundLevelPanel extends JPanel implements iSoundLevelTreshHoldProvider{
  private static final long serialVersionUID = -1956465114269743781L;
  private final Color DARK_GREEN = new Color(0,150,0);
  private final Color DARK_BLUE = new Color(0,0,150);
  private double myMaxLevel = 15;
  
  private double myCurrentSoundLevel = 0;
  private double myMaxSoundLevel = 0;
  private double myThresHoldLevel = 0;
  private double myCurrentPlayingLevel = 0;
  
  private boolean isDragStart = false;
  
  public SoundLevelPanel(){
    addMouseListener();
  }
  
  private void addMouseListener(){
    MyMouseAdapter theMouseAdapter =  new MyMouseAdapter();
    addMouseListener(theMouseAdapter);
    addMouseMotionListener( theMouseAdapter );
  }

  @Override
  public double getThreshHold() {
    return myThresHoldLevel;
  }

  @Override
  public void currentRecordingSoundLevel( double aSoundLevel ) {
    myCurrentSoundLevel = aSoundLevel;
    if(myCurrentSoundLevel > myMaxSoundLevel){
      myMaxSoundLevel = myCurrentSoundLevel;
      myMaxLevel = myMaxSoundLevel * 1.2;
    }
    repaint();
  }
  
  @Override
  public void currentPlayingSoundLevel( double aSoundLevel ) {
    myCurrentPlayingLevel = aSoundLevel;
    if(myCurrentPlayingLevel > myMaxSoundLevel){
      myMaxSoundLevel = myCurrentPlayingLevel;
      myMaxLevel = myMaxSoundLevel * 1.2;
    }
    repaint();
  }
  
  @Override
  public Dimension getPreferredSize() {
    Dimension theDimension = super.getPreferredSize();
    theDimension.height = 30;
    return theDimension;
  }

  public void paint(Graphics g){
    Graphics2D theGraphics = (Graphics2D)g;
    
    g.setColor( Color.white );
    g.fillRect( 0, 0, getWidth(), getHeight() );
    
    g.setColor( Color.black );
    g.drawRect( 5, 5, getWidth() - 10, getHeight() - 10 );
    
    int theWidth = getWidth() - 11;
    int theHeight = getHeight() - 11;
    int theLevelWith = (int)(theWidth * myCurrentSoundLevel / myMaxLevel);
    theGraphics.setPaint( new GradientPaint( 0, 0, DARK_GREEN, getWidth(), 0, Color.red) );
    g.fillRect( 6, 6, theLevelWith, theHeight);
    
    int thePlayingWith = (int)(theWidth * myCurrentPlayingLevel / myMaxLevel);
    theGraphics.setPaint( new GradientPaint( 0, 0, DARK_BLUE, getWidth(), 0, Color.red) );
    g.fillRect( 6, 6, thePlayingWith, theHeight / 2);
    
    theGraphics.setPaint( null );
    
    g.setColor( Color.red );
    int theMaxWidth = (int)( 6 + theWidth * myMaxSoundLevel / myMaxLevel);
    g.fillRect( theMaxWidth, 6, 2, theHeight );
    
    g.setColor( Color.blue);
    int theThresHoldWidth = getThreshHoldWith();
    g.fillRect( theThresHoldWidth, 6, 2, theHeight );
  }
  
  
  //x = 6 + theWidth * myThresHoldLevel / myMaxLevel
 
  private int getThreshHoldWith(){
    int theWidth = getWidth() - 11;
    return (int)( 6 + theWidth * myThresHoldLevel / myMaxLevel);
  }
  
  private class MyMouseAdapter extends MouseAdapter{
    public void mousePressed(MouseEvent e) {
      int theX = e.getX();
      if( Math.abs( theX - getThreshHoldWith()) < 10 ){
         isDragStart = true;
      }
    }

    /**
     * {@inheritDoc}
     */
    // (x - 6) * myMaxLevel / theWidht = myThreshHoldLevel
    public void mouseReleased(MouseEvent e) {
      isDragStart = false;
    }
    
    public void mouseDragged(MouseEvent e){
      if(isDragStart){
        myThresHoldLevel = ((e.getX() - 6) * myMaxLevel) / (getWidth() - 11);
        repaint();
      }
    }
  }


}
