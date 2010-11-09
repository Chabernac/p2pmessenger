package chabernac.gui.utils;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class GUIDeadlockDetector implements Runnable{
  private static Logger LOGGER = Logger.getLogger(GUIDeadlockDetector.class);
  
  private boolean stop = false;

  public void run() {
    while(!stop){
      postEvent();
      try {
        Thread.sleep(30000);
      } catch (InterruptedException e) {
        LOGGER.debug("Could not sleep", e);
      }
    }
  }
  
  public void start(){
    stop = false;
    new Thread(this).start();
  }
  
  public void stop(){
    stop = true;
  }
  
  private void postEvent(){
    Notifier theNotifier = new Notifier();
    SwingUtilities.invokeLater(new GUIEvent(theNotifier));
    if(!theNotifier.waitForFinished()){
      LOGGER.debug("Possible deadlock detected!");
      printStackTrace();
    } else {
      LOGGER.debug("No deadlock detected!");
    }
  }
  
  private void printStackTrace(){
    System.err.println("Deadlock stack trace");
    System.err.println("--------------------");
    
    int theNrOfThreads = Thread.activeCount();
    Thread[] theThreads = new Thread[theNrOfThreads];
    Thread.enumerate(theThreads);
    for(int i=0;i<theThreads.length;i++){
      //stack trace is written to system.err
      System.err.println(theThreads[i].toString());
      //theThreads[i].dumpStack();
    }
    
    System.err.println("--------------------");
  }
  
  private class Notifier{
    private boolean isFinished = false;

    public boolean isFinished() {
      return isFinished;
    }

    public synchronized void setFinished(boolean isFinished) {
      this.isFinished = isFinished;
      notifyAll();
    }
    
    public synchronized boolean waitForFinished(){
      if(!isFinished){
        try {
          wait(10);
        } catch (InterruptedException e) {
          LOGGER.debug("Could not wait", e);
        }
      }
      return isFinished;
    }
  }
  
  private class GUIEvent implements Runnable{
    private Notifier myNotifier = null;
    
    public GUIEvent(Notifier aNotifier){
      myNotifier = aNotifier;
    }
    
    public void run(){
      myNotifier.setFinished(true);
    }
  }

}
