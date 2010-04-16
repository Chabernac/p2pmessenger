package chabernac.space.geom;


public class Plane{
  public GVector myNormalVector;
  public double myD = 0F;
  
  public Plane(GVector aNormalVector, Point3D aPoint){
    myNormalVector = aNormalVector;
    myNormalVector.normalize();
    myD = - distanceToPoint(aPoint);
  }

  public Plane(Point3D aPoint1, Point3D aPoint2, Point3D aPoint3, Point3D aPoint4){
	  this(new GVector(aPoint1, aPoint2, aPoint3, aPoint4, true), aPoint1);
  }
  
  public Plane(Point3D aPoint1, Point3D aPoint2, Point3D aPoint3){
    this(new GVector(aPoint1, aPoint2, aPoint3), aPoint1);
  }
  
  /*
   * Vlak gecontstrueerd adhv 2 punten.
   * Het vlak is het middelloodvlak tussen de 2 punten
   * De normaal vector van het vlak wijst van punt 1 naar punt 2
   */
  public Plane(Point3D aPoint1, Point3D aPoint2){
    myNormalVector = aPoint2.minus(aPoint1);
    myNormalVector.normalize();
    aPoint1.add(aPoint2);
    aPoint2.divide(2);
    myD = - distanceToPoint(aPoint2);
  }

  public double distanceToPoint(Point3D aPoint){
    return myNormalVector.x * aPoint.x + myNormalVector.y * aPoint.y + myNormalVector.z * aPoint.z + myD;
  }
  
  /**
   * aPoint is een punt op het XY vlake
   * the functie geeft het, evenwijdig aan de z as, op het vlak geprojecteerde punt terug
   * 
   * !!this method will result in a division by zero if the plane is parallel to the XY plane!
   * @param aPoint
   * @return
   */
  public Point3D pointOnPlane(Point2D aPoint){
    double z = -(myD + (myNormalVector.x * aPoint.x + myNormalVector.y * aPoint.y))  / myNormalVector.z;
    return new Point3D(aPoint.x, aPoint.y, z);
  }
  
  public Line3D intersect(Plane aPlane){
    GVector theDirection = myNormalVector.produkt(aPlane.myNormalVector);
    double div = myNormalVector.x * aPlane.myNormalVector.y - aPlane.myNormalVector.x * myNormalVector.y;
    double x = ( (myNormalVector.y * aPlane.myD  - aPlane.myNormalVector.y * myD) / div);
    double y = ( (aPlane.myNormalVector.x * myD  - myNormalVector.x * aPlane.myD) / div);
    double z = 0;
    return new Line3D(new Point3D(x,y,z), theDirection);
  }
  
  public String toString(){
  	StringBuffer theBuffer = new StringBuffer();
  	theBuffer.append("<Plane vgl=");
	theBuffer.append(myNormalVector.x);
	theBuffer.append("X + ");
	theBuffer.append(myNormalVector.y);
	theBuffer.append("Y + ");
	theBuffer.append(myNormalVector.z);
	theBuffer.append("Z + ");
	theBuffer.append(myD);
	theBuffer.append(" = 0>");
	return theBuffer.toString();
  }
}