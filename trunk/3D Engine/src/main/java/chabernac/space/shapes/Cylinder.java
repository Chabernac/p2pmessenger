/*
 * Created on 13-jul-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package chabernac.space.shapes;

import org.apache.log4j.Logger;

import chabernac.math.MatrixException;
import chabernac.space.Camera;
import chabernac.space.PolygonException;
import chabernac.space.TranslateException;
import chabernac.space.Vertex;
import chabernac.space.geom.GVector;
import chabernac.space.geom.GeomFunctions;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Rotation;
import chabernac.space.geom.Shape;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Cylinder extends Shape {
  private static final Logger LOGGER = Logger.getLogger(Cylinder.class);
  
	private static final int POLYGONS = 8;
	
	private Point3D myLocation = null;
	private GVector myDirection = null;
	private double myRadius = 0;

	public Cylinder(Point3D aLocation, GVector aDirection, double aRadius) throws TranslateException, PolygonException, MatrixException{
		super(POLYGONS + 2, false);
		myLocation = aLocation;
		myDirection = aDirection;
		myRadius = aRadius;
		createPolygons();
	}
	
	private void createPolygons() throws TranslateException, PolygonException, MatrixException{
		//first create all points of the ground polygon in the xy plane
		//Color theBlueColor = new Color(0,0,200);
		double depth = myDirection.length();
		Polygon ground = new Polygon(POLYGONS);
		Polygon top = new Polygon(POLYGONS);
		for(double alpha=0;alpha<2*Math.PI;alpha += 2 * Math.PI / POLYGONS){
			double x = myRadius * Math.cos(alpha);
			double y = myRadius * Math.sin(alpha);
			ground.addVertex(new Vertex(new Point3D(x, y, 0)));
			top.addVertex(new Vertex(new Point3D(x, y, depth)));
		}
		//ground.color = theBlueColor;
		ground.done();
		addPolygon(ground);
		//top.color = theBlueColor;
		top.done();
		addPolygon(top);
		int j = 0;
		for(int i=0;i<POLYGONS;i++){
			j = (i + 1) % POLYGONS;
			Polygon rectangle = new Polygon(4);
			rectangle.addVertex(ground.w[i]);
			rectangle.addVertex(ground.w[j]);
			rectangle.addVertex(top.w[j]);
			rectangle.addVertex(top.w[i]);
			//rectangle.color = new Color(0, 0, Math.abs(i - POLYGONS / 2) * 256 / POLYGONS);
			rectangle.done();
			addPolygon(rectangle);
		}
		done();
		Point3D theTranslationPoint = (Point3D)myLocation.clone();
		theTranslationPoint.invert();
		Rotation theRotation = GeomFunctions.vector2Rotation(myDirection);
		LOGGER.debug(myDirection.toString() + " converted to rotation: " + theRotation);
		//theRotation.invert();
		Camera theTranslationCamera = new Camera(new Point3D(0,0,0),theRotation , 1);
		translate(theTranslationCamera);
		theTranslationCamera = new Camera(theTranslationPoint,new Rotation(0,0,0) , 1);
		translate(theTranslationCamera);
		LOGGER.debug(myPolygons[1].toString());
	}
	
	public GVector getDirection(){
		return myDirection;
	}
	
}
