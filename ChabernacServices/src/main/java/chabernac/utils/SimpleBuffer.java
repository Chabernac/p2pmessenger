package chabernac.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimpleBuffer<T> {
  private int myLowerLimit;
  private int myUpperLimit;
  private List<T> myItems = null;
  private final Object LOCK = new Object();
  private boolean isWaitUntillFull = true;

  public SimpleBuffer(int aLowerLimit, int aUpperLimit){
    this(aLowerLimit, aUpperLimit, null);
  }

  public SimpleBuffer(int aLowerLimit, int aUpperLimit, Comparator<T> aComparator) {
    super();
    myLowerLimit = aLowerLimit;
    myUpperLimit = aUpperLimit;
    
    myItems = new ArrayList<T>();
  }

  public int getLowerLimit() {
    return myLowerLimit;
  }

  public void setLowerLimit(int aLowerLimit) {
    myLowerLimit = aLowerLimit;
  }

  public int getUpperLimit() {
    return myUpperLimit;
  }

  public void setUpperLimit(int aUpperLimit) {
    myUpperLimit = aUpperLimit;
  }

  public void put(T aPacket){
    myItems.add(aPacket);
    synchronized(LOCK)
    { 
      LOCK.notifyAll();
    }
  }
  
  public boolean isBufferUnderrun(){
    return isWaitUntillFull && myItems.size() < myUpperLimit || myItems.size() <= myLowerLimit;
  }

  public T get(){
    while(isBufferUnderrun()){
      try {
        synchronized (LOCK) {
          LOCK.wait();
        }
      } catch (InterruptedException e) {
      }
    }
    isWaitUntillFull = false;
    T thePacket  = myItems.remove(0);
    if(myItems.size() <= myLowerLimit){
      isWaitUntillFull = true;
    }
    return thePacket;
  }

  public int size(){
    return myItems.size();
  }
}
