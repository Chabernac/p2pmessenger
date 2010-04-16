
package chabernac.space.geom;


public class Line3D {
  
  private GVector myDirection = null;
  private Point3D myOrigin = null;
  
  public Line3D(Point3D aP1, Point3D aP2){
    this(aP1, new GVector(aP1, aP2));
  }
  
  public Line3D(Point3D aOrigin, GVector aDirection){
    myDirection = aDirection;
    myOrigin = aOrigin;
  }
  
  public GVector getDirection(){
    return myDirection;
  }
  
  public Point3D getOrigin(){
    return myOrigin;
  }
  
  

}
