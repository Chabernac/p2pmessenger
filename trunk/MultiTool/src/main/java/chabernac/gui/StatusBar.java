package chabernac.gui;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.util.StatusEvent;

public class StatusBar extends JTextField implements iEventListener, Runnable{
  private static final Color COLOR_WARNING = new Color(0xDB9500); 
  private static final Color COLOR_ERROR = new Color(0xB71416);
  private static final Color COLOR_MESSAGE = new Color(0x4D9844);

  private boolean stopFade = false;
  private boolean isRunning = false;
  
  private static final int COLOR_INC = 5;

  public StatusBar(){
    setEditable(false);
    setBackground(new Color(250,250,250));
    setForeground(COLOR_ERROR);

    EtchedBorder theBorder= new EtchedBorder(EtchedBorder.RAISED, Color.black, Color.white);
    setBorder(theBorder);
    //ApplicationEventDispatcher.addListener(this, StatusEvent.class);
    ApplicationEventDispatcher.addListener(this);
  }

  private synchronized void fadeout(){
    interruptFade();
    stopFade = false;
    new Thread(this).start();
    while(!isRunning){
      try {
        wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void run(){
    synchronized(StatusBar.this){
      isRunning = true;
      notifyAll();
      try {
        wait(6000);
        while(!stopFade && !getForeground().equals(Color.white)){
          setForeground(makeBrighter(getForeground()));
          wait(30);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      isRunning = false;
      notifyAll();
    }
  }
  
  private synchronized void interruptFade(){
    while(isRunning){
      stopFade = true;
      notifyAll();
      try {
        wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }


  private void setMessage(String aMessage){
    setForeground(COLOR_MESSAGE);
    setText(aMessage);
    fadeout();
    
  }

  private void setWarning(String aWarning){
    setForeground(COLOR_WARNING);
    setText(aWarning);
    fadeout();
  }

  private void setError(String anError){
    setForeground(COLOR_ERROR);
    setText(anError);
    fadeout();
  }

  public void clear(){
    setText("");
  }
  
  public static Color makeBrighter(Color aColor){
    int red = aColor.getRed();
    int green = aColor.getBlue();
    int blue = aColor.getBlue();
    
    red += COLOR_INC;
    green += COLOR_INC;
    blue += COLOR_INC;
    
    if(red > 255) red = 255;
    if(blue > 255) blue = 255;
    if(green  > 255) green = 255;
    return new Color(red, green, blue);
  }

  public void eventFired(Event anEvent) {
    if(anEvent instanceof StatusEvent){
      StatusEvent theEvent = (StatusEvent)anEvent;
      if(theEvent.getType() == StatusEvent.WARNING) setWarning(theEvent.getDescription());
      else if(theEvent.getType() == StatusEvent.MESSAGE) setMessage(theEvent.getDescription());
      else if(theEvent.getType() == StatusEvent.ERROR) setError(theEvent.getDescription());
    } else {
      setMessage("Event dispatched: " + anEvent.getDescription());
    }
  }
}
