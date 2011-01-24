/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InfoPanel extends JDialog {
  private static final long serialVersionUID = 2469097210084713576L;
  private JLabel myLabel = new JLabel();

  private int myTargetX;
  private int myTargetY;
  private int myStartX;
  private int myStartY;

  private ScheduledExecutorService myService = Executors.newScheduledThreadPool( 1 );
  private ScheduledFuture<?> myFuture = null;
  private ScheduledFuture<?> myFuture2 = null;
  private int myDisplayTime = 5;
  private Runnable myCommandOnClick = null;

  public InfoPanel(){
    init();
    buildGUI();
    addListeners();
  }

  private void init(){
    myLabel.setHorizontalAlignment( JLabel.CENTER );
    myLabel.setVerticalAlignment( JLabel.CENTER );
  }
  
  
  private void buildGUI(){
    setUndecorated( true );
    setSize( 250,40 );
    setLayout( new BorderLayout() );
    InfoPanelP myPanel = new InfoPanelP();
    add(myPanel, BorderLayout.CENTER);
    setAlwaysOnTop( true );
  }
  
  public void setSize(int aWidth, int aHeight){
    super.setSize( aWidth, aHeight );
    getScreenParams();
  }
  
  private void getScreenParams(){
    Dimension theScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
    myTargetX = (int)(theScreenSize.getWidth() - getWidth() - 4);
    myTargetY = (int)(theScreenSize.getHeight() - getHeight() - 60);
    myStartX = (int)theScreenSize.getWidth() + 2;
//    myStartX = myTargetX;
    myStartY = myTargetY;    
//    myStartY = myTargetY + 100;
  }

  private void addListeners(){
    MyListener theListener = new MyListener();
    addMouseListener( theListener );
  }

  public void setText(String aText){
    myLabel.setText( aText );
    super.setVisible( true );
    setVisible( true );
  }
  
  public int getDisplayTime() {
    return myDisplayTime;
  }

  public void setDisplayTime( int aDisplayTime ) {
    myDisplayTime = aDisplayTime;
  }

  public Runnable getCommandOnClick() {
    return myCommandOnClick;
  }

  public void setCommandOnClick( Runnable aCommandOnClick ) {
    myCommandOnClick = aCommandOnClick;
  }

  private class InfoPanelP extends JPanel{
    private static final long serialVersionUID = 179037312560798346L;

    public InfoPanelP(){
      setLayout( new BorderLayout() );
      add(myLabel, BorderLayout.CENTER);
      setBackground( Color.white );
    }

    public void paint(Graphics g){
      super.paint( g );
      Graphics2D theG = (Graphics2D)g;
      theG.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
      Stroke theStroke = new BasicStroke( 1.5F );
      theG.setStroke( theStroke );
      //      theG.setStroke( new Stroke )
      g.setColor( Color.DARK_GRAY );
      g.drawRoundRect( 1, 1, getWidth()-2, getHeight()-2, 10,10 );
    }
  }

  public void setVisible(boolean isVisible){
    if(myFuture != null && !myFuture.isCancelled()){
      myFuture.cancel( true );
    }
    
    if(myFuture2 != null && !myFuture2.isCancelled()){
      myFuture2.cancel( true );
    }
    
    if(isVisible){
      setLocation( myStartX, myStartY );
      myFuture = myService.scheduleAtFixedRate( new FrameMover( 50, myTargetX, myTargetY ), 0, 10, TimeUnit.MILLISECONDS);
      myFuture2 = myService.schedule( new HideCommand(), myDisplayTime, TimeUnit.SECONDS );
    } else {
      myFuture = myService.scheduleAtFixedRate( new FrameMover( 50, myStartX, myStartY), 0, 10, TimeUnit.MILLISECONDS);
    }
  }
  
  private class FrameMover implements Runnable{
    private final int mySteps, myTargetX, myTargetY;
    private double myX,myY,myDeltaX,myDeltaY;
    private boolean isSlow = false;

    public FrameMover( int aSteps, int aTargetX, int aTargetY ) {
      super();
      mySteps = aSteps;
      myTargetX = aTargetX;
      myTargetY = aTargetY;
      myX = getX();
      myY = getY();
      myDeltaX = ((double)myTargetX - (double)myX) / (double)mySteps; 
      myDeltaY = ((double)myTargetY - (double)myY) / (double)mySteps;
    }

    public void run(){
      if(Math.abs( myTargetX - myX) <= Math.abs(myDeltaX)  && Math.abs(myTargetY  - myY) <= Math.abs(myDeltaY)){
        myFuture.cancel( true );
      } else {
        myX += myDeltaX;
        myY += myDeltaY;
        setLocation( (int)myX, (int)myY );
      }
      
      if(myDeltaX != 0 && !isSlow && Math.abs(myTargetX - myX) < getWidth() / 8){
        myDeltaX /= 20;
        isSlow = true;
      }
      
      if(myDeltaY != 0 && !isSlow && Math.abs(myTargetY - myY) < getHeight() / 8){
        myDeltaY /= 20;
        isSlow = true;
      }
    }
  }
  
  private class HideCommand implements Runnable{
    public void run(){
      setVisible( false );
    }
  }


  private class MyListener extends MouseAdapter{

    @Override
    public void mousePressed( MouseEvent anEvent ) {
      setVisible( false );
      if(anEvent.getButton() == 1 && myCommandOnClick != null){
        myCommandOnClick.run();
      }
    }
  }
  
  public static void main(String args[]) throws InterruptedException{
    InfoPanel thePanel = new InfoPanel();
    thePanel.setDisplayTime( 5 );
    thePanel.setSize( 100, 20 );
    thePanel.setText( "Nieuw bericht" );
    thePanel.setCommandOnClick( new Runnable() {
      
      @Override
      public void run() {
        System.out.println("Cliced!!");
      }
    });
  }

}
