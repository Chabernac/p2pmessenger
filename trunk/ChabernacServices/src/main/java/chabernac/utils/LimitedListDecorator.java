package chabernac.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class LimitedListDecorator<E> implements List<E> {
  private final List<E> myInnerList;
  private int myMaxItems;

  public LimitedListDecorator(int aMaxItems, List<E> aInnerList) {
    super();
    myMaxItems = aMaxItems;
    myInnerList = aInnerList;
  }

  public int size() {
    return myInnerList.size();
  }

  public boolean isEmpty() {
    return myInnerList.isEmpty();
  }

  public boolean contains(Object aO) {
    return myInnerList.contains(aO);
  }

  public Iterator<E> iterator() {
    return myInnerList.iterator();
  }

  public Object[] toArray() {
    return myInnerList.toArray();
  }

  public <T> T[] toArray(T[] aA) {
    return myInnerList.toArray(aA);
  }

  public boolean add(E aE) {
    boolean isAdded = myInnerList.add(aE);
    truncate();
    return isAdded;
  }
  
  private void truncate(){
    while(size() > myMaxItems) myInnerList.remove(0);
  }

  public boolean remove(Object aO) {
    return myInnerList.remove(aO);
  }

  public boolean containsAll(Collection<?> aC) {
    return myInnerList.containsAll(aC);
  }

  public boolean addAll(Collection<? extends E> aC) {
    boolean isAdded = myInnerList.addAll(aC);
    truncate();
    return isAdded;
  }

  public boolean addAll(int aIndex, Collection<? extends E> aC) {
    boolean isAdded = myInnerList.addAll(aIndex, aC);
    truncate();
    return isAdded;
  }

  public boolean removeAll(Collection<?> aC) {
    return myInnerList.removeAll(aC);
  }

  public boolean retainAll(Collection<?> aC) {
    return myInnerList.retainAll(aC);
  }

  public void clear() {
    myInnerList.clear();
  }

  public boolean equals(Object aO) {
    return myInnerList.equals(aO);
  }

  public int hashCode() {
    return myInnerList.hashCode();
  }

  public E get(int aIndex) {
    return myInnerList.get(aIndex);
  }

  public E set(int aIndex, E aElement) {
    return myInnerList.set(aIndex, aElement);
  }

  public void add(int aIndex, E aElement) {
    myInnerList.add(aIndex, aElement);
    truncate();
  }

  public E remove(int aIndex) {
    return myInnerList.remove(aIndex);
  }

  public int indexOf(Object aO) {
    return myInnerList.indexOf(aO);
  }

  public int lastIndexOf(Object aO) {
    return myInnerList.lastIndexOf(aO);
  }

  public ListIterator<E> listIterator() {
    return myInnerList.listIterator();
  }

  public ListIterator<E> listIterator(int aIndex) {
    return myInnerList.listIterator(aIndex);
  }

  public List<E> subList(int aFromIndex, int aToIndex) {
    return myInnerList.subList(aFromIndex, aToIndex);
  }
}
