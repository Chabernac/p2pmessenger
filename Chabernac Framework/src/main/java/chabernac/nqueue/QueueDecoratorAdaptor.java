/*
 * Created on 9-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

import chabernac.decorator.iDecorator;

public class QueueDecoratorAdaptor implements iQueue, iDecorator {
	protected iQueue myQueue = null;
	
	public QueueDecoratorAdaptor(iQueue aQueue){
		myQueue = aQueue;
	}
	
	
	public void setPutEnabled(boolean putEnabled) {	myQueue.setPutEnabled(putEnabled); }
	public boolean isPutEnabled() {return myQueue.isPutEnabled(); }
	public void setGetEnabled(boolean getEnabled) { myQueue.setGetEnabled(getEnabled); }
	public boolean isGetEnabled() { return myQueue.isGetEnabled(); }
	public int size() { return myQueue.size(); }
	public Object get() { return myQueue.get(); }
	public void put(Object o) { myQueue.put(o); }
	public boolean isEmpty(){ return size() == 0; }
	public void clear(){ myQueue.clear(); }
	public Object getDecoratedObject() {return myQueue;	}
}
