package chabernac.space;

import java.awt.Dimension;

import chabernac.space.geom.GVector;
import chabernac.space.geom.Plane;
import chabernac.space.geom.Point3D;
//import chabernac.utils.Debug;

public class ScreenFrustrum extends Frustrum{
  private Point3D myEyePoint = null;
  private Dimension myScreenDimension = null;

  public ScreenFrustrum(Point3D anEyePoint, Dimension aScreenDimension){
    super(5);
    //Debug.log(this,"Creating screen frustrum with eyepoint: " + anEyePoint + " dimension: " + aScreenDimension);
    myEyePoint = anEyePoint;
    myScreenDimension = aScreenDimension;
    createPlanes();
  }
  
  public void createPlanes(){
    Point3D theEyePoint = new Point3D(0,0,-myEyePoint.z);
    //z = 0 plane
    addPlane(new Plane(new GVector(0,0,0.01),new Point3D(0,0,0)));
    //left plane
    addPlane(new Plane(new GVector(myEyePoint.z,0F,(float)(myScreenDimension.getWidth() / 2) - 1),theEyePoint));
    //right plane
    //TODO how come we need to write -2 ?
    addPlane(new Plane(new GVector(-myEyePoint.z,0,(float)(myScreenDimension.getWidth() / 2) - 2),theEyePoint));
    //top plane
    addPlane(new Plane(new GVector(0, -myEyePoint.z,(float)(myScreenDimension.getHeight() / 2) - 1),theEyePoint));
    //bottom plane
    addPlane(new Plane(new GVector(0, myEyePoint.z,(float)(myScreenDimension.getHeight() / 2) - 1),theEyePoint));
  }
  
  public Point3D getEyePoint(){ return myEyePoint; }
  public Dimension getScreenDimension(){ return myScreenDimension; }
  
  public void setEyePoint(Point3D anEyePoint){ myEyePoint = anEyePoint; }
  public void setScreenDimension(Dimension aScreenDimension){ myScreenDimension = aScreenDimension; }
}