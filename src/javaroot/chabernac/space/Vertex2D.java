package chabernac.space;

import chabernac.space.geom.Point2D;

public class Vertex2D {
	private Point2D myPoint = null;
	private Point2D myTexturePoint = null;
	private double myDepth, myInverseDepth;
	private double myLightning;
	
	public Vertex2D(Point2D aPoint, double aDepth, double aLightning){
		this(aPoint, null, aDepth, aLightning);
	}
	
	public Vertex2D(Point2D aPoint, Point2D aTexturePoint, double aDepth, double aLightning){
		myPoint = aPoint;
		myTexturePoint = aTexturePoint;
		myDepth = aDepth;
		myInverseDepth = 1 / myDepth;
		myLightning = aLightning;
	}
	
	public Point2D getPoint(){
		return myPoint;
	}
	
	public Point2D getTexturePoint(){
		return myTexturePoint;
	}
	
	public double getDepth(){
		return myDepth;
	}
	
	public double getInverseDepth(){
		return myInverseDepth;
	}
	
	public double getLightning(){
		return myLightning;
	}
}

