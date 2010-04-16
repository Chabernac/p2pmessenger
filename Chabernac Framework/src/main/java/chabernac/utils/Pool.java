package chabernac.utils;

public class Pool{
	private int myPoolSize;
	private int myCurrentSlot;

	public Pool(int aPoolSize){
		myPoolSize = aPoolSize;
		myCurrentSlot = 0;
	}

	public synchronized int getSlot(){
		while(myCurrentSlot >= myPoolSize){
			try{
				wait();
			}catch(Exception e){
				Debug.log(this,"Could not wait");
			}
		}
		return ++myCurrentSlot;
	}

	public synchronized void freeSlot(){
		--myCurrentSlot;
		notifyAll();
	}

	public int getPoolSize(){
		return myPoolSize;
	}

	public synchronized void setPoolSize(int aPoolSize){
		myPoolSize = aPoolSize;
	}

	public int getCurrentSlot(){
		return myCurrentSlot;
	}

}