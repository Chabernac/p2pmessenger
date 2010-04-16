/*
 * Created on 9-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

public class QueueFactory {
	public static TriggeredQueue createTriggeredQueue(){
		return new TriggeredQueue(new Queue());
	}
	
	public static TriggeredQueue createTriggeredQueue(int aQueueSize){
		return new TriggeredQueue(new ArrayQueue(aQueueSize));
	}
	
	public static TriggeredQueue createTriggeredCyclicQueue(){
		return new TriggeredQueue(new CycleQueue(new Queue()));
	}
	
	public static TriggeredQueue createTriggeredSmartQueue(){
		return new TriggeredQueue(new SmartQueue(new Queue()));
	}
	
	public static TriggeredQueue createTriggeredSmartQueue(int aQueueSize){
		return new TriggeredQueue(new SmartQueue(new ArrayQueue(aQueueSize)));
	}
	
	public static TriggeredQueue createTriggeredSmartCyclicQueue(){
		return new TriggeredQueue(new SmartQueue(new CycleQueue(new Queue())));
	}
	
	public static TriggeredQueue createTriggeredSmartPriorityCyclicQueue(){
		return new TriggeredQueue(new SmartQueue(new PriorityCycleQueue(new Queue())));	
	}
	
	public static TriggeredQueue createTriggeredPriorityCyclicQueue(){
		return new TriggeredQueue(new PriorityCycleQueue(new Queue()));	
	}
	
	public static TriggeredQueue createTriggeredFilteredPriorityCyclicQueue(iObjectFilter aFilter){
		return new TriggeredQueue(new FilteredQueue(new PriorityCycleQueue(new Queue()), aFilter));	
	}

}
