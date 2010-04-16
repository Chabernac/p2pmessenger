
package chabernac.space;

import chabernac.space.geom.GVector;
import chabernac.space.geom.Rotation;


public class RotationManager extends TranslateManager {
  private Rotation myRotation = null;
	
	public RotationManager(Rotation aRotation){
    setRotation(aRotation); 
	}
	
	protected void translate(iTranslatable aTranslatable) {
    GVector theVector = new GVector(aTranslatable.getCenterPoint());
    
    Transformation theTransform = new Transformation();
    theTransform.addTransformation(MatrixOperations.buildTranslationMatrix(theVector.inv()));
    theTransform.addTransformation(MatrixOperations.buildRotationMatrix(myRotation));
    theTransform.addTransformation(MatrixOperations.buildTranslationMatrix(theVector));
    aTranslatable.translate(theTransform);
	}
  
  public void setRotation(Rotation aRotation){
    myRotation = aRotation;
  }
  
  public Rotation getRotation(){
    return myRotation;
  }
}
