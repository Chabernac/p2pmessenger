package chabernac.space;

import org.apache.log4j.Logger;

import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;

public class SimpleTransform implements Transform3D{
  private static final Logger LOGGER = Logger.getLogger( SimpleTransform.class );
  private double myAngle;
  
  public SimpleTransform(double anAngle){
    myAngle = anAngle;
  }
  
  public Point2D transform(Point3D aPoint){
    int x = (int)(aPoint.x + aPoint.z * Math.cos(myAngle));
    int y = (int)(aPoint.y + aPoint.z * Math.sin(myAngle));
    Point2D thePoint = new Point2D(x,y);
    LOGGER.debug( aPoint.toString() + " --> " + thePoint.toString());
    //Debug.log(this,"X: " + x  + " Y: " + y);
    return new Point2D(x,y);
  }
}