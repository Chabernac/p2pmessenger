/*
 * Created on 25-mrt-2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.test;

import java.util.Random;

import chabernac.thread.ThreadPool;
import chabernac.thread.iThreadPoolListener;
import chabernac.utils.SynchronizedCounter;

public class ThreadPoolTest {
	public static void main(String args[]){
		ThreadPool thePool = new ThreadPool();
		final Random theRandom = new Random();
		final SynchronizedCounter theCounter = new SynchronizedCounter();
		for(int i=0;i<20;i++){
			thePool.addRunnable(new Runnable(){
				public void run(){
					try {
						Thread.sleep(Math.abs(theRandom.nextInt() % 5000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					theCounter.increment();
					System.out.println("Thread finished: " + theCounter.getValue());
				}
			});
		}
		
		thePool.addListener(new iThreadPoolListener(){
			public void threadsFinished(){
				System.out.println("Threads finished");
			}
		});
		
		thePool.startThreads();
	}
}
