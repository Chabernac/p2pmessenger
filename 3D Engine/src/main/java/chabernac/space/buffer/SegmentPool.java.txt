/*
 * Created on 25-jul-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.buffer;

import java.util.Stack;

public class SegmentPool {
	private Stack<Segment> myPool = null;
	
	public SegmentPool(){
		myPool = new Stack<Segment>();
	}
	
	public Segment getSegment(){
		if(myPool.isEmpty()) growPool();
		return myPool.pop();
	}
	
	public void freeSegment(Segment aSegment){
		myPool.push(aSegment);
	}
	
	private void growPool(){
		for(int i=0;i<100;i++){
			myPool.push(new Segment());
		}
	}
	
	

}
