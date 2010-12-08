package chabernac.space;

import chabernac.space.geom.GVector;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;

public class Vertex{
  public Point3D myPoint = null; // its position, xyz in 3D, xy in 2D
  //public double u, v, c; // u and v are coordinates of the source texture
  public Point2D myTextureCoordinate = null;
  public GVector normal = null;
  public double lightIntensity;

  public Vertex(Point3D aPoint, Point2D aTextureCoordinate, double aLight){
	  this.myPoint = aPoint;
	  this.myTextureCoordinate = aTextureCoordinate;
	  this.lightIntensity = aLight;
  }
  
  public Vertex(Point3D aPoint){
    this(aPoint, new Point2D(0,0), 0);
  }

  public String toString(){
    return "<Vertex: " + myPoint.toString() + " " + myTextureCoordinate.toString() + "/>";
  }
  
  public boolean equals(Vertex aVertex){
	  return myPoint.equals(aVertex.myPoint);
  }

}