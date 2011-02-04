/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;

public class ObservableList<E> extends Observable implements List<E> {
  
  private final List<E> myList;

  public ObservableList( List<E> aList ) {
    super();
    myList = aList;
  }

  public boolean add( E aE ) {
    boolean theResult = myList.add( aE );
    notifyAllObs();
    return theResult;
  }

  public void add( int aIndex, E aElement ) {
    myList.add( aIndex, aElement );
    notifyAllObs();
  }

  public boolean addAll( Collection<? extends E> aC ) {
    boolean theResult = myList.addAll( aC );
    notifyAllObs();
    return theResult;
  }

  public boolean addAll( int aIndex, Collection<? extends E> aC ) {
    boolean theResult = myList.addAll( aIndex, aC );
    notifyAllObs();
    return theResult;
  }

  public void clear() {
    myList.clear();
    notifyAllObs();
  }

  public boolean contains( Object aO ) {
    return myList.contains( aO );
  }

  public boolean containsAll( Collection<?> aC ) {
    return myList.containsAll( aC );
  }

  public boolean equals( Object aO ) {
    return myList.equals( aO );
  }

  public E get( int aIndex ) {
    return myList.get( aIndex );
  }

  public int hashCode() {
    return myList.hashCode();
  }

  public int indexOf( Object aO ) {
    return myList.indexOf( aO );
  }

  public boolean isEmpty() {
    return myList.isEmpty();
  }

  public Iterator<E> iterator() {
    return myList.iterator();
  }

  public int lastIndexOf( Object aO ) {
    return myList.lastIndexOf( aO );
  }

  public ListIterator<E> listIterator() {
    return myList.listIterator();
  }

  public ListIterator<E> listIterator( int aIndex ) {
    return myList.listIterator( aIndex );
  }

  public E remove( int aIndex ) {
    E theItem = myList.remove( aIndex );
    notifyAllObs();
    return theItem;
  }

  public boolean remove( Object aO ) {
    boolean theResult = myList.remove( aO );
    notifyAllObs();
    return theResult;
  }

  public boolean removeAll( Collection<?> aC ) {
    boolean theResult = myList.removeAll( aC );
    notifyAllObs();
    return theResult;
  }

  public boolean retainAll( Collection<?> aC ) {
    boolean theResult = myList.retainAll( aC );
    notifyAllObs();
    return theResult;
  }

  public E set( int aIndex, E aElement ) {
    E theItem = myList.set( aIndex, aElement );
    notifyAllObs();
    return theItem;
  }

  public int size() {
    return myList.size();
  }

  public List<E> subList( int aFromIndex, int aToIndex ) {
    return myList.subList( aFromIndex, aToIndex );
  }

  public Object[] toArray() {
    return myList.toArray();
  }

  public <T> T[] toArray( T[] aA ) {
    return myList.toArray( aA );
  }
  
  public void notifyAllObs(){
    setChanged();
    notifyObservers();
  }
}
