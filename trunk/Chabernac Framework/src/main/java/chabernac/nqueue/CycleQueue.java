/*
 * Created on 9-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

public class CycleQueue extends QueueDecoratorAdaptor {

	public CycleQueue(iQueue aQueue) {
		super(aQueue);
	}
	
	public Object get(){
		Object o = super.get();
		put(o);
		return o;
	}

}
