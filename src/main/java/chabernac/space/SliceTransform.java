package chabernac.space;

import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;

//import chabernac.utils.*;

public class SliceTransform implements Transform3D{
  public static final int XY = 0;
  public static final int XZ = 1;
  public static final int YZ = 2;
  
  private int myType;
  
  public SliceTransform(int aType){
    myType = aType;
  }
  
  public Point2D transform(Point3D aPoint){
    switch(myType){
      case XY: return new Point2D((int)aPoint.x, (int)aPoint.y);
      case XZ: return new Point2D((int)aPoint.x, (int)aPoint.z);
      case YZ: return new Point2D((int)aPoint.y, (int)aPoint.z);
    }
    return null;
  }
}
