/*
 * Created on 12-jan-2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space;

import java.awt.Color;

import chabernac.math.MatrixException;
import chabernac.space.geom.Plane;
import chabernac.space.geom.Point3D;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PointShape implements Comparable{
	public Point3D[] w;
	public Point3D[] c;
	public int mySize;
	public int myCamSize;
	private double myCamDistance;
	public Point3D myCenterPoint;
	public Point3D myCamCenterPoint;
	public Color myColor = Color.black;

	/**
	 * 
	 */
	public PointShape(int aSize) {
		mySize = aSize;
		initialize();
	}
	
	private void initialize(){
		w = new Point3D[mySize];
		c = new Point3D[mySize];
	}
	
	public void world2Cam(Camera aCamera) throws MatrixException{
		for(int i=0;i<w.length;i++){
			//c = new Point3D[w.length];
			c[i] = aCamera.world2Cam(w[i]);
		}
		myCamSize = c.length;
		myCamCenterPoint = aCamera.world2Cam(myCenterPoint);
		myCamDistance = myCamCenterPoint.x * myCamCenterPoint.x +
						myCamCenterPoint.y * myCamCenterPoint.y +
						myCamCenterPoint.z * myCamCenterPoint.z;

	}
	
	public void clip2Frustrum(Frustrum aFrustrum){
		for(int i=0;i<aFrustrum.myPlanes.length;i++){
			 clip2Plane(aFrustrum.myPlanes[i]);
		}
	}
	
	public void clip2Plane(Plane aPlane){
		Point3D[] theTempPoints = new Point3D[w.length];
		int theSize = myCamSize;
		myCamSize = 0;
		for(int i=0;i<theSize;i++){
			//if(c[i] == null)Debug.log(PointShape.class," this camera point is null: " + i);
			if(aPlane.distanceToPoint(c[i]) > 0){
				theTempPoints[myCamSize++] = c[i];
			}
		}
		c = theTempPoints;
		//Debug.log(PointShape.class,"Remaining points: "  + myCamSize);
	}
	
	public void calculateCenterPoint(){
		double x = 0;
		double y = 0;
		double z = 0;
		for(int i=0;i<w.length;i++){
			x += w[i].x;
			y += w[i].y;
			z += w[i].z;
		}
		myCenterPoint = new Point3D(x / w.length, y / w.length, z / w.length);
	}
	
	public int compareTo(Object aObject){
		PointShape theShape = (PointShape)aObject;
		if (myCamDistance == theShape.myCamDistance) return 0;
		else if (myCamDistance < theShape.myCamDistance) return -1;
		else return 1;
	}
	
	public void translate(Camera aCamera) throws MatrixException{
		for(int i=0;i<w.length;i++){
			w[i] = aCamera.world2Cam(w[i]);
		}
		myCenterPoint = aCamera.world2Cam(myCenterPoint);
	}
}
