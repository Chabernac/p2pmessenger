package chabernac.queue;

import chabernac.utils.*;

public abstract class QueueReader implements Runnable{
	private AbstractQueue myQueue = null;
	private int myThreads = 1;
	private int currentThreads = 0;
	private boolean stop = false;

	public QueueReader(AbstractQueue aQueue){
		myQueue = aQueue;
		start();
	}

	public void run(){
		incThreads();
		Debug.log(this,"Currently running threads: " + currentThreads);
		boolean stopThisThread = false;
		while(!stop && !stopThisThread){
			if(getCurrentThreads() != myThreads){
				if(getCurrentThreads() > myThreads){
					stopThisThread = true;
				} else if(getCurrentThreads() < myThreads){
					new Thread(this).start();
				}
			}
			try{
				Debug.log(this,"Processing next object");
				processObject(myQueue.get());
				Debug.log(this,"End of object processing");
			}catch(Exception e){ Debug.log(this,"Error occured while processing object",e); }
		}
		Debug.log(this,"Ending thread: " + stop + "," + stopThisThread);
		decThreads();
	}

	private synchronized void incThreads(){
		currentThreads++;
	}

	private synchronized void decThreads(){
		currentThreads--;
	}

	public void start(){
		stop = false;
		new Thread(this).start();
	}

	public void stop(){
		stop = true;
	}

	public void setThreads(int aThreads){
		myThreads = aThreads;
	}

	public int getThreads(){
		return myThreads;
	}

	public synchronized int getCurrentThreads(){
		return currentThreads;
	}

	public AbstractQueue getQueue(){
		return myQueue;
	}

	protected abstract void processObject(Object aObject);

}