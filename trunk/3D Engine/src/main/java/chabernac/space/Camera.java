/*
 * Created on 9-jan-2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space;

import java.io.Serializable;

import chabernac.math.Matrix;
import chabernac.math.MatrixException;
import chabernac.math.MatrixOperations;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Camera implements iTranslatable, iTransformator, Serializable{
  private static final long serialVersionUID = -5512630842154013982L;
  private Matrix myTranslationMatrix = null;
	private Matrix myRotationMatrix = null;
	private Matrix myScalingMatrix = null;
	private Matrix myMatrix = null;
	private Matrix myInvMatrix = null;

	public Camera() throws MatrixException{
		this(new Point3D(0,0,0), new Rotation(0,0,0), 1);
	}
	
	public Camera(Point3D aLocation, Rotation aRotation, float aScaling) throws MatrixException{
		myRotationMatrix = MatrixOperations.buildRotationMatrix(aRotation);
		myTranslationMatrix = MatrixOperations.buildTranslationMatrix(aLocation);
		myScalingMatrix = MatrixOperations.buildScalingMatrix(aScaling,aScaling,aScaling);
		calculateMatrix();
	}
	
	private void calculateMatrix() throws MatrixException{
		myMatrix = (myTranslationMatrix.multiply(myRotationMatrix)).multiply(myScalingMatrix);
		myInvMatrix = myMatrix.inverse();
	}
  
  
	
	public Point3D world2Cam(Point3D aPoint) throws MatrixException{
		Point3D thePoint = MatrixOperations.buildPoint3d((MatrixOperations.buildMatrix(aPoint).multiply(myMatrix)));
		//System.out.println(aPoint.toString() + " -- world2cam --> " + thePoint.toString());
		return thePoint;
	}
	
	public Point3D cam2World(Point3D aPoint) throws MatrixException{
		return MatrixOperations.buildPoint3d(MatrixOperations.buildMatrix(aPoint).multiply(myInvMatrix));
	}
	
	public GVector world2Cam(GVector aVector) throws MatrixException{
		return MatrixOperations.buildGVector(MatrixOperations.buildMatrix(aVector).multiply(myRotationMatrix));
	}
	
	public GVector cam2World(GVector aVector) throws MatrixException{
		return MatrixOperations.buildGVector(MatrixOperations.buildMatrix(aVector).multiply(myRotationMatrix));
	}
	
	public Vertex world2Cam(Vertex aVertex){
		Vertex theVertex = new Vertex(world2Cam(aVertex.myPoint), aVertex.myTextureCoordinate, aVertex.lightIntensity);
		theVertex.normal = world2Cam(aVertex.normal);
		return theVertex;
	}
	
	public Vertex cam2World(Vertex aVertex){
		Vertex theVertex = new Vertex(cam2World(aVertex.myPoint), aVertex.myTextureCoordinate, aVertex.lightIntensity);
		theVertex.normal = cam2World(aVertex.normal);
		return theVertex;
	}
	
	public synchronized void moveCamera(Point3D aCameraPoint) throws MatrixException{
		setLocation(cam2World(aCameraPoint));
	}
	
	public void setLocation(Point3D aWorldPoint) throws MatrixException{
		myTranslationMatrix = MatrixOperations.buildTranslationMatrix(aWorldPoint);
		calculateMatrix();
	}
	
	public synchronized void rotateCamera(Rotation aRotation) throws MatrixException{
		myRotationMatrix = myRotationMatrix.multiply(MatrixOperations.buildRotationMatrix(aRotation));
		calculateMatrix();
	}
	
	public synchronized void translate(Camera aCamera){
		//myTranslationMatrix = myTranslationMatrix.multiply(aCamera.getTranslationMatrix());
		//Debug.log(this,"Current location: " + getLocation().toString());
		setLocation(cam2World(aCamera.getLocation()));
		myRotationMatrix = myRotationMatrix.multiply(aCamera.getRotationMatrix());
		calculateMatrix();
		//Debug.log(this,"Current location: " + getLocation().toString());
	}
	
	public Matrix getTranslationMatrix(){ return myTranslationMatrix; }
	public Matrix getRotationMatrix(){ return myRotationMatrix; }
	public Matrix getMatrix(){ return myMatrix; }
	public Matrix getInvMatrix(){ return myInvMatrix;}
	public Point3D getLocation(){ return new Point3D(-myTranslationMatrix.getValueAt(3,0),-myTranslationMatrix.getValueAt(3,1), -myTranslationMatrix.getValueAt(3,2));}

	public Point3D getCenterPoint() { return getLocation(); }
	
	public static Camera makeTranslationCamera(GVector aDirection){
		return new Camera(new Point3D(-aDirection.x ,-aDirection.y, -aDirection.z), new Rotation(), 1);
	}

  public Point3D inverseTransform(Point3D aPoint) {
    return cam2World(aPoint);
  }

  public GVector inverseTransform(GVector aVector) {
    return cam2World(aVector);
  }

  public Point3D transform(Point3D aPoint) {
    return world2Cam(aPoint);
  }

  public GVector transform(GVector aVector) {
    return world2Cam(aVector);
  }

  public void translate(iTransformator anTransformator) throws TranslateException {
    // TODO Auto-generated method stub
    
  }

  public Vertex inverseTransform(Vertex aVertex) {
    return cam2World(aVertex);
  }

  public Vertex transform(Vertex aVertex) {
    return world2Cam(aVertex);
  }
}
