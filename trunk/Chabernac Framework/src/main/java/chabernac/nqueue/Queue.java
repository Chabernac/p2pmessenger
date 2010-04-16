/*
 * Created on 9-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

import java.util.ArrayList;

public class Queue extends AbstractQueue {
	private ArrayList myQueue = null;
	
	public Queue(){
		myQueue = new ArrayList(100);
	}

	protected void storeObject(Object o) {
		myQueue.add(o);
	}

	protected Object retrieveObject() {
		Object o = myQueue.get(0);
		myQueue.remove(0);
		return o;
	}

	public int size() {
		return myQueue.size();
	}
	
	public void clear(){
		myQueue.clear();
	}

}
