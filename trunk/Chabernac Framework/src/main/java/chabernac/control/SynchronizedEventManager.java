/*
 * Created on 13-jul-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package chabernac.control;

import java.util.ArrayList;

import chabernac.utils.Debug;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SynchronizedEventManager implements Runnable{
	private ArrayList myList = null;
	private long myCycleTime = 0;
	private boolean stop = false;
	private long counter = 0;
	
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
	
	
	
	public void run(){
		while(!stop){
			long start = System.currentTimeMillis();
			for(int i=0;i<myList.size();i++){
				((iSynchronizedEvent)myList.get(i)).executeEvent(counter++);
			}
			long end = System.currentTimeMillis();
			long duration = end - start;
      if(counter % 30 == 0) System.out.println(((float)1000 / (float)duration) + " frames / sec.");
			if(duration > myCycleTime){
				//Debug.log(this, "Time to process all events is greater than cycle time " + duration + " " + ((float)1000 / (float)duration) + " frames / sec.");
				if(counter % 10 == 0) System.out.println(counter + ": Time to process all events is greater than cycle time " + duration + " " + ((float)1000 / (float)duration) + " frames / sec.");
			} else {
				try{
					Thread.sleep(myCycleTime - duration);
				}catch(InterruptedException e){
					Debug.log(this, "Could not sleep", e);
				}
			}
			Thread.yield();
		}
		
	}
}
