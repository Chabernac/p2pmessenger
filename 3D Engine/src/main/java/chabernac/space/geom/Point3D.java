package chabernac.space.geom;


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
}