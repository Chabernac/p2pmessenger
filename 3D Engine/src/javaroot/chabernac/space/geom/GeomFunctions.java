package chabernac.space.geom;


//import chabernac.utils.Debug;

public class GeomFunctions{

  public static final int PLANE_BACKSIDE = 0;
  public static final int PLANE_FRONT    = 1;
  public static final int ON_PLANE       = 2;


  public static Point2D cam2Screen(Point3D aPoint, Point3D anEyePoint){
    double x = (anEyePoint.x + aPoint.x * anEyePoint.z / (anEyePoint.z + aPoint.z));
    double y = (anEyePoint.y - aPoint.y * anEyePoint.z / (anEyePoint.z + aPoint.z));
    //int x = (int)(anEyePoint.x + anEyePoint.z * Math.atan(aPoint.x / (anEyePoint.z + aPoint.z)));
    //int y = (int)(anEyePoint.y - anEyePoint.z * Math.atan(aPoint.y / (anEyePoint.z + aPoint.z)));
    //int x = (int)(anEyePoint.x + anEyePoint.z *  aPoint.x / Math.sqrt((Math.pow(aPoint.x,2) + Math.pow((anEyePoint.z + aPoint.z),2))));
    //int y = (int)(anEyePoint.y - anEyePoint.z *  aPoint.y / Math.sqrt((Math.pow(aPoint.y,2) + Math.pow((anEyePoint.z + aPoint.z),2))));


    return new Point2D(x,y);

  }

  public static double intersectRayPlaneDistance(Point3D aRayOrigin, GVector aRayVector, GVector aPlaneVector, Point3D aPointOnPlane){
	  return intersectRayPlaneDistance(aRayOrigin, aRayVector, new Plane(aPlaneVector, aPointOnPlane));
  }

  public static double intersectRayPlaneDistance(Point3D aRayOrigin, GVector aRayVector, Plane aPlane){
	  double theDistance = aPlane.distanceToPoint(aRayOrigin);
	  double theCosAngle = aPlane.myNormalVector.dotProdukt(aRayVector);
	  if(theCosAngle == 0) return -1F;
	  return -(theDistance / theCosAngle);
  }

  public static Point3D intersectRayPlane(Point3D aRayOrigin, GVector aRayVector, GVector aPlaneVector, Point3D aPointOnPlane){
  	  return intersectRayPlane(aRayOrigin, aRayVector, new Plane(aPlaneVector, aPointOnPlane));
  }

  public static Point3D intersectRayPlane(Point3D aRayOrigin, GVector aRayVector, Plane aPlane){
	  double theDistance = intersectRayPlaneDistance(aRayOrigin, aRayVector, aPlane);
	  Point3D theIntersectionPoint = (Point3D )aRayOrigin.clone();
	  GVector theRayVector = (GVector)aRayVector.clone();
	  theRayVector.normalize();
	  theRayVector.multiply(theDistance);
	  theIntersectionPoint.add(theRayVector);
	  return theIntersectionPoint;
  }

  public static int classifyPoint(Plane aPlane, Point3D aPoint){
	  double theDistance = aPlane.distanceToPoint(aPoint);
	  if(theDistance > 0.001F) return PLANE_FRONT;
	  if(theDistance < -0.001F) return PLANE_BACKSIDE;
	  return ON_PLANE;
  }
  
  public static Rotation vector2Rotation(GVector aVector){
  	double r = Math.sqrt(aVector.x * aVector.x + aVector.z * aVector.z);
  	double alpha = 0;
  	if(aVector.z == 0){
  		if(aVector.x > 0) alpha = Math.PI / 2;
  		if(aVector.x < 0) alpha = -Math.PI / 2;
  	} else {
  		alpha = Math.atan(aVector.x / aVector.z);
  		if(aVector.x <= 0 && aVector.z < 0) alpha += Math.PI;
  	}
  	
  	double beta = 0;
  	if(r == 0){
  		if(aVector.y > 0) beta = Math.PI / 2;
  		if(aVector.y < 0) beta = -Math.PI / 2;
  	} else {
  		beta = Math.atan(aVector.y / r);
  		if(aVector.y < 0 && r <= 0) beta += Math.PI;
  	}
  	
  	return new Rotation(0, beta, alpha);
  }


}