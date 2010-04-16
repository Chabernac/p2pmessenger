package chabernac.queue;

import java.util.Observable;

public abstract class AbstractQueue extends Observable implements QueueInterface{

	private boolean putEnabled = true;
	private boolean getEnabled = true;
	private int maxSize = 0;
	private boolean putLock = false;
	private boolean getLock = false;
	private Object putLockObject = new Object();
	private Object getLockObject = new Object();


	public void setGetEnabled(boolean getEnabled){
		this.getEnabled = getEnabled;
		if(getEnabled && getLock){
			synchronized(getLockObject){
				getLock = false;
				getLockObject.notifyAll();
				notifyObs();
			}
		}
	}

	public void setPutEnabled(boolean putEnabled){
		this.putEnabled = putEnabled;
		if(putEnabled && putLock){
			synchronized(putLockObject){
				putLock = false;
				putLockObject.notifyAll();
				notifyObs();
			}
		}
	}

	public boolean isPutEnabled(){
		return this.putEnabled;
	}

	public boolean isGetEnabled(){
		return this.getEnabled;
	}

	public final Object get() throws QueueException{
		while(size() == 0  || !getEnabled){
			synchronized(getLockObject){
				try{
					getLock = true;
					getLockObject.wait();
				}catch(Exception e){ getLock = false; }
			}
		}
		Object aObject = getObject();
		if(putLock && putEnabled && (maxSize==0 || size() < maxSize)){
			synchronized(putLockObject){
				putLock = false;
				putLockObject.notifyAll();
			}
		}
		notifyObs();
		return aObject;
	}

	public final void put(Object aObject) throws QueueException{
			while(!putEnabled || (maxSize > 0 && size() >= maxSize)){
					synchronized(putLockObject){
						try{
							putLock = true;
							putLockObject.wait();
						}catch(Exception e){ putLock = false; }
					}
			}
			putObject(aObject);
			if(getLock && getEnabled && size() > 0){
				synchronized(getLockObject){
					getLock = false;
					getLockObject.notifyAll();
				}
			}
			notifyObs();
	}

	public void setMaxSize(int aSize){
		this.maxSize = aSize;
		notifyObs();
	}

	public int getMaxSize(){
		return maxSize;
	}

	private void notifyObs(){
		setChanged();
		notifyObservers();
	}

	protected abstract Object getObject() throws QueueException;
	protected abstract void putObject(Object aObject) throws QueueException;
}