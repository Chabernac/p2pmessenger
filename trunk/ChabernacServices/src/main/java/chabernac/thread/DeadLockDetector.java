package chabernac.thread;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class DeadLockDetector {
  private static Logger LOGGER = Logger.getLogger(DeadLockDetector.class);
  private static long COUNTER_TRESHOLD = 3;
  
  private long myTimeout = 30000;
  
  
  private boolean isGUIEventExecuted = true;
  private boolean stop = false;
  
  public DeadLockDetector(long aTimeout){
    myTimeout = aTimeout;
  }
  
  public synchronized void setGUIEventExecuted(boolean isConsumed){
    this.isGUIEventExecuted = isConsumed;
  }
  
  public synchronized boolean isGUIEventExecuted(){
    return isGUIEventExecuted;
  }
  
  public void start(){
   stop = false;
   
   new Thread(new GUIEventGenerator()).start();
   try {
    Thread.sleep(myTimeout / 2);
  } catch (InterruptedException e) {
    LOGGER.error("Could not sleep", e);
  }
   new Thread(new Detector()).start();
  }
  
  public void stop(){
    stop = true;
  }
  
  private void deadLockDetected(){
    Thread theThread = Thread.currentThread();
    ThreadGroup theGroup = theThread.getThreadGroup();
    while(theGroup.getParent() != null){
      theGroup = theGroup.getParent();
    }
    
    Thread[] theThreads = new Thread[theGroup.activeCount()];
    theGroup.enumerate(theThreads);
    
    for(int i=0;i<theThreads.length;i++){
      //can not generate a stack trace for a thread in jdk 1.4 :-(
    }
    
    System.out.println("A GUI deadlock has been detected");
  }
  
  
  private class GUIEvent implements Runnable{
    public void run() {
      System.out.println("Executing gui event");
      setGUIEventExecuted(true);
    }
  }
  
  private class GUIEventGenerator implements Runnable{

    public void run() {
      try {
        while(!stop){
          Thread.sleep(myTimeout);
          //only generate a new event if the previous one has been consumed
          if(!isGUIEventExecuted()){
            SwingUtilities.invokeLater(new GUIEvent());
          }
        }
      } catch (InterruptedException e) {
        LOGGER.error("Could not sleep", e);
      }
    }
  }
  
  
  
  private class Detector implements Runnable{
    public void run() {
      try {
        long counter = 0;
        while(!stop){
          Thread.sleep(myTimeout);
          if(isGUIEventExecuted()){
            setGUIEventExecuted(false);
            counter = 0;
            System.out.println("No deadlock");
          } else {
            counter++;
            if(counter >= COUNTER_TRESHOLD){
              //a deadlock has been detected, try to print the information
              deadLockDetected();
            }
          }
          
        }
      } catch (InterruptedException e) {
        LOGGER.error("Could not sleep", e);
      }
    }
  }
  
  public static void main(String args[]){
    DeadLockDetector theDetector = new DeadLockDetector(1000);
    theDetector.start();
    
    SwingUtilities.invokeLater(new DeadLockEvent());
    //JOptionPane.showMessageDialog(null, "test");
  }
  
  private static class DeadLockEvent implements Runnable{

    public void run() {
      try {
        Thread.sleep(30000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }
    
  }
  
  
  

}
