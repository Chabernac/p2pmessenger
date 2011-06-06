/*
 * Created on 13-aug-2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space;

import chabernac.math.MatrixOperations;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Line3D;
import chabernac.space.geom.Plane;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;

public class CoordinateSystem implements iTranslatable{
	private Point3D myOrigin = null;
  private GVector myXUnit = null;
  private GVector myYUnit = null;
  private GVector myZUnit = null;
  private Transformation myTransformation = null;
  
  
  public CoordinateSystem(Line3D aLine){
    this(aLine.getOrigin(), aLine.getDirection());
  }
	
  /**
   * create a coordinate system with one fixed axes (z-axis)
   * the other 2 axis are rondonmly chosen (the 2 axis will be in the plane which has
   * the z axis as normal vector)
   * @param anOrigin
   * @param aVector
   */
  public CoordinateSystem(Point3D anOrigin, GVector aVector){
    Plane thePlane = new Plane(aVector, anOrigin);
    Point3D theNewPointOnPlane = null;
    if(aVector.x == 0 && aVector.y == 0){
      //het vlak is evenwijdig met het xy vlak
       theNewPointOnPlane = new Point3D(anOrigin.x + 10, anOrigin.y + 10, anOrigin.z);
    } else if(aVector.x == 0 && aVector.z == 0){
      //het vlak is evenwijdig met het xz vlak
      theNewPointOnPlane = new Point3D(anOrigin.x + 10, anOrigin.y, anOrigin.z + 10);
    } else if(aVector.y == 0 && aVector.z == 0){
      //het vlak is evenwijdig met het yz vlak
      theNewPointOnPlane = new Point3D(anOrigin.x, anOrigin.y + 10, anOrigin.z + 10);
    } else {
      theNewPointOnPlane = thePlane.pointOnPlane(new Point2D(10,10));
    }
   
    myOrigin = anOrigin;
    myZUnit = aVector.norm();
    
    myXUnit = new GVector(theNewPointOnPlane, anOrigin).norm();
    myYUnit = myXUnit.produkt(myZUnit);

    myTransformation = new Transformation();
    myTransformation.addTransformation(MatrixOperations.buildTransformationMatrix(this));
  }
  
	public CoordinateSystem(Point3D anOrigin){
		this(anOrigin, new GVector(1,0,0), new GVector(0,1,0));
	}
	
	public CoordinateSystem(Point3D anOrigin, GVector anXVector, GVector anYVector){
		this(anOrigin, anXVector, anYVector, anXVector.produkt(anYVector));
	}
	
	public CoordinateSystem(Point3D anOrigin, GVector anXVector, GVector anYVector, GVector aZVector){
		myOrigin = anOrigin;
		myXUnit = anXVector;
		myYUnit = anYVector; 
		myZUnit = aZVector;

    myTransformation = new Transformation();
    myTransformation.addTransformation(MatrixOperations.buildTransformationMatrix(this));
	}
  
  public Transformation getTransformator(){
    return myTransformation;
  }
	
  /*
	public Point3D transform(Point3D aPoint){
		return MatrixOperations.buildPoint3d(MatrixOperations.buildMatrix(aPoint).multiply(myTransformationMatrix));		
	}
  */
	
	public static void main(String args[]){
		CoordinateSystem theSystem = new CoordinateSystem(new Point3D(0,0,0), new GVector(0,1,0), new GVector(-1,0,0));
		System.out.println(theSystem.getTransformator().transform(new Point3D(5,5,5)));
	}

  public Point3D getOrigin() {
    return myOrigin;
  }

  public GVector getXUnit() {
    return myXUnit;
  }

  public GVector getYUnit() {
    return myYUnit;
  }

  public GVector getZUnit() {
    return myZUnit;
  }

  @Override
  public Point3D getCamCenterPoint() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void translate(iTransformator aTransformator) throws TranslateException {
    myOrigin = aTransformator.transform(myOrigin);
    myXUnit = aTransformator.transform(myXUnit);
    myYUnit = aTransformator.transform(myYUnit);
    myZUnit = aTransformator.transform(myZUnit);
    myTransformation = new Transformation();
    myTransformation.addTransformation(MatrixOperations.buildTransformationMatrix(this));
  }
}
