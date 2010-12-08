package chabernac.space;

import java.util.Iterator;

import chabernac.math.MatrixException;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;


public class LightSource implements iTranslatable{
  private Point3D myWorldLocation = null;
  private Point3D myCamLocation = null;
  private double myIntensity = 0;

  public LightSource(Point3D aLocation, double anNeutralDistance){
    myWorldLocation = aLocation;
    myIntensity = anNeutralDistance;
  }

  public double getIntensity() {	return myIntensity; }
  public void setIntensity(double intensity) { this.myIntensity = intensity; }
  public Point3D getLocation() {	return myWorldLocation;  }
  public void setLocation(Point3D location)  { this.myWorldLocation = location;	 }

  public void world2Cam(Camera aCamera) throws MatrixException{
    myCamLocation = aCamera.world2Cam(myWorldLocation);		
  }

  public Point3D getCamLocation() {
    return myCamLocation;
  }

  public double calculateLight(Point3D aPixel, GVector aNormalVector){
    GVector theDirectionToPixel = new GVector(aPixel, getCamLocation());
    double distance = theDirectionToPixel.length();
    theDirectionToPixel.normalize();
    double lightningFactor = (theDirectionToPixel.dotProdukt(aNormalVector) * getIntensity()) / distance;
    if(lightningFactor < 0.0D){
      lightningFactor = 0.0D;
    }
    return lightningFactor;
  }

  public static double calculateLight(World aWorld, Point3D aPixel, GVector aNormalVector)
  {
    double light = 0.0D;
    for(Iterator i = aWorld.getLightSources().iterator(); i.hasNext();){
      light += ((LightSource)i.next()).calculateLight(aPixel, aNormalVector);
    }

    return light;
  }

  /*
	public void translate(Camera aCamera) throws TranslateException {
		myWorldLocation = aCamera.world2Cam(myWorldLocation);
	}
   */

  public void translate(iTransformator aTransformator) throws TranslateException {
    myWorldLocation = aTransformator.transform(myWorldLocation);
  }

  public Point3D getCenterPoint(){
    return myWorldLocation;
  }
}
