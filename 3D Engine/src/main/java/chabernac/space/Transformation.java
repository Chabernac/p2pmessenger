package chabernac.space;

import chabernac.math.Matrix;
import chabernac.math.MatrixOperations;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;

public class Transformation implements iTransformator{
  private Matrix myTransformationMatrix = MatrixOperations.buildIdentityMatrix();
  private Matrix myInvTransformationMatrix = null;
  private Point3D myOriginTransform = null;
  private Point3D myInvOriginTransform = null;
  
  public Transformation addTransformation(Matrix aTransformation){
    myTransformationMatrix = myTransformationMatrix.multiply(aTransformation);
    return this;
  }
  
  
  public void calculateInverseTransformation(){
    if(myInvTransformationMatrix == null){
      myInvTransformationMatrix = myTransformationMatrix.inverse();
    }
  }
  
  public void calculateOriginTransform(){
    if(myOriginTransform == null){
      myOriginTransform = transform(new Point3D(0,0,0));
    }
  }
  
  public void calculateInverseOriginTtansform(){
    if(myInvOriginTransform == null){
      myInvOriginTransform = inverseTransform(new Point3D(0,0,0));
    }
  }
  
  public Point3D transform(Point3D aPoint){
    return aPoint.multiply( myTransformationMatrix );
//    return MatrixOperations.buildPoint3d((MatrixOperations.buildMatrix(aPoint).multiply(myTransformationMatrix)));
  }
  
  public Point3D inverseTransform(Point3D aPoint){
    calculateInverseTransformation();
    return aPoint.multiply( myInvTransformationMatrix );
//    return MatrixOperations.buildPoint3d((MatrixOperations.buildMatrix(aPoint).multiply(myInvTransformationMatrix)));
  }
  
  /**
   * 
   * @param aVector
   * @return
   */
  public GVector transform(GVector aVector){
    calculateOriginTransform();
//    Point3D thePoint = MatrixOperations.buildPoint3d((MatrixOperations.buildMatrix(aVector).multiply(myTransformationMatrix)));
//    return new GVector(myOriginTransform, thePoint);
    return new GVector(myOriginTransform, new Point3D( aVector ).multiply( myTransformationMatrix ));
  }
  
  public GVector inverseTransform(GVector aVector){
    calculateInverseOriginTtansform();
//    Point3D thePoint = MatrixOperations.buildPoint3d(MatrixOperations.buildMatrix(aVector).multiply(myInvTransformationMatrix));
//    return new GVector(myOriginTransform, thePoint);
    return new GVector(myInvOriginTransform, new Point3D( aVector ).multiply( myInvTransformationMatrix));
  }
  
  public Vertex transform(Vertex aVertex){
    Vertex theVertex = new Vertex(transform(aVertex.myPoint), aVertex.myTextureCoordinate, aVertex.lightIntensity);
    theVertex.normal = transform(aVertex.normal);
    return theVertex;
  }
  
  public Vertex inverseTransform(Vertex aVertex){
    Vertex theVertex = new Vertex(inverseTransform(aVertex.myPoint), aVertex.myTextureCoordinate, aVertex.lightIntensity);
    theVertex.normal = inverseTransform(aVertex.normal);
    return theVertex;
  }
  
  public Matrix getTransformationMatrix(){
    return myTransformationMatrix;
  }
  
  public Matrix getInverseTransformationMatrix(){
    calculateInverseTransformation();
    return myTransformationMatrix;
  }
  
  public static void main(String args[]){
    Point3D theInitPoint = new Point3D(2,1,0);
    
    Transformation theTrans = new Transformation();
    theTrans.getTransformationMatrix().print();
    theTrans.addTransformation(MatrixOperations.buildTranslationMatrix(new GVector(-1,-1,0)));
    theTrans.addTransformation(MatrixOperations.buildRotationMatrix(new Rotation((float)Math.PI/2,0,0)));
    theTrans.addTransformation(MatrixOperations.buildTranslationMatrix(new GVector(1,1,0)));
    theTrans.getTransformationMatrix().print();
    
    System.out.println(theTrans.transform(theInitPoint));
    
  }
  
 
 
}
