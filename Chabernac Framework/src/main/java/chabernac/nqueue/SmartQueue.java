/*
 * Created on 9-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

import java.util.ArrayList;

public class SmartQueue extends QueueDecoratorAdaptor {
	private ArrayList myObjectHistory = null;
	
	public SmartQueue(iQueue aQueue) {
		super(aQueue);
		myObjectHistory = new ArrayList();
	}
	
	public void put(Object o){
		if(!myObjectHistory.contains(o)){
			myObjectHistory.add(o);
			super.put(o);
		}
	}
}
