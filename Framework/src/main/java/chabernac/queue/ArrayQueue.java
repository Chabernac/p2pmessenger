package chabernac.queue;

import chabernac.log.Logger;

public class ArrayQueue implements iQueue {
  private Object[] myObjects = null;
  private int getPointer = 0;
  private int putPointer = 0;
  private int objectsOnQueue = 0;
  
  public ArrayQueue(int aSize){
    myObjects = new Object[aSize];
  }

  public synchronized Object get() {
    while(objectsOnQueue <= 0){
      try{
        wait();
      }catch(InterruptedException e){
        Logger.log(this, "waiting was interrupted", e);
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
      try{
        wait();
      }catch(InterruptedException e){
        Logger.log(this, "waiting was interrupted", e);
      }
    }
    myObjects[putPointer] = anObject;
    putPointer = increasePointer(putPointer);
    objectsOnQueue++;
    notifyAll();
  }
  
  public int size(){
    return objectsOnQueue;
  }
  
  private int increasePointer(int aPointer){
    return (++aPointer) % myObjects.length;
  }

}
