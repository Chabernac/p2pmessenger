/*
 * Created on 16-jul-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package chabernac.space;

import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;
import chabernac.space.shapes.Cylinder;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CylinderRotationManager extends TranslateManager {
	private float myRoll = 0;
	
	public CylinderRotationManager(float aRoll){
		myRoll = aRoll;
	}
	
	protected void translate(iTranslatable aTranslatable) {
		Point3D theCenterPoint = (Point3D)aTranslatable.getCamCenterPoint().clone();
		aTranslatable.translate(new Camera(theCenterPoint, new Rotation(0,0,0), 1));
		Cylinder theCylinder = (Cylinder)aTranslatable;
		GVector theDirection = theCylinder.getDirection();
		//Rotation theRotation = GeomFunctions.vector2Rotation(theDirection);
		//theRotation.setRoll(myRoll);
		//aTranslatable.translate(new Camera(new Point3D(0,0,0), theRotation, 1));
		//aTranslatable.translate(new Camera(new Point3D(0,0,0), new Rotation(myRoll, 0,0), 1));
		//theRotation.invert();
		//aTranslatable.translate(new Camera(new Point3D(0,0,0), theRotation, 1));
		theCenterPoint.invert();
		aTranslatable.translate(new Camera(theCenterPoint, new Rotation(0,0,0), 1));
	}
	
	public static void main(String args[]){
		Point3D thePoint = new Point3D(100,100,100);
		Rotation theRotation = new Rotation((float)0.2,(float)0.3,(float)0.4);
		Camera theCamera = new Camera(new Point3D(0,0,0), theRotation, 1);
		theRotation.invert();
		Camera theCamera2 = new Camera(new Point3D(0,0,0), theRotation, 1);
		Point3D thePoint2 = theCamera.world2Cam(thePoint);
		Point3D thePoint3 = theCamera.world2Cam(thePoint2);
		System.out.println(thePoint.toString());
		System.out.println(thePoint3.toString());
	}
}
