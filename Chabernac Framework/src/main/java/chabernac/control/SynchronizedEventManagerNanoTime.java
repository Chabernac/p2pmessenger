/*
 * Created on 13-jul-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package chabernac.control;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import chabernac.utils.Debug;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SynchronizedEventManagerNanoTime implements Runnable{
	private ArrayList myList = null;
	private long myCycleTime = 0;
	private boolean stop = false;
	private long counter = 0;

	public SynchronizedEventManagerNanoTime(long aCycleTime){
		myList = new ArrayList();
		myCycleTime = aCycleTime * 1000000;
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



	public void run(){
		while(!stop){
			//long start = System.nanoTime();
      long start = 0;
			counter++;
			for(int i=0;i<myList.size();i++){
				((iSynchronizedEvent)myList.get(i)).executeEvent(counter);
			}	
			//long end = System.nanoTime();
      long end = 0;
			long duration = end - start;
			if(counter % 100 == 0) {
				System.out.println("Duration: " + duration);
				//System.out.println(((float)1000 / (float)duration) + " frames / sec.");
			}
			if(duration > myCycleTime){
				//Debug.log(this, "Time to process all events is greater than cycle time " + duration + " " + ((float)1000 / (float)duration) + " frames / sec.");
				if(counter % 10 == 0) System.out.println(counter + ": Time to process all events is greater than cycle time " + duration + " " + ((float)1000 / (float)duration) + " frames / sec.");
			} else {
				try{
					long ms = (myCycleTime - duration);
					ms = ms / 1000000;
					int ns = (int)(ms % 1000000);
					Thread.sleep(ms,ns);
				}catch(InterruptedException e){
					Debug.log(this, "Could not sleep", e);
				}
			}
			Thread.yield();
		}
	}
  
  private class SynchronizedEventTimerTask extends TimerTask{
    private  int myCounter = 0;
    public iSynchronizedEvent mySyncEvent = null;
    
    public SynchronizedEventTimerTask(iSynchronizedEvent anEvent){
      mySyncEvent = anEvent;
    }

    public void run() {
     mySyncEvent.executeEvent(myCounter++);
    }
    
  }
}
