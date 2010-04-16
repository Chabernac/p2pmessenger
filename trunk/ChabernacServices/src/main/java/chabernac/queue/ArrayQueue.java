/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.queue;

import org.apache.log4j.Logger;

/**
 *   
 *  Array implementation of a queue.  In the constructor you specify the size of the queue.
 *  If the queue is full the put object locks until an object is get from the queue.
 *  If the queue is empty the get method locks until an object is put on the queue.
 *
 * @version v1.0.0      Sep 20, 2005
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Sep 20, 2005 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */

public class ArrayQueue implements iQueue {
  private static Logger        logger       = Logger.getLogger(ArrayQueue.class);

  private Object[] myObjects = null;
  private int getPointer = 0;
  private int putPointer = 0;
  private int objectsOnQueue = 0;
  private boolean isDisposeItemWhenFull = false;

  public ArrayQueue(int aSize, boolean isDisposeWhenFull){
    myObjects = new Object[aSize];
    this.isDisposeItemWhenFull = isDisposeWhenFull;
  }

  public ArrayQueue(int aSize){
    this(aSize, false);
  }

  public synchronized Object get() {
    while(objectsOnQueue <= 0){
      try{
        wait();
      }catch(InterruptedException e){
        logger.debug("waiting was interrupted", e);
      }
    }
    Object theObject = myObjects[getPointer];
    getPointer = increasePointer(getPointer);
    objectsOnQueue--;
    notifyAll();
    return theObject;
  }

  public synchronized void put(Object anObject) {
    while(objectsOnQueue >= myObjects.length){
      if(isDisposeItemWhenFull){
        //dispose 1 item to make place for the new item
        Object theDisposedItem = get();
        logger.error( "Array queue overloaded disposing item: " +  theDisposedItem);      
      } else {
        try{
          wait();
        }catch(InterruptedException e){
          logger.debug("waiting was interrupted", e);
        }
      }
    }
    myObjects[putPointer] = anObject;
    putPointer = increasePointer(putPointer);
    objectsOnQueue++;
    notifyAll();
  }

  public synchronized int size(){
    return objectsOnQueue;
  }

  private int increasePointer(int aPointer){
    return (++aPointer) % myObjects.length;
  }

  public void clear() {
    while(size() > 0){
      get();
    }
  }

  public Object peek() {
    return myObjects[getPointer];
  }

  public boolean isDisposeItemWhenFull() {
    return isDisposeItemWhenFull;
  }

  public void setDisposeItemWhenFull( boolean anIsDisposeItemWhenFull ) {
    isDisposeItemWhenFull = anIsDisposeItemWhenFull;
  }
}
