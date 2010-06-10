/*
 * Created on 23-mrt-2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.util;

public class SynchronizedCounter {
	 private long counter = 0;
	  private long maxValue = 0;
	  private long minValue = 0;
	  public synchronized void increment(){
		  counter ++;
		  notifyAll();
		  if(counter > maxValue) maxValue = counter;
	  }
	  
	  public synchronized void decrement(){
		  counter --;
		  notifyAll();
		  if(counter < minValue) minValue = counter;
	  }
	  
	  public synchronized long getValue(){
		  return counter;
	  }
	  
	  public synchronized long getMinValue(){
		  return minValue;
	  }
	  
	  public synchronized long getMaxValue(){
		  return maxValue;
	  }
	  
	  public synchronized void waitForIncrement() throws InterruptedException{
		  long theCounter = counter;
		  while(counter <= theCounter){
			  wait();
		  }
	  }
	  
	  public synchronized void waitForDecrement() throws InterruptedException{
		  long theCounter = counter;
		  while(counter >= theCounter){
			  wait();
		  }
	  }
	  
	  public synchronized void waitFor(long aCounter) throws InterruptedException{
		  while(counter != aCounter){
			  wait();
		  }
	  }
}
