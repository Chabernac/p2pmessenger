/*
 * Created on 5-feb-08
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space;

import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;

public class OneLocationTranslateManager extends TranslateManager{
	private Point3D myDestination = null;
	private float mySpeed;
	
	public OneLocationTranslateManager(Point3D aDestination, float aSpeed){
		myDestination = aDestination;
		mySpeed = aSpeed;
	}

	protected void translate(iTranslatable aTranslatable) {
		
		GVector theDirection = myDestination.minus(aTranslatable.getCamCenterPoint());
		if(theDirection.length() > mySpeed){
			theDirection.normalize();
			theDirection.multiply(mySpeed);
			
			Camera theTranslationCamera = Camera.makeTranslationCamera(theDirection);
			aTranslatable.translate(theTranslationCamera);
		}
	}

	public Point3D getMyDestination() {
		return myDestination;
	}

	public void setDestination(Point3D aDestination) {
		this.myDestination = aDestination;
	}

	public float getSpeed() {
		return mySpeed;
	}

	public void setpeed(float aSpeed) {
		this.mySpeed = aSpeed;
	}
	
	
	

}
