/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.squeue;

import java.util.Vector;

import org.apache.log4j.Logger;



/**
 *
 *
 * @version v1.0.0      17-jun-2004
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 17-jun-2004 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:Guy.Chauliac@axa.be"> Guy Chauliac </a>
 */
public class Queue extends Vector {
	private static final long serialVersionUID = 7427158300686511377L;
	private static Logger        logger       = Logger.getLogger(Queue.class);
	private boolean unlock = false;

	public Queue(int initialCapacity, int capacityIncrement){
		super(initialCapacity, capacityIncrement);
	}

	public synchronized void put(Object anObject){
		addElement(anObject);
		notifyAll();
	}

	public synchronized Object get(){
		while(size() == 0 && !unlock){
			try{
				//System.out.println("Waiting...");
				wait();
				if(unlock){
					unlock = false;
					break;
				}
				//System.out.println("Notify received");
			}catch(InterruptedException e){
				logger.error("Waiting was interrupted", e);
			}
		}
		if(size() == 0) return null;
		Object theObject = elementAt(0);
		removeElementAt(0);
		return theObject;
	}

	public synchronized void unlock(){
		unlock = true;
		//System.out.println("Unlocking...");
		notifyAll();
	}
}
