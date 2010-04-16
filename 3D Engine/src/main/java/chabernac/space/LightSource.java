package chabernac.space;

import chabernac.math.MatrixException;
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

	public void translate(Camera aCamera) throws TranslateException {
		myWorldLocation = aCamera.world2Cam(myWorldLocation);
	}
	
	public Point3D getCenterPoint(){
		return myWorldLocation;
	}
}
