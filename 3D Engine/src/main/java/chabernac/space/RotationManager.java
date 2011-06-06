
package chabernac.space;

import chabernac.math.MatrixOperations;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Rotation;


public class RotationManager extends TranslateManager {
  private Rotation myRotation = null;
  private iTranslatable myRotationCenter = null;

  public RotationManager(Rotation aRotation){
    setRotation(aRotation); 
  }

  protected void translate(iTranslatable aTranslatable) {
    GVector theVector;
    if(myRotationCenter != null){
      theVector = new GVector(myRotationCenter.getCamCenterPoint());
    } else {
      theVector = new GVector(aTranslatable.getCamCenterPoint());
    }

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

  public iTranslatable getRotationCenter() {
    return myRotationCenter;
  }

  public void setRotationCenter(iTranslatable anRotationCenter) {
    myRotationCenter = anRotationCenter;
  }
}
