package chabernac.space;

import java.awt.Color;

import chabernac.space.geom.Point3D;

public class ShapeFactory{

	public static void makeCube(Shape aShape, Point3D anOrigPoint, float aWidth, float aHeight, float aDepth) throws PolygonException{
		Point3D thePoint1 = anOrigPoint;
		Point3D thePoint2 = new Point3D(anOrigPoint.x + aWidth, anOrigPoint.y, anOrigPoint.z);
		Point3D thePoint3 = new Point3D(thePoint2.x, thePoint2.y + aHeight, anOrigPoint.z);
		Point3D thePoint4 = new Point3D(anOrigPoint.x, thePoint3.y, anOrigPoint.z);
		Point3D thePoint5 = new Point3D(anOrigPoint.x, anOrigPoint.y, anOrigPoint.z + aDepth);
		Point3D thePoint6 = new Point3D(thePoint2.x, anOrigPoint.y, thePoint5.z);
		Point3D thePoint7 = new Point3D(thePoint6.x , thePoint3.y, thePoint5.z);
		Point3D thePoint8 = new Point3D(thePoint5.x , thePoint3.y, thePoint5.z);

		Polygon thePolygon = null;

		thePolygon = new Polygon(4);
		thePolygon.addVertex(new Vertex(thePoint1));
		thePolygon.addVertex(new Vertex(thePoint2));
		thePolygon.addVertex(new Vertex(thePoint3));
		thePolygon.addVertex(new Vertex(thePoint4));
		thePolygon.color = new Color(200,0,0);
		thePolygon.done();
		aShape.addPolygon(thePolygon);

		thePolygon = new Polygon(4);
		thePolygon.addVertex(new Vertex(thePoint1));
		thePolygon.addVertex(new Vertex(thePoint2));
		thePolygon.addVertex(new Vertex(thePoint6));
		thePolygon.addVertex(new Vertex(thePoint5));
		thePolygon.color = new Color(0,200,0);
		thePolygon.done();
		aShape.addPolygon(thePolygon);


		thePolygon = new Polygon(4);
		thePolygon.addVertex(new Vertex(thePoint2));
		thePolygon.addVertex(new Vertex(thePoint3));
		thePolygon.addVertex(new Vertex(thePoint7));
		thePolygon.addVertex(new Vertex(thePoint6));
		thePolygon.color = new Color(0,0,200);
		thePolygon.done();
		aShape.addPolygon(thePolygon);

		thePolygon = new Polygon(4);
		thePolygon.addVertex(new Vertex(thePoint1));
		thePolygon.addVertex(new Vertex(thePoint4));
		thePolygon.addVertex(new Vertex(thePoint8));
		thePolygon.addVertex(new Vertex(thePoint5));
		thePolygon.color = new Color(200,200,0);
		thePolygon.done();
		aShape.addPolygon(thePolygon);

		thePolygon = new Polygon(4);
		thePolygon.addVertex(new Vertex(thePoint5));
		thePolygon.addVertex(new Vertex(thePoint6));
		thePolygon.addVertex(new Vertex(thePoint7));
		thePolygon.addVertex(new Vertex(thePoint8));
		thePolygon.color = new Color(0,200,200);
		thePolygon.done();
		aShape.addPolygon(thePolygon);

		thePolygon = new Polygon(4);
		thePolygon.addVertex(new Vertex(thePoint4));
		thePolygon.addVertex(new Vertex(thePoint3));
		thePolygon.addVertex(new Vertex(thePoint7));
		thePolygon.addVertex(new Vertex(thePoint8));
		thePolygon.color = new Color(200,0,200);
		thePolygon.done();
		aShape.addPolygon(thePolygon);
		aShape.done();
	}
}