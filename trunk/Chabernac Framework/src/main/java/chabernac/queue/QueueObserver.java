package chabernac.queue;

import java.util.Observer;
import java.util.Observable;
import java.util.Vector;
import java.io.Serializable;
import chabernac.utils.*;

public abstract class QueueObserver extends Observable implements Observer, Runnable, ThreadTimerListener, Serializable
{
  protected Queue myQueue = null;
  private boolean stop = false;
  //protected StatusListener listener = null;
  private int threads = 1;
  protected int currentThreads = 0;
  private Vector waitFor = null;
  private int decrement = 0;
  private int increment = 0;
  private int interval = 0;
  private int timeout = 0;
  private Vector secondaryQueues = null;
  private boolean threaded = true;
  private int priority = Thread.NORM_PRIORITY;


  public QueueObserver(Queue aQueue)
  {
    myQueue = aQueue;
    myQueue.addObserver(this);
    initialize();
  }

 private void initialize()
 {
	 waitFor = new Vector(1,1);
	 secondaryQueues = new Vector(2,2);

 }

 public void run()
 {
	 runIt();
 }

 private void runIt()
 {
	 //incrementThreads();
	 ThreadTimer timer  = null;
	 try
	   {
		Object o = null;
		if(timeout > 0)
		{
			timer = new ThreadTimer(timeout,this,Thread.currentThread());
			timer.start();
		}
   		while(!waiting() && !stop && (o = getElement())!=null) //&& !Thread.interrupted())
   		{
				processObject(o);
				if(timer!=null && timer.isTimedOut()){break;}
				if(interval > 0)
				{
					try
					{
						Debug.log(this,"Pausing thread for " + interval  + " milliseconds");
						Thread.sleep(interval);
						intervalElapsed();
					}catch(Exception e){Debug.log(this,"Could not sleep",e);}
				}
     	}
	   }catch(Exception e){Debug.log(this,"Exception occured while processing object",e);}


	if(timer!=null && !timer.isTimedOut())
	{
		timer.stop();
		//Debug.log(this,"Thread finished, decreasing threads");
		decreaseThreads();
	}
	else if(timer == null)
	{
		decreaseThreads();
	}


/*
	if(!Thread.interrupted())
	{
		Debug.log(this,"Thread not interrupted");
		decreaseThreads();
	}
	else
	{
		Debug.log(this,"Decrease skipped, thread timed out");
	}
	*/
  }

  private synchronized void incrementThreads()
  {
	  if(currentThreads==0)
	  {
	  		//listener.statusChanged(this,StatusListener.RUNNING);
	  		setChanged();
	   	   	notifyObservers();
  	  }
  	  increment++;
	  //Debug.log(this,increment + " times incremented");
	  currentThreads++;
  }

  private synchronized void decreaseThreads()
  {
	    decrement++;
	    //Debug.log(this,decrement + " times decremented");
	  	currentThreads--;
	  	//Debug.log(this,currentThreads + " threads running");
	  	if(currentThreads==0)
	  	{
			cleanUp();
	  	   	setChanged();
	  	    notifyObservers();
	 	}
  }


/**The implemented method must end with a readQueue() statement in order to process the next element in the Queue.*/
 public abstract void  processObject(Object o);
 //public boolean isRunning(){return running;}

/**This method can be overridden in order to cleanup some objects after the queue is empty*/
public void cleanUp(){}
/**This method can be overridden to do some action after the interval time has elapsed, only called when interval > 0 millisecond*/
public void intervalElapsed(){}

public void update(Observable o, Object arg)
{
	trigger();
}

public void trigger()
{
	if(!waiting() && !stop)
	{
	  	startReadingQueue();
	}
}


public synchronized void startReadingQueue()
{
	if(!threaded && currentThreads==0)
	{
		currentThreads++;
		runIt();
	}
	else
	{
		//Debug.log(this,"Begin of startReadingQueue()");
		int currentQueueSize = myQueue.size();
		//Debug.log(this,"stored queue size");
		//int currentThreadsEstimation = currentThreads;
		while(myQueue.isGetEnabled() && currentQueueSize!=0 && !stop && currentThreads < threads)
		{
			//Debug.log(this,"making new thread " + currentThreads);
			increment++;
			//Debug.log(this,increment + " times incremented");
			currentThreads++;
			currentQueueSize--;

			if(currentThreads==1)
			{
				//listener.statusChanged(this,StatusListener.RUNNING);
				setChanged();
				notifyObservers();
			}
			//currentThreadsEstimation++;
			//Debug.log(this,"Making new thread");
			Thread thread = new Thread(this);
			thread.setPriority(priority);
			thread.start();
		}
	}
}

public void setThreads(int threads){this.threads = threads;}
public int getThreads(){return threads;}
public void setInterval(int interval){this.interval = interval;}
public int getInterval(){return interval;}
public void setPaused(boolean stop){this.stop = stop;}
public boolean isPaused(){return stop;}
public void setTimeout(int timeout){this.timeout = timeout;}
public int getTimeout(){return timeout;}
public void addQueue(Queue aQueue){secondaryQueues.addElement(aQueue);}
public void removeQueue(Queue aQueue){secondaryQueues.removeElement(aQueue);}
public void setThreaded(boolean threaded){this.threaded = threaded;}
public void setPriority(int priority){this.priority = priority;}
public int getPriority(){return priority;}
public Queue getQueue(){return myQueue;}
public Vector getSecondaryQueues(){return secondaryQueues;}

public boolean isRunning()
{
	if(currentThreads==0){return false;}
	else{return true;}
}

//public void addStatusListener(StatusListener listener){this.listener = listener;}

private Object getElement()
{
	try
	{
		if(myQueue.isGetEnabled() && myQueue.size() > 0){return myQueue.get();}
		else
		{
			Queue theQueue = null;
			for(int i=0;i<secondaryQueues.size();i++)
			{
				theQueue = (Queue)secondaryQueues.elementAt(i);
				if(theQueue.isGetEnabled() && theQueue.size() > 0){return theQueue.get();}
			}
		}
		return null;
	}catch(Exception e)
		{
			Debug.log(this,"Exception occured while retrieving object, returning null",e);
			return null;
		}
}

public void waitFor(QueueObserver aQueueObserver)
{
	waitFor.addElement(aQueueObserver);
	aQueueObserver.addObserver(this);
}

private boolean waiting()
{
	for(int i=0;i<waitFor.size();i++)
	{
		if(((QueueObserver)waitFor.elementAt(i)).isRunning()){return true;}
	}
	return false;
}

public void timeOutOccured()
{

	Debug.log(this,"TIME OUT OCCURED, decreasing threads");
	decreaseThreads();
	//Debug.log(this,"Making new threads");
	startReadingQueue();

}


}