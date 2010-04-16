/*
 * Created on 6-aug-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.shapes;

import chabernac.space.Polygon;
import chabernac.space.Shape;
import chabernac.space.Vertex;
import chabernac.space.geom.Point3D;

public class SinglePolygonShape extends Shape {
	private Point3D myOrigin = null;
	private int myWidth;
	private int myHeight;
	
	public SinglePolygonShape(Point3D anOrigin){
		super(1);
		myOrigin = anOrigin;
	}
	
	public SinglePolygonShape(Point3D aOrigin, int aWidth, int aHeight){
		super(1);
		myOrigin = aOrigin;
		myWidth = aWidth;
		myHeight = aHeight;
		createPolygons();
	}
	
	protected void createPolygons(){
		Polygon thePolygon = new Polygon(4);
		
		thePolygon.addVertex(new Vertex(myOrigin));
		thePolygon.addVertex(new Vertex(new Point3D(myOrigin.x + myWidth, myOrigin.y, myOrigin.z)));
		thePolygon.addVertex(new Vertex(new Point3D(myOrigin.x + myWidth, myOrigin.y - myHeight, myOrigin.z)));
		thePolygon.addVertex(new Vertex(new Point3D(myOrigin.x, myOrigin.y - myHeight, myOrigin.z)));
		
		thePolygon.doubleSided = true;
		thePolygon.done();
		addPolygon(thePolygon);
		done();
	}
	
	protected void setWidth(int aWidth){
		myWidth = aWidth;
	}
	
	protected void setHeight(int aHeight) {
		myHeight = aHeight;
	}
}
