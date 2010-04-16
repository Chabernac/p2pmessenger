/*
 * Created on 13-aug-2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space;

import chabernac.math.Matrix;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;

public class CoordinateSystem {
	public Point3D myOrigin = null;
	public GVector myXUnit = null;
	public GVector myYUnit = null;
	public GVector myZUnit = null;
	public Matrix myTransformationMatrix = null;
	
	public CoordinateSystem(Point3D anOrigin){
		this(anOrigin, new GVector(1,0,0), new GVector(0,1,0));
	}
	
	public CoordinateSystem(Point3D anOrigin, GVector anXVector, GVector anYVector){
		this(anOrigin, anXVector, anYVector, anYVector.produkt(anYVector));
	}
	
	public CoordinateSystem(Point3D anOrigin, GVector anXVector, GVector anYVector, GVector aZVector){
		myOrigin = anOrigin;
		myXUnit = anXVector;
		myYUnit = anYVector; 
		myZUnit = aZVector;
		myTransformationMatrix = new Matrix(4, 4);
		
		myTransformationMatrix = MatrixOperations.buildTransformationMatrix(this);
	}
	
	public Point3D transform(Point3D aPoint){
		return MatrixOperations.buildPoint3d(MatrixOperations.buildMatrix(aPoint).multiply(myTransformationMatrix));		
	}
	
	public static void main(String args[]){
		CoordinateSystem theSystem = new CoordinateSystem(new Point3D(0,0,0), new GVector(0,1,0), new GVector(-1,0,0));
		System.out.println(theSystem.transform(new Point3D(5,5,5)));
	}


}
