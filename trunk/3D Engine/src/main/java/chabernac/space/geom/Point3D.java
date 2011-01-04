package chabernac.space.geom;

import chabernac.math.Matrix;
import chabernac.math.MatrixException;


public class Point3D{
  public float x;
  public float y;
  public float z;
  
  public Point3D(GVector aVector){
    x = aVector.x;
    y = aVector.y;
    z = aVector.z;
  }
  
  public Point3D(PolarPoint3D aPoint){
    x = aPoint.radius * (float)Math.cos(aPoint.beta) * (float)Math.cos(aPoint.alpha);
    y = aPoint.radius * (float)Math.sin(aPoint.beta);
    z = aPoint.radius * (float)Math.cos(aPoint.beta) * (float)Math.sin(aPoint.alpha);
  }
  
  public Point3D(float x, float y, float z){
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public void add(Point3D aPoint){
	  x += aPoint.x;
	  y += aPoint.y;
	  z += aPoint.z;
  }

  public void add(GVector aVector){
	  x += aVector.x;
	  y += aVector.y;
	  z += aVector.z;
  }
  
  public Point3D addition(Point3D aPoint){
  	return new Point3D(x + aPoint.x, y + aPoint.y, z + aPoint.z);
  }
  
  public Point3D addition(GVector aVector){
	  return new Point3D(x + aVector.x, y + aVector.y, z + aVector.z);
  }
  
  public void subtract(Point3D aPoint){
		x -= aPoint.x;
		y -= aPoint.y;
		z -= aPoint.z;
  }

  /*
  public float getX(){ return x; }
  public float getY(){ return y; }
  public float getZ(){ return z; }

  public void setX(float x){ this.x = x; }
  public void setY(float y){ this.y = y; }
  public void setZ(float z){ this.z = z; }
  */

  public String toString(){
    return "<Point3D x: " + x + " y: " + y + " z: " + z + ">";
  }

  public Object clone(){
	  return new Point3D(x, y, z);
  }
  
  public void invert(){
    x = -x;
    y = -y;
    z = -z;
  }
  
  public GVector minus(Point3D aPoint){
    return new GVector(x - aPoint.x,y - aPoint.y,z - aPoint.z);
  }
  
  public void divide(float aFactor){
    x /= aFactor;
    y /= aFactor;
    z /= aFactor;
  }
  
  public Point3D division(float aFactor){
    return new Point3D(x/aFactor, y/aFactor, z/aFactor);
  }
  
  public boolean equals(Object anObject){
    if(anObject instanceof Point3D){
      return equals( (Point3D)anObject);
    } else {
      return super.equals( anObject);
    }
  }
  
  public boolean equals(Point3D aPoint){
	  if(x != aPoint.x) return false;
	  if(y != aPoint.y) return false;
	  if(z != aPoint.z) return false;
	  return true;
  }
  
  /**
   * 
   * origanally we did the following for transforming a point with a matrix transformation:
   * 
   * MatrixOperations.buildPoint3d((MatrixOperations.buildMatrix(aPoint).multiply(myTransformationMatrix)));
   * 
   * but we can do this a lot more optimized:
   * 
   * @param aMatrix
   * @return
   */
  public Point3D multiply(Matrix aMatrix){
    if(aMatrix.getRows() != 4 || aMatrix.getColumns() != 4){
      throw new MatrixException("Can only multiply with a 4 x 4 matrix ");
    }
    
    float[] theSource = aMatrix.getSource();
    
    float theX = x * theSource[0] + y * theSource[4] + z * theSource[8] + theSource[12];
    float theY = x * theSource[1] + y * theSource[5] + z * theSource[9] + theSource[13];
    float theZ = x * theSource[2] + y * theSource[6] + z * theSource[10] + theSource[14];
    
    return new Point3D( theX,theY,theZ );
  }
}