/*
 * Created on 9-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;


public class PriorityCycleQueue extends QueueDecoratorAdaptor {
	private CycleQueue myCycleQueue = null;

	public PriorityCycleQueue(iQueue aQueue) {
		super(aQueue);
		myCycleQueue = new CycleQueue(new Queue());
	}
	
	public Object get(){
		Object o = null;
		if(super.size() == 0 && myCycleQueue.size() > 0){
			o = myCycleQueue.get();
		} else {
			o = super.get();
			myCycleQueue.put(o);
		}
		return o;
	}
	
	public int size(){
		return super.size() + myCycleQueue.size();
	}
	
	public void clear(){
		super.clear();
		myCycleQueue.clear();
	}
}
