/*
 * Created on 10-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

public class FilteredQueue extends QueueDecoratorAdaptor {
	iObjectFilter myFilter = null;

	public FilteredQueue(iQueue aQueue, iObjectFilter aFilter) {
		super(aQueue);
		myFilter = aFilter;
	}
	
	public void put(Object o){
		if(myFilter.filter(o)){
			super.put(o);
		}
	}
	
}
