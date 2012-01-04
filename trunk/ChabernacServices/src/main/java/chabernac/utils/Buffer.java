package chabernac.utils;

import java.util.Comparator;
import java.util.TreeSet;

public class Buffer<T> {
  private int myLowerLimit;
  private int myUpperLimit;
  private TreeSet<T> mySortedItems = null;
  private final Object LOCK = new Object();
  private boolean isWaitUntillFull = true;

  public Buffer(int aLowerLimit, int aUpperLimit){
    this(aLowerLimit, aUpperLimit, null);
  }

  public Buffer(int aLowerLimit, int aUpperLimit, Comparator<T> aComparator) {
    super();
    myLowerLimit = aLowerLimit;
    myUpperLimit = aUpperLimit;
    if(aComparator != null) mySortedItems = new TreeSet<T>(aComparator);
    else mySortedItems = new TreeSet<T>();
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
    mySortedItems.add(aPacket);
    synchronized(LOCK)
    { 
      LOCK.notifyAll();
    }
  }

  public T get(){
    while(isWaitUntillFull && mySortedItems.size() < myUpperLimit || mySortedItems.size() <= myLowerLimit){
      try {
        synchronized (LOCK) {
          LOCK.wait();
        }
      } catch (InterruptedException e) {
      }
    }
    isWaitUntillFull = false;
    T thePacket  = mySortedItems.pollFirst();
    if(mySortedItems.size() <= myLowerLimit){
      isWaitUntillFull = true;
    }
    return thePacket;
  }

  public int size(){
    return mySortedItems.size();
  }
}
