/*
 * Created on 9-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

import java.util.Observable;
import java.util.Observer;

import chabernac.utils.Debug;

public class QueueReader implements Observer, Runnable{
	private iQueue myQueue = null;
	private int maxThreads = 1;
	private int threads = 0;
	private iObjectProcessor myObjectProcessor = null;
	private int timeout = 0;
	private boolean pause = false;
	
	public QueueReader(TriggeredQueue aQueue, iObjectProcessor aObjectProcessor){
		myQueue = aQueue;
		myObjectProcessor = aObjectProcessor;
		aQueue.addObserver(this);
	}

	public void update(Observable o, Object arg) {
		trigger();
	}
	
	public void trigger(){
		if(threads < maxThreads){
			new Thread(this).start();
			Thread.yield();
		}
	}
	
	public void run() {
		threads++;
		Debug.log(this,"Threads running: " + threads);
		if(threads < maxThreads && myQueue.size() > threads)new Thread(this).start();
		while(!myQueue.isEmpty() && !pause){
			boolean ok = myObjectProcessor.processObject(myQueue.get());
			if(ok && timeout > 0){
				try{
					Thread.sleep(timeout);
				}catch(InterruptedException e){
					Debug.log(this,"Could not sleep", e);
				}
			}
			
		}
		threads--;
		Debug.log(this,"Threads running: " + threads);
	}
	
	public void setThreads(int threads){ this.maxThreads = threads; }
	public int getThreads(){ return threads; }
	
	public void setObjectProcessor(iObjectProcessor aObjectProcessor){ myObjectProcessor = aObjectProcessor; }
	public iObjectProcessor getObjectProcessor(){ return myObjectProcessor; }
	
	public iQueue getQueue(){ return myQueue; }

	public void setTimeout(int aTimeout){ timeout = aTimeout; }
	public int getTimeout(){ return timeout; }
	
	public void setPaused(boolean pause){
		this.pause = pause;
		if(!pause) trigger();
	}
	
	public boolean isPaused(){ return pause; }
}
