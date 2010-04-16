
package chabernac.space;

import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;


public class RotationManager extends TranslateManager {
	private Camera myRotationCamera = null;
	
	public RotationManager(Rotation aRotation){
		myRotationCamera = new Camera(new Point3D(0,0,0), aRotation, 1);
	}
	
	protected void translate(iTranslatable aTranslatable) {
		Point3D theCenterPoint = (Point3D)aTranslatable.getCenterPoint().clone();
		aTranslatable.translate(new Camera(theCenterPoint, new Rotation(0,0,0), 1));
		aTranslatable.translate(myRotationCamera);
		theCenterPoint.invert();
		aTranslatable.translate(new Camera(theCenterPoint, new Rotation(0,0,0), 1));
	}
}
