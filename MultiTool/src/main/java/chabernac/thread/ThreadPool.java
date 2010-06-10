/*
 * Created on 25-mrt-2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.thread;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import chabernac.util.SynchronizedCounter;

public class ThreadPool {
	private static Logger logger = Logger.getLogger(ThreadPool.class);
	private ArrayList myRunnables = null;
	private ArrayList myListeners = null;
	
	public ThreadPool(){
		myRunnables = new ArrayList();
		myListeners = new ArrayList();
	}
	
	public void addRunnable(Runnable aRunnable){
		myRunnables.add(aRunnable);
	}
	
	public void startThreads(){
		final SynchronizedCounter theCounter = new SynchronizedCounter();
		int threadsStarted = 0;
		for(final Iterator i=myRunnables.iterator();i.hasNext();){
			final Runnable theRunnable = ((Runnable)i.next());
			new Thread(
					new Runnable(){
						public void run(){
							theRunnable.run();
							theCounter.increment();
						}
					}
			).start();
			threadsStarted ++;
		}
		
		final int theThreadsStarted = threadsStarted;
		
		new Thread(new Runnable(){
			public void run(){
				try {
					theCounter.waitFor(theThreadsStarted);
					for(Iterator i=myListeners.iterator();i.hasNext();){
						((iThreadPoolListener)i.next()).threadsFinished();
					}
				} catch (InterruptedException e) {
					logger.error("An error occured while waiting for threads", e);
				}
			}
		}).start();
	}
	
	public void addListener(iThreadPoolListener aListener){
		myListeners.add(aListener);
	}
	
	public void removeListener(iThreadPoolListener aListener){
		myListeners.remove(aListener);
	}
	
}
