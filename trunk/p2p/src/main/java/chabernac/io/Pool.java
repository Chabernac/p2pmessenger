/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class Pool<T> implements Iterable< T >{
  private List<PoolItem< T >> myPool = Collections.synchronizedList( new ArrayList< PoolItem< T > >() );
  
  public synchronized void add(T anObject){
    myPool.add(new PoolItem< T >(anObject));
  }
  
  public synchronized void remove(T anObject){
    PoolItem< T > theItem = getPoolItem( anObject );
    if(theItem != null){
      myPool.remove( theItem );
    }
  }
  
  public synchronized boolean contains(T anObject){
    return getPoolItem( anObject ) != null;
  }
  
  private PoolItem< T > getPoolItem(T anObject){
    for(PoolItem< T > theItem : myPool){
      if(theItem.myObject.equals( anObject )){
        return theItem;
      }
    }
    return null;
  }
  
  public synchronized int size(){
    return myPool.size();
  }
  
  public List<T> getItemsOlderThan(long aTimeStamp){
    List<T> theList = new ArrayList< T >();
    for(PoolItem< T > theItem : new ArrayList< PoolItem< T > > (myPool)){
      if(theItem.myTimestamp <= aTimeStamp){
        theList.add(theItem.myObject);
      }
    }
    return theList;
  }
  
  private class PoolItem<E>{
    long myTimestamp = System.currentTimeMillis();
    E myObject;
    
    public PoolItem (E anObject ) {
      super();
      myObject = anObject;
    }
    
    public String toString(){
      return myObject.toString();
    }
  }

  @Override
  public Iterator< T > iterator() {
    return getItemsOlderThan( System.currentTimeMillis() ).iterator();
  }
  
  public void clear(){
    myPool.clear();
  }
}
