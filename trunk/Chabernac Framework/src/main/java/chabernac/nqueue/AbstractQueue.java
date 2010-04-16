/*
 * Created on 9-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

import chabernac.utils.Debug;

public abstract class AbstractQueue implements iQueue{
	private boolean putEnabled = true;
	private boolean getEnabled = true;
	
	private Object putLock = new Object();
	private Object getLock = new Object();
		
	public final void put(Object o){
		while(!isPutEnabled()){
			synchronized (putLock) {
				try{
					wait();
				}catch(InterruptedException e){
					Debug.log(this,"Could not wait", e);
				}
			}
		}
		storeObject(o);
	}
	
	protected abstract void storeObject(Object o);
	
	public final Object get(){
		while(!isGetEnabled()){
			synchronized (getLock) {
				try{
					wait();
				}catch(InterruptedException e){
					Debug.log(this,"Could not wait", e);
				}
			}
		}
		return retrieveObject();
	}
	
	protected abstract Object retrieveObject();
	
	public void setGetEnabled(boolean getEnabled){ 
		this.getEnabled = getEnabled;
		synchronized (getLock) {
			notifyAll();
		}
	}
	
	public boolean isGetEnabled(){ return getEnabled; }
	public void setPutEnabled(boolean putEnabled){ 
		this.putEnabled = putEnabled; 
		synchronized (putLock) {
			notifyAll();
		}
	}
	public boolean isPutEnabled(){ return putEnabled; }
	public boolean isEmpty(){ return size() == 0; }
	
}
