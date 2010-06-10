/*
 * Created on 8-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.util;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

import chabernac.task.Task;
import chabernac.task.TaskException;

public class TreeIterator implements Iterator {
	private TreeNode myRoot = null;
	private TreeNode myCurrentNode = null;
	private int myCurrentChild = 0;
	private TreeIterator myChildIterator = null;
	
	
	public TreeIterator(TreeNode aNode){
		myRoot = aNode;
	}

	public boolean hasNext() {
		if(myRoot != null && myCurrentNode == null) return true;
		else if(myCurrentChild < myRoot.getChildCount()) return true;
		else if(myChildIterator != null && myChildIterator.hasNext()) return true;
		return false;
	}

	public Object next() {
		if(myCurrentNode == null){
			myCurrentNode = myRoot;
		} else if(myChildIterator == null) {
			myCurrentNode = myRoot.getChildAt(myCurrentChild++);
			if(myCurrentNode.getChildCount() > 0){
				myChildIterator = new TreeIterator(myCurrentNode);
			}
		}
		if(myChildIterator != null){
			if(!myChildIterator.hasNext()) myChildIterator = null;
			else return myChildIterator.next();
		}
		return myCurrentNode;
	}

	public void remove() {}
	
	public static void main(String args[]){
		Task theRootTask = new Task(1, "1");
		Task theTask1_1 = new Task(1,"1.1");
		Task theTask1_2 = new Task(1,"1.2");
		Task theTask1_2_1 = new Task(1,"1.2.1");
		Task theTask1_2_2 = new Task(1,"1.2.2");
		try{
			theRootTask.addSubTask(theTask1_1);
			theRootTask.addSubTask(theTask1_2);
			theTask1_2.addSubTask(theTask1_2_1);
			theTask1_2.addSubTask(theTask1_2_2);
			TreeIterator theIterator = new TreeIterator(theRootTask);
			while(theIterator.hasNext()){
				System.out.println(theIterator.next());
			}
		}catch(TaskException e){
			e.printStackTrace();
		}
		
		
	}

}
