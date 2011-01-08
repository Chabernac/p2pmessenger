package chabernac.space.geom;

import chabernac.math.Matrix;
import chabernac.math.MatrixException;


public class GVector{
  public float x,y,z;

  public GVector(Point3D aPoint){
    this(aPoint.x, aPoint.y, aPoint.z);
  }

  public GVector(float x, float y, float z){
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public GVector(GVector aVector1, GVector aVector2){
    GVector theVector = aVector1.produkt(aVector2);
    x = theVector.x;
    y = theVector.y;
    z = theVector.z;
  }

  public GVector(Point3D aPoint1, Point3D aPoint2){
    this.x = aPoint2.x - aPoint1.x;
    this.y = aPoint2.y - aPoint1.y;
    this.z = aPoint2.z - aPoint1.z;
  }

  public GVector(Point3D aPoint1, Point3D aPoint2, Point3D aPoint3){
    this(new GVector(aPoint1, aPoint2), new GVector(aPoint1, aPoint3));
  }

  /*
   * Create a Vector which is the normal vector of the plane formed with the
   * points aPoint1, aPoint2 and aPoint2.
   * aPoint4 determines the orientation of the vector.  The resulting vector
   * will point away from aPoint4.
   */
  public GVector(Point3D aPoint1, Point3D aPoint2, Point3D aPoint3, Point3D aPoint4, boolean toWardsPoint){
    this(aPoint1, aPoint2, aPoint3);
    GVector theVector = new GVector(aPoint1, aPoint4);
    if(toWardsPoint && dotProdukt(theVector) < 0 || !toWardsPoint && dotProdukt(theVector) > 0) invert();
    //Debug.log(this," for points: " + aPoint1.toString() + "," + aPoint2.toString() + "," + aPoint3.toString());
  }

  public GVector(){
    this(0F, 0F, 0F);
  }

  public String toString(){
    return "< x: " + x + " y: " + y + " z: " + z + " >";
  }

  public void normalize(){
    float c = (float)Math.sqrt(x * x  + y * y + z * z);
    if(c > 0){
      x = x / c;
      y = y / c;
      z = z / c;
    }
  }

  public GVector norm(){
    float length = length();
    return new GVector(x / length, y / length, z / length);
//    float theInvApprLength = FastMath.invSqrt( dotProdukt( this ) );
//    return new GVector(x * theInvApprLength, y * theInvApprLength, z * theInvApprLength);
  }

  public void invert(){
    x = -x;
    y = -y;
    z = -z;
  }

  public GVector inv(){
    return new GVector(-x, -y, -z);
  }

  public float dotProdukt(GVector aVector){
    return x * aVector.x + y * aVector.y + z * aVector.z;
  }

  public GVector produkt(GVector aVector){
    return new GVector(y * aVector.z - z * aVector.y, z * aVector.x - x * aVector.z, x * aVector.y - y * aVector.x);
  }

  public float length(){
    return (float)Math.sqrt(dotProdukt(this));
  }
  
  public void multiply(float afloat){
    x *= afloat;
    y *= afloat;
    z *= afloat;
  }

  public GVector multip(float afloat){
    return new GVector(	x * afloat, y * afloat, z * afloat);
  }
  
  public GVector division(float aFloat) {
    return new GVector(  x / aFloat, y / aFloat, z / aFloat);
  }

  public GVector addition(GVector aVector){
    return new GVector(x + aVector.x, y + aVector.y, z + aVector.z);
  }

  public void add(GVector aVector){
    x += aVector.x;
    y += aVector.y;
    z += aVector.z;
  }

  public Object clone(){
    return new GVector(x, y, z);
  }

  public boolean equals(Object anObject){
    if(anObject instanceof GVector){
      GVector theVector = (GVector)anObject;
      if(x != theVector.x) return false;
      if(y != theVector.y) return false;
      if(z != theVector.z) return false;
      return true;
    }
    return false;
  }
  
  public GVector multiply(Matrix aMatrix){
    if(aMatrix.getRows() != 4 || aMatrix.getColumns() != 4){
      throw new MatrixException("Can only multiply with a 4 x 4 matrix ");
    }

    float[] theSource = aMatrix.getSource();
    float theX = x * theSource[0] + y * theSource[4] + z * theSource[8] + theSource[12];
    float theY = x * theSource[1] + y * theSource[5] + z * theSource[9] + theSource[13];
    float theZ = x * theSource[2] + y * theSource[6] + z * theSource[10] + theSource[14];
    
    return new GVector( theX,theY,theZ );
  }
}