/*
 * Created on 9-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

import java.util.Observable;

import chabernac.decorator.iDecorator;

public class TriggeredQueue extends Observable implements iQueue, iDecorator {
	private iQueue myQueue = null;
	private int triggerDepth = 0;
	
	public TriggeredQueue(iQueue aQueue){
		myQueue = aQueue;
	}

	public Object get() {
		return myQueue.get();
	}

	public void put(Object o) {
		myQueue.put(o);
		if(myQueue.size() >= triggerDepth){
			setChanged();
			notifyObservers();
		}
	}

	public void setPutEnabled(boolean putEnabled) {	myQueue.setPutEnabled(putEnabled); }
	public boolean isPutEnabled() {return myQueue.isPutEnabled(); }
	public void setGetEnabled(boolean getEnabled) { myQueue.setGetEnabled(getEnabled); }
	public boolean isGetEnabled() { return myQueue.isGetEnabled(); }
	public int size() { return myQueue.size(); }
	public boolean isEmpty(){ return myQueue.isEmpty(); }
	public void clear(){ myQueue.clear(); }
	public Object getDecoratedObject() { return myQueue; }
}
