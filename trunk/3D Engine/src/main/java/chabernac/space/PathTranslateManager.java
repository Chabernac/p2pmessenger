/*
 * Created on 5-feb-08
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space;

import java.util.HashMap;
import java.util.Map;

import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;

public class PathTranslateManager extends TranslateManager{
	private Point3D[] myPath = null;
	private Map myCurrentDestination = null;
	private float mySpeed;
	
	public PathTranslateManager(Point3D[] aPath, float aSpeed){
		myPath = aPath;
		myCurrentDestination = new HashMap();
		mySpeed = aSpeed;
	}

	protected void translate(iTranslatable aTranslatable) {
		if(!myCurrentDestination.containsKey(aTranslatable)){
			myCurrentDestination.put(aTranslatable, new Integer(0));
		}
		
		int theCurrentDestination = ((Integer)myCurrentDestination.get(aTranslatable)).intValue();
		
		Point3D theDestination = myPath[theCurrentDestination];
		
		GVector theDirection = theDestination.minus(aTranslatable.getCamCenterPoint());
		theDirection.normalize();
		theDirection.multiply(mySpeed);
		
		Camera theTranslationCamera = Camera.makeTranslationCamera(theDirection);
		aTranslatable.translate(theTranslationCamera);
		
		GVector theNewDirection = theDestination.minus(aTranslatable.getCamCenterPoint());
		
		if(theDirection.dotProdukt(theNewDirection) < 0){
			theCurrentDestination = (++theCurrentDestination) % myPath.length;
		}
		
		myCurrentDestination.put(aTranslatable, new Integer(theCurrentDestination));
	}
	

}
