package chabernac.space;

import java.awt.Dimension;

import chabernac.space.geom.GVector;
import chabernac.space.geom.Plane;
import chabernac.space.geom.Point3D;
//import chabernac.utils.Debug;

public class ScreenFrustrum extends Frustrum{
  private Point3D myEyePoint = null;
  private Dimension myScreenDimension = null;
  private float myFarClippingPlaneDepth;
  private float myNearClippingPlaneDepth;
  
  private float myInverseNearDepth;
  private float myDivisionConstant;

  public ScreenFrustrum(Point3D anEyePoint, Dimension aScreenDimension, float aNearClippingPlaneDepth, float aFarClippingPlaneDepth){
    super(6);
    //Debug.log(this,"Creating screen frustrum with eyepoint: " + anEyePoint + " dimension: " + aScreenDimension);
    myEyePoint = anEyePoint;
    myScreenDimension = aScreenDimension;
    myFarClippingPlaneDepth = aFarClippingPlaneDepth;
    myNearClippingPlaneDepth = aNearClippingPlaneDepth;
    calculateConstants();
    createPlanes();
  }
  
  private void calculateConstants(){
    myInverseNearDepth = 1 / myNearClippingPlaneDepth;
    myDivisionConstant = 1 / myFarClippingPlaneDepth - myInverseNearDepth;
  }
  
  public void createPlanes(){
    Point3D theEyePoint = new Point3D(0,0,-myEyePoint.z);
    //z = 0 plane, near clipping plane
    addPlane(new Plane(new GVector(0,0,(float)myNearClippingPlaneDepth),new Point3D(0,0,0)));
    //left plane
    addPlane(new Plane(new GVector(myEyePoint.z,0F,(float)(myScreenDimension.getWidth() / 2) - 1),theEyePoint));
    //right plane
    //TODO how come we need to write -2 ?
    addPlane(new Plane(new GVector(-myEyePoint.z,0,(float)(myScreenDimension.getWidth() / 2) - 2),theEyePoint));
    //top plane
    addPlane(new Plane(new GVector(0, -myEyePoint.z,(float)(myScreenDimension.getHeight() / 2) - 1),theEyePoint));
    //bottom plane
    addPlane(new Plane(new GVector(0, myEyePoint.z,(float)(myScreenDimension.getHeight() / 2) - 1),theEyePoint));
    //far clipping plane
    addPlane(new Plane(new GVector(0, 0,-1),new Point3D(0,0,myFarClippingPlaneDepth)));
  }
  
  public Point3D getEyePoint(){ return myEyePoint; }
  public Dimension getScreenDimension(){ return myScreenDimension; }
  
  public void setEyePoint(Point3D anEyePoint){ myEyePoint = anEyePoint; }
  public void setScreenDimension(Dimension aScreenDimension){ myScreenDimension = aScreenDimension; }
  
  public float calculateRelativeDepth(float aDepth){
    return aDepth;
//    return 1 / aDepth;
//    return 1000 * (1 / aDepth - myInverseNearDepth) / myDivisionConstant;
  }
}