package chabernac.space.geom;


public class GVector{
  public double x,y,z;

  public GVector(Point3D aPoint){
    this(aPoint.x, aPoint.y, aPoint.z);
  }

  public GVector(double x, double y, double z){
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
    double c = Math.sqrt(x * x  + y * y + z * z);
    if(c > 0){
      x = x / c;
      y = y / c;
      z = z / c;
    }
  }

  public GVector norm(){
    double length = length();
    return new GVector(x / length, y / length, z / length);
  }

  public void invert(){
    x = -x;
    y = -y;
    z = -z;
  }

  public GVector inv(){
    return new GVector(-x, -y, -z);
  }

  public double dotProdukt(GVector aVector){
    return x * aVector.x + y * aVector.y + z * aVector.z;
  }

  public GVector produkt(GVector aVector){
    return new GVector(y * aVector.z - z * aVector.y, z * aVector.x - x * aVector.z, x * aVector.y - y * aVector.x);
  }

  public double length(){
    return Math.sqrt(dotProdukt(this));
  }

  public void multiply(double adouble){
    x *= adouble;
    y *= adouble;
    z *= adouble;
  }

  public GVector multip(double adouble){
    return new GVector(	x * adouble, y * adouble, z * adouble);
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
}