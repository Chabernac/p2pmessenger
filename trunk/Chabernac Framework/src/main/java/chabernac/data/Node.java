package chabernac.data;

public class Node{
		private Node previous = null;
		private Node next = null;
		private Object myData = null;

		public Node(Object aDataObject, Node previous, Node next){
			this.myData = aDataObject;
			this.previous = previous;
			this.next = next;
		}

		public Node(Object aDataObject){
			this(aDataObject, null, null);
		}

		public Node getPrevious(){
			return previous;
		}

		public Node getNext(){
			return next;
		}

		public Object getData(){
			return myData;
		}

		public void setPrevious(Node previous){
			this.previous = previous;
		}

		public void setNext(Node next){
			this.next = next;
		}

		public void setData(Object aData){
			myData = aData;
		}


}