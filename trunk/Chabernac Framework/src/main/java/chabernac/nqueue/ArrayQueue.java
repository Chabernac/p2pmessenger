/*
 * Created on 9-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

import chabernac.utils.Debug;

public class ArrayQueue extends AbstractQueue {
	private Object[] list = null;
	private int getPointer = 0;
	private int putPointer = 0;
	private int size = 0; 
	
	public ArrayQueue(int size){
		list = new Object[size];
	}

	protected synchronized void storeObject(Object o) {
		while(size >= list.length){
			try{
				wait();
			}catch(InterruptedException e){
				Debug.log(this,"Could not wait", e);
			}
		}
		list[putPointer++] = o;
		putPointer %= list.length;
		size++;
		notifyAll();
	}

	protected synchronized Object retrieveObject() {
		while(size <= 0){
			try{
				wait();
			}catch(InterruptedException e){
				Debug.log(this,"Could not wait", e);
			}
		}
		Object o = list[getPointer++];
		getPointer %= list.length;
		size--;
		notifyAll();
		return o;
	}
	
	public int size(){
		return size;
	}
	
	public void clear(){
		getPointer = 0;
		putPointer = 0;
		size = 0;
	}

}
