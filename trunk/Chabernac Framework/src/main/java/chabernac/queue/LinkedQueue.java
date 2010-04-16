package chabernac.queue;

import chabernac.data.*;

public class LinkedQueue extends AbstractQueue{

	private Node myFirstNode = null;
	private Node myLastNode = null;
	private int size = 0;


	protected synchronized void putObject(Object aObject) throws QueueException{
		Node theNode = new Node(aObject);
		if(myFirstNode == null){
			myFirstNode = theNode;
		}
		if(myLastNode != null){
			myLastNode.setNext(theNode);
		}
		myLastNode = theNode;
		size++;
	}

	protected synchronized Object getObject() throws QueueException{
		if(myFirstNode != null){
			Object theDataObject = myFirstNode.getData();
			Node nextNode = myFirstNode.getNext();
			myFirstNode.setNext(null);
			myFirstNode = nextNode;
			size--;
			return theDataObject;
		} else {
			throw new QueueException("Queue empty");
		}
	}

	public int size(){
		return size;
	}

	public void clear(){
		Node current = myFirstNode;
		Node next = null;
		while(current != null){
			next = current.getNext();
			current.setNext(null);
			current = next;
		}
		myLastNode = null;
	}


}
