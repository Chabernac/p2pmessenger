/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.net;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class NetstatMonitor extends JFrame {
  private static final long serialVersionUID = -52669569018545738L;
  private static final int MAX_SOCKETS = 700;
  private static final int MAX_SAMPLES = 54000;
  private static Logger LOGGER = Logger.getLogger(NetstatMonitor.class);

  private List<Integer> mySocketsUsed = new ArrayList< Integer >();
  
  private int myMaxIndex = -1;

  public NetstatMonitor(){
    init();
    startTimer();
    
  }
  
  private void init(){
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    setTitle( "Netstat monitor" );
  }

  private void startTimer(){
    ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );
    theService.scheduleAtFixedRate( new RetrieveNrOfSocktes(), 1, 1, TimeUnit.SECONDS );
  }
  
  private void drawAxes(Graphics g){
    //draw y axx
    
    
  
  }
  
  public void paint(Graphics g){
    super.paint( g );
    
    BufferedImage theImage = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
    Graphics theGraphics = theImage.getGraphics();
    
    drawAxes(theGraphics);
    
    double theWidth = getWidth();
    double theHeight = getHeight();
    double theHSpacing = (double)theWidth / (double)mySocketsUsed.size(); 
    double theVSpacing =  (double)theHeight / (double)MAX_SOCKETS;
    
    //draw y axis
    theGraphics.setColor( Color.black );
    theGraphics.drawLine( 10, 0, 10, getHeight() );
    for(int i=100;i<=MAX_SOCKETS;i+=100){
      theGraphics.setColor( Color.black );
      int y = (int)(getHeight() - i * theVSpacing);
      theGraphics.drawString(Integer.toString( i ), 10,  y);
      theGraphics.setColor( Color.LIGHT_GRAY );
      theGraphics.drawLine( 0, y , getWidth(), y );
    }
    
    //draw x axis
    theGraphics.setColor( Color.black );
    theGraphics.drawLine( 0, getHeight() - 10, getWidth(), getHeight() - 10);
    for(int i=0;i<=MAX_SAMPLES;i+=60){
      theGraphics.setColor( Color.black );
      int x = (int)(i * theHSpacing);
      theGraphics.drawString(Integer.toString( i / 60 ), x,  getHeight() - 10);
      theGraphics.setColor( Color.LIGHT_GRAY );
      theGraphics.drawLine( x, 0, x, getHeight() );
    }
    
    theGraphics.setColor( Color.blue);
    
    double thePreviousX = 0;
    double thePreviousY = theHeight;
    
    for(int i=0;i<mySocketsUsed.size();i++){
      double theX = (i + 1) * theHSpacing;
      double theY = theHeight - (mySocketsUsed.get( i ) * theVSpacing);
      
      theGraphics.drawLine( (int)thePreviousX, (int)thePreviousY, (int)theX, (int)theY );
      
      thePreviousX = theX;
      thePreviousY = theY;
    }
    
    //draw max
    if(myMaxIndex >= 0){
      theGraphics.setColor( Color.red );
      int theMaxSockets = mySocketsUsed.get(myMaxIndex);
      double theX = myMaxIndex * theHSpacing;
      double theY = theHeight - theMaxSockets * theVSpacing;
      theGraphics.drawString( Integer.toString( theMaxSockets ), (int)theX, (int)theY );
    }
    
    g.drawImage( theImage, 0, 0, null );
  }
  
  private void checkMax(){
    if(myMaxIndex == -1 || mySocketsUsed.get( mySocketsUsed.size() - 1 ) > mySocketsUsed.get(myMaxIndex)){
      myMaxIndex = mySocketsUsed.size() - 1;
    }
  }

  private class RetrieveNrOfSocktes implements Runnable {
    private Runtime myRuntime = Runtime.getRuntime();

    @Override
    public void run() {
      try{
        Process theProcess = myRuntime.exec( "netstat" );
        BufferedReader theReader = new BufferedReader( new InputStreamReader( theProcess.getInputStream() ) );
        int theLines = 0;
        while(theReader.readLine() != null){
          theLines++;
        }
        mySocketsUsed.add( theLines );
        if(mySocketsUsed.size() >= MAX_SAMPLES) mySocketsUsed.remove( 0 );
        checkMax();
      }catch(Exception e){
        LOGGER.error("Could not get nr of sockets", e);
      }
      repaint();
    }
  }
  
  public static void main(String[] args){
    BasicConfigurator.configure();
    NetstatMonitor theMonitor = new NetstatMonitor();
    theMonitor.setSize( 600, 400 );
    theMonitor.setVisible( true );
  }

}
