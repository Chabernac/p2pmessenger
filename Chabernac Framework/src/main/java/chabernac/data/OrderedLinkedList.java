package chabernac.data;

import java.util.LinkedList;
import java.util.ListIterator;

public class OrderedLinkedList extends LinkedList{
	public Comparable add(Comparable aObject){
		if(size() == 0){
			super.add(aObject);
			return aObject;
		}
		ListIterator theListIterator = listIterator(0);
		Comparable theComparable = null;
		while(theListIterator.hasNext()){
			theComparable = (Comparable)theListIterator.next();
			int compare = theComparable.compareTo(aObject);
			if( compare < 0){
				//Debug.log(this,theComparable.toString() + " is smaller than " + aObject.toString());
				//break;
			} else if(compare > 0){
				//Debug.log(this,theComparable.toString() + " is equal or greater than " + aObject.toString());
				theListIterator.previous();
				theListIterator.add(aObject);
				return aObject;
			} else {
				return theComparable;
			}
		}
		//Debug.log(this,"next index: " + theListIterator.nextIndex());
		//theListIterator.previous();
		theListIterator.add(aObject);
		return aObject;
	}

	public boolean contains(Comparable aObject){
		if(find(aObject) != null){
			return true;
		}
		return false;
	}

	public void remove(Comparable aObject){
		Object theObject = find(aObject);
		if(aObject != null){
			super.remove(aObject);
		}
	}

	public Object find(Comparable aObject){
		ListIterator theListIterator = listIterator(0);
		Comparable theComparable = null;
		while(theListIterator.hasNext()){
			theComparable = (Comparable)theListIterator.next();
			if(theComparable.compareTo(aObject) == 0){
				return theComparable;
			}
		}
		return null;
	}

	public boolean add(Object aObject){
		if(aObject instanceof Comparable){
			Object theObject = add((Comparable)aObject);
			if(theObject != aObject){
				return false;
			} else {
				return true;
			}
		} else {
			return super.add(aObject);
		}
	}
}