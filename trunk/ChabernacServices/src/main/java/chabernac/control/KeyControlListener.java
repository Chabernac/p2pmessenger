package chabernac.control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.log4j.Logger;

public class KeyControlListener implements KeyListener, Runnable{
  private static final Logger LOGGER = Logger.getLogger( KeyControlListener.class );
  
  private boolean[] keysPressed;
  private boolean[] keys;
  private boolean[] keysReleased;
  private KeyMap[] myKeyMappings = null;
  private boolean stop = false;
  private int myTimeout;
  
  public static final int FRAMES_PER_SECOND = 1;
  public static final int FRAMES_PER_MINUTE = 2;
  
  public KeyControlListener(KeyMap[] keyMappings, int aTimeout){
    keysPressed = new boolean[256];
    keys = new boolean[256];
    keysReleased = new boolean[256];
    myKeyMappings = keyMappings;
    myTimeout = aTimeout;
    new Thread(this).start();
  }
  
  public void keyPressed(KeyEvent e){
    keysPressed[e.getKeyCode()] = true;
    keys[e.getKeyCode()] = true; 
  }
  
  public void keyReleased(KeyEvent e){ 
    keys[e.getKeyCode()] = false;
    keysReleased[e.getKeyCode()] = true;
  }
  
  public void keyTyped(KeyEvent e) {}
  
  public void run(){
    while(!stop){
      for(int i=0;i<myKeyMappings.length;i++){
        for(int j=0;j<myKeyMappings[i].getKeyCodes().length;j++){
          if(keysPressed[myKeyMappings[i].getKeyCodes()[j]]){ 
            myKeyMappings[i].getCommand().keyPressed(); 
            myKeyMappings[i].getCommand().keyDown();
            keysPressed[myKeyMappings[i].getKeyCodes()[j]] = false;
          } else if(keys[myKeyMappings[i].getKeyCodes()[j]]){ myKeyMappings[i].getCommand().keyDown(); 
          } else if(keysReleased[myKeyMappings[i].getKeyCodes()[j]]){ 
            myKeyMappings[i].getCommand().keyReleased(); 
            keysReleased[myKeyMappings[i].getKeyCodes()[j]] = false;
          }
        }
      }
      try{
        Thread.sleep(myTimeout);
      }catch(InterruptedException e){
        LOGGER.error( "Could not sleep",e);
      }
    }
  }
  
  public void stop(){ stop = true; }
  public void start(){
    stop = false;
    new Thread(this).start();
  }
  
  public int getTimeOut(){ return myTimeout; }
  public void setTimeout(int aTimeout){myTimeout = aTimeout;}
  public void setTimeout(int aNumber, int aType){
    switch(aType){
      case FRAMES_PER_SECOND:{
        setTimeout((int)(1000  / aNumber));
        break;
      }
      case FRAMES_PER_MINUTE:{
        setTimeout((int)(60000  / aNumber));
        break;
      }
        
    }
  } 

}