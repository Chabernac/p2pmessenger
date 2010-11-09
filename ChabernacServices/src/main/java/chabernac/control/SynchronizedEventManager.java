/*
 * Created on 13-jul-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package chabernac.control;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import chabernac.math.Average;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SynchronizedEventManager implements Runnable{
  private static Logger LOGGER = Logger.getLogger(SynchronizedEventManager.class);
  
	private ArrayList myList = null;
	private long myCycleTime = 0;
	private boolean stop = false;
  private double myFPS;

	public SynchronizedEventManager(long aCycleTime){
		myList = new ArrayList();
		myCycleTime = aCycleTime;
	}

	public void addSyncronizedEvent(iSynchronizedEvent anEvent){
		myList.add(anEvent);
	}

	public void removeSyncronizedEvent(iSynchronizedEvent anEvent){
		myList.remove(anEvent);
	}

	public void startManager(){
		stop = false;
		new Thread(this).start();
	}

	public void stopManager(){
		stop = true;
	}

	public boolean isRunning(){
		return !stop;
	}

	public double getFPS() {
    return myFPS;
  }

  public void setFPS(double anFps) {
    myFPS = anFps;
  }

  public void run(){
    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    long startTime = System.currentTimeMillis();
    long counter = 0;
    
    double theSleepTime = (double)myCycleTime / 2d;
    long thePreviousSleeptime = 0;
    
    Average theAverage = new Average(30);

    
    
		while(!stop){
      long t1 = System.currentTimeMillis();
   
      counter++;
			for(int i=0;i<myList.size();i++){
				((iSynchronizedEvent)myList.get(i)).executeEvent(counter);
			}
      
      
      long calcEndTime = startTime + counter * myCycleTime;
      
      long currentSleepTime = calcEndTime - System.currentTimeMillis();
      
      double theSleepStep = Math.abs((double)currentSleepTime / 1000d);
      
      if(currentSleepTime > 0 && currentSleepTime > thePreviousSleeptime){
        //if(counter % 100 == 0) LOGGER.debug("Increasing");
        theSleepTime += theSleepStep;
      } else if(currentSleepTime < 0 && currentSleepTime < thePreviousSleeptime){
        //if(counter % 100 == 0) LOGGER.debug("Decreasing");
        theSleepTime -= theSleepStep;
        
      }
      thePreviousSleeptime = currentSleepTime;
      
      if(theSleepTime < 0){
        theSleepTime = 0;
      }
      
      long ms = (long)Math.floor(theSleepTime);
      int ns = (int)((theSleepTime - (double)ms) * 1000000);
      
      /*
      if(counter % 100 == 0) {
        LOGGER.debug("sleeptime: " + theSleepTime + "ms: " + ms + "ns: " + ns + " current: " + currentSleepTime);
      }
      */
      
      if(ms>= 0 && ns > 0){
        try{
          Thread.sleep(ms,ns);
        }catch(InterruptedException e){
          LOGGER.error( "Could not sleep", e);
        }
      }
			Thread.yield();
      
      theAverage.addNumber(System.currentTimeMillis() - t1);
      
      //calculate fps
      myFPS = 1000 / theAverage.getAverage();
		}
	}
  
}
