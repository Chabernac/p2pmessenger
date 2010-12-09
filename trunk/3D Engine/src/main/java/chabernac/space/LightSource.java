package chabernac.space;

import java.awt.Color;

import chabernac.math.MatrixException;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;


public class LightSource implements iTranslatable{
  private Point3D myWorldLocation = null;
  private Point3D myCamLocation = null;
  private float myIntensity = 0;
  private int myColor = Color.white.getRGB();

  public LightSource(Point3D aLocation, float anNeutralDistance){
    myWorldLocation = aLocation;
    myIntensity = anNeutralDistance;
  }

  public float getIntensity() {	return myIntensity; }
  public void setIntensity(float intensity) { this.myIntensity = intensity; }
  public Point3D getLocation() {	return myWorldLocation;  }
  public void setLocation(Point3D location)  { this.myWorldLocation = location;	 }

  public void world2Cam(Camera aCamera) throws MatrixException{
    myCamLocation = aCamera.world2Cam(myWorldLocation);		
  }

  public Point3D getCamLocation() {
    return myCamLocation;
  }

  public float calculateLight(Point3D aPixel, GVector aNormalVector){
    GVector theDirectionToPixel = new GVector(aPixel, getCamLocation());
    float distance = theDirectionToPixel.length();
    theDirectionToPixel.normalize();
    float lightningFactor = (theDirectionToPixel.dotProdukt(aNormalVector)  * getIntensity()) / distance;
    if(lightningFactor < 0.0){
      lightningFactor = (float)0.0;
    }
    return lightningFactor;
  }

  public static float calculateLight(World aWorld, Point3D aPixel, GVector aNormalVector)
  {
    float light = 0;
    for(LightSource theLightSource : aWorld.lightSources){
      light += theLightSource.calculateLight(aPixel, aNormalVector);
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
