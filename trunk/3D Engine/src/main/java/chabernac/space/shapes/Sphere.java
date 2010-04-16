package chabernac.space.shapes;

import chabernac.space.geom.Point3D;

public class Sphere{
	public Point3D myCenterPoint = null;
	public float myRadius;

	public Sphere(Point3D aCenterPoint, float aRadius){
		myCenterPoint = aCenterPoint;
		myRadius = aRadius;
	}
}