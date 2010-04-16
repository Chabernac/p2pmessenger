/*
 * Created on 18-jul-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.buffer;

import java.awt.Color;

import chabernac.space.Polygon2D;
import chabernac.space.Vertex2D;

public class ZBuffer extends AbstractBuffer{
	private double myZBuffer[][];
	private double myMinY, myMaxY;
	private int cycle = 0;
	
	
	public ZBuffer(int aWidth, int aHeight){
		super(aWidth, aHeight);
		myZBuffer = new double[aWidth][aHeight];
		clearBuffer();
	}
	
	protected void clearBuffer(){
		cycle = cycle + 1;
		if(cycle == 0){
			for(int x=0;x<myWidth;x++){
				for(int y=0;y<myHeight;y++)
				myZBuffer[x][y] = 0;
			}
		}
	}
	
	public void setValueAt(int x, int y, double aDepth, int aColor, boolean ignoreDepth){
		//aColor = Color.red;
		//System.out.println("Setting value at: " + x + ", " + y + " at color " + aColor.getRed() + ","  + aColor.getGreen() + "," + aColor.getBlue());
		
		
		//TODO hier
		//if(x >= myWidth || y >= myHeight || x < 0 || y < 0 || aDepth < 1){
		if(x >= myWidth || y >= myHeight || x < 0 || y < 0 || aDepth < 0 || aDepth > 1){
			//System.out.println("An attempt was made to set a point at: " + x  + ", " + y);
			return;
		}
		
		//double invDepth = 1 / aDepth;
		//int index = getBufferIndex(x, y);
		if(ignoreDepth || aDepth  + cycle > myZBuffer[x][y]){
			myZBuffer[x][y] = aDepth + cycle;
			setPixelAt(x, y, aColor);
		}
	}
	
	public void drawPolygon(Polygon2D aPolygon) {
		findMinMaxY(aPolygon);
		Vertex2D[] theScanLine;
		Color theColor = aPolygon.getColor();
		for(int y = (int)Math.ceil(myMinY);y <= myMaxY;y++){
			theScanLine = aPolygon.intersectHorizontalLine(y);
			if(theScanLine.length == 2){
				drawSegment(new Segment(theScanLine[0],theScanLine[1], theColor.getRGB(), aPolygon.getTexture() ), y);
			}
		}
	}
	
	public void drawLine(Vertex2D aStartPoint, Vertex2D anEndEPoint, int aColor){
		Vertex2D theTempVertex = null;
		
		double xDiff =  anEndEPoint.getPoint().x - aStartPoint.getPoint().x;
		double yDiff =  anEndEPoint.getPoint().y - aStartPoint.getPoint().y;
		
		if(Math.abs(xDiff) > Math.abs(yDiff)){
			if(aStartPoint.getPoint().x > anEndEPoint.getPoint().x){
				theTempVertex = aStartPoint;
				aStartPoint = anEndEPoint;
				anEndEPoint = theTempVertex;
			}
			double zDiff = anEndEPoint.getInverseDepth() - aStartPoint.getInverseDepth();
			double deltaY = yDiff / xDiff;
			double deltaZ = zDiff / xDiff;
			double y = aStartPoint.getPoint().y;
			double z = aStartPoint.getInverseDepth();
			for(int x=(int)Math.ceil(aStartPoint.getPoint().x);x<(int)Math.floor(anEndEPoint.getPoint().x);x++){
				setValueAt(x, (int)y, (int)z, aColor, true);
				y += deltaY;
				z += deltaZ;
			}
			
		} else {
			if(aStartPoint.getPoint().y > anEndEPoint.getPoint().y){
				theTempVertex = aStartPoint;
				aStartPoint = anEndEPoint;
				anEndEPoint = theTempVertex;
			}
			double zDiff = anEndEPoint.getInverseDepth() - aStartPoint.getInverseDepth();
			double deltaX = xDiff / yDiff;
			double deltaZ = zDiff / yDiff;
			double x = aStartPoint.getPoint().x;
			double z = aStartPoint.getInverseDepth();
			for(int y=(int)Math.ceil(aStartPoint.getPoint().y);y<(int)Math.floor(anEndEPoint.getPoint().y);y++){
				setValueAt((int)x, y, (int)z, aColor, true);
				x += deltaX;
				z += deltaZ;
			}
		}
	}
	
	public void findMinMaxY(Polygon2D aPolygon){
		double[] minmax = BufferTools.findMinMaxY(aPolygon);
		myMinY = minmax[0];
		myMaxY = minmax[1];
	}

	protected void prepareImage() {}
	
	


}
