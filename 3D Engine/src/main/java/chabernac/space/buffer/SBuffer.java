/*
 * Created on 25-jul-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.buffer;

import java.awt.Color;
import java.awt.Graphics;

import chabernac.data.LinkNode;
import chabernac.space.Polygon2D;
import chabernac.space.Vertex2D;
import chabernac.utils.Debug;

public class SBuffer extends AbstractBuffer{
	private LinkNode[] segmentRoot = null;
	private double myMinY, myMaxY;
	//Graphics object is only for debugging reasons so that we can visualise the process of the segment creation.
	private Graphics g = null;
	public static final int DRAW_POLYGONS = 1;
	public static final int DRAW_POLYGONS_1_BY_1 = 1 << 1;
	public static final int DRAW_POLYGONS_SLOW = 1 << 2;
	
	public SBuffer(int width, int height){
		super(width, height);
		segmentRoot = new LinkNode[height];
		clearBuffer();
	}
	
	protected void clearBuffer(){
		for(int i=0;i<myHeight;i++){
			segmentRoot[i] = new LinkNode(new Segment(-1,-1,-1,-1,-1,-1,Color.black.getRGB(), null));
			segmentRoot[i].setPrevious(segmentRoot[i]);
		}
	}
	
	public void addSegment(Segment aSegment, int y){
		if(getDebugMode() >= DRAW_POLYGONS) Debug.log(this, "Adding segment at " + y + " :" + aSegment);
 		LinkNode root = segmentRoot[y];
		LinkNode current = root;
		LinkNode start = null;
		LinkNode end = null;
		int counter = 1;
		
		//find the segment after which the new one must be inserted
		while( ((Segment)current.getValue()).getXStart() < aSegment.getXStart() && current.getNext() != null){
			current = current.getNext();
		}
		if( ((Segment)current.getValue()).getXStart() >=  aSegment.getXStart() )start = current.getPrevious();
		else start = current;
		
		
		//find the segment before which the new one must be inserted
		current = start;
		while( current != null && ((Segment)current.getValue()).getXEnd() < aSegment.getXEnd() ){
			current = current.getNext();
			if(current != null) counter++;
		}
		//end can be null if the segment has to be inserted on the end of the linked list
		end = current;
		
		//store all segments from start to end in a seperata array 
		Segment[] theSegmentsStartToEnd = new Segment[counter];
		current = start;
		for(int i=0;i<counter;i++){
			theSegmentsStartToEnd[i] = (Segment)current.getValue();
			current = current.getNext();
		}
		
		LinkNode theFirstSegmentAfterEnd = null;
		if(end != null) theFirstSegmentAfterEnd = end.getNext();
		Segment theSegmentToInsert = aSegment;
		current = start.getPrevious();
		for(int i=0;i<theSegmentsStartToEnd.length;i++){
			Segment[] theIntersections = theSegmentsStartToEnd[i].intersect(theSegmentToInsert);
			for(int j=0;j<theIntersections.length;j++){
				if(current != root || (current == root && root.getValue() != theIntersections[j])) {
					current.setNext(new LinkNode(theIntersections[j]));
					current = current.getNext();
				}
			}
			current = current.getPrevious();
			theSegmentToInsert = theIntersections[theIntersections.length - 1];
		}
		current = current.getNext();
		if(theFirstSegmentAfterEnd != null) current.setNext(theFirstSegmentAfterEnd);
		
		if(getDebugMode() >= DRAW_POLYGONS){
			g.drawImage(getImage(), 0,0, null);
			char[] theHeight = Integer.toString(y).toCharArray();
			g.drawChars(theHeight, 0, theHeight.length, 0, y);
			if((getDebugMode() & DRAW_POLYGONS_SLOW) == DRAW_POLYGONS_SLOW){
				try{
					Thread.sleep(1000);
				}catch(Exception e){}
			}
		}
		
	}
	
	public void drawLine(Vertex2D aStartVertex, Vertex2D anEndVertex, int aColor){
		
	}

	public void drawPolygon(Polygon2D aPolygon) {
		if((getDebugMode() & DRAW_POLYGONS_1_BY_1) == DRAW_POLYGONS_1_BY_1) clear();
		findMinMaxY(aPolygon);
		Vertex2D[] theScanLine;
		Color theColor = aPolygon.getColor();
		for(int y = (int)Math.ceil(myMinY);y <= myMaxY;y++){
			theScanLine = aPolygon.intersectHorizontalLine(y);
			if(theScanLine.length == 2){
				addSegment(new Segment(theScanLine[0], theScanLine[1], theColor.getRGB(), aPolygon.getTexture() ), y);
			}
		}
	}

	protected void prepareImage(){
		for(int i=0;i<myHeight;i++){
			LinkNode current = segmentRoot[i];
			while(current != null){
				if(current.getPrevious() != current) drawSegment((Segment)current.getValue(), i);
				current = current.getNext();
			}
		}
	}
	
	public void findMinMaxY(Polygon2D aPolygon){
		double[] minmax = BufferTools.findMinMaxY(aPolygon);
		myMinY = minmax[0];
		myMaxY = minmax[1];
	}

	
	public void print(){
		for(int i=0;i<myHeight;i++){
			LinkNode current = segmentRoot[i];
			while(current != null){
				System.out.println(current.getValue());
				current = current.getNext();
			}
		}
	}
	
	public void setGraphics(Graphics g){
		this.g = g;
	}
	
	public static void main(String args[]){
		SBuffer theBuffer = new SBuffer(100, 1);

//		theBuffer.addSegment(new Segment(1,5,1,3,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(4,9,5,5,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(2,6,8,8,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(7,11,4,8,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(9,11,10,9,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(12,16,1,4,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(14,17,4,1,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(19,23,9,13,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(20,22,12,10,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(18,24,8,4,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(18,20,5,5,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(17,25,3,3,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(6,10,3,3,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(28,32,11,10,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(32,36,10,12,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(25,28,14,11,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(26,30,8,4,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(30,35,4,7,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(25,38,6,6,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(2,8,4,4,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(7,9,2,2,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(11,14,6,3,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(14,17,3,5,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(15,18,9,6,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(18,22,6,7,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(10,23,4,4,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(1,2,8,7,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(2,5,7,11,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(5,8,11,8,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(8,11,8,9,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(0,12,12,12,0,0,Color.black.getRGB()), 0);
		
//		theBuffer.addSegment(new Segment(7,9,2,2,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(4,6,2,2,0,0,Color.black.getRGB()), 0);
//		theBuffer.addSegment(new Segment(1,3,2,2,0,0,Color.black.getRGB()), 0);
		theBuffer.addSegment(new Segment(213.0846456296158,229.94850480226927,3408.390682379771,3398.5236950059198,0,0,Color.black.getRGB(), null), 0);
		theBuffer.addSegment(new Segment(229.9485048022692,234.0331500827991,3398.5236950059198,3455.5510835816362,0,0,Color.black.getRGB(), null), 0);
		
		
		
		
		theBuffer.print();
	}

}

