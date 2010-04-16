/*
 * Created on 25-jul-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.data;

public class LinkNode {
	private LinkNode previous = null;
	private LinkNode next = null;
	private Object value = null;
	
	public LinkNode(Object aValue){
		value = aValue;
	}

	public LinkNode getNext() {
		return next;
	}

	public void setNext(LinkNode next) {
		this.next = next;
		if(next.getPrevious() != this) next.setPrevious(this);
	}

	public LinkNode getPrevious() {
		return previous;
	}

	public void setPrevious(LinkNode previous) {
		this.previous = previous;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
