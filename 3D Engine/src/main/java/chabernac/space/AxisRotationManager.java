package chabernac.space;

import chabernac.math.Matrix;
import chabernac.math.MatrixOperations;
import chabernac.space.geom.Line3D;
import chabernac.space.geom.Rotation;

public class AxisRotationManager extends TranslateManager {
  private Transformation myTransformation = null;
  
  
  public AxisRotationManager(Line3D anAxis, float aRotation){
    CoordinateSystem theSystem = new CoordinateSystem(anAxis);
    myTransformation = theSystem.getTransformator();
    Matrix theInvMatrix = myTransformation.getTransformationMatrix().inverse();
    myTransformation.addTransformation(MatrixOperations.buildRotationMatrix(new Rotation(aRotation, 0, 0)));
    myTransformation.addTransformation(theInvMatrix);
  }

  protected void translate(iTranslatable aTranslatable) {
    aTranslatable.translate(myTransformation);
  }

}
