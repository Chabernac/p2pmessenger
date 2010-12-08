package chabernac.space.geom;


public class Point3D{
  public double x;
  public double y;
  public double z;
  
  public Point3D(GVector aVector){
    x = aVector.x;
    y = aVector.y;
    z = aVector.z;
  }
  
  public Point3D(PolarPoint3D aPoint){
    x = aPoint.radius * Math.cos(aPoint.beta) * Math.cos(aPoint.alpha);
    y = aPoint.radius * Math.sin(aPoint.beta);
    z = aPoint.radius * Math.cos(aPoint.beta) * Math.sin(aPoint.alpha);
  }
  
  public Point3D(double x, double y, double z){
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
  public double getX(){ return x; }
  public double getY(){ return y; }
  public double getZ(){ return z; }

  public void setX(double x){ this.x = x; }
  public void setY(double y){ this.y = y; }
  public void setZ(double z){ this.z = z; }
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
  
  public void divide(double aFactor){
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