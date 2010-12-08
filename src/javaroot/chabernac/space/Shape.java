package chabernac.space;

//import chabernac.utils.Debug;
import java.awt.Color;

import chabernac.math.MatrixException;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;
import chabernac.utils.Tools;

public class Shape implements Comparable, iTranslatable{
  public int mySize;
  private int myCurrentPolygon = 0;
  public Polygon[] myPolygons;
  public  Point3D myCenterPoint = null;
  public  Point3D myCamCenterPoint = null;
  public double myCamDistance;
  private boolean isRoom = false;
  public int myGrowSize = 10;
  public boolean visible;

  public Shape(int nrOfPolygons){
	  this(nrOfPolygons, false);
  }

  public Shape(int nrOfPolygons, boolean isRoom){
     mySize = nrOfPolygons;
     this.isRoom = isRoom;
     initialize();
  }

  private void initialize(){
    myPolygons = new Polygon[mySize];
    clear();
  }

  public void clear(){
    for(int i=0;i<mySize;i++){
      myPolygons[i] = null;
    }
    myCurrentPolygon= 0;
  }

  public void addPolygon(Polygon aPolygon){
	if(myCurrentPolygon >= myPolygons.length){
		myPolygons = (Polygon[])Tools.growArray(myPolygons, myGrowSize);
		mySize += myGrowSize;
	}
    myPolygons[myCurrentPolygon++] = aPolygon;
  }

  public void done() throws PolygonException{
    optimize();
    calculateCenterPoint();
    calculateNormalVectors();
    calculateVertexNormals();
  }

  public void optimize(){
    if(myCurrentPolygon < mySize){
      Polygon[] theTempPolygons = new Polygon[myCurrentPolygon];
      System.arraycopy(myPolygons, 0, theTempPolygons, 0, myCurrentPolygon);
      myPolygons = theTempPolygons;
      mySize = myCurrentPolygon;
    }
  }

  public void calculateCenterPoint(){
    if(myCenterPoint != null) return;
    double x = 0, y = 0, z = 0;
    for(int i=0;i<mySize;i++){
      myPolygons[i].calculateCenterPoint();
      x += myPolygons[i].myCenterPoint.x;
      y += myPolygons[i].myCenterPoint.y;
      z += myPolygons[i].myCenterPoint.z;
    }
    myCenterPoint = new Point3D(x / mySize, y / mySize, z / mySize);
  }

  public void calculateNormalVectors() throws PolygonException{
    //calculateCenterPoint();
    for(int i=0;i<mySize;i++){
      myPolygons[i].calculateNormalVector(myCenterPoint, isRoom);
    }
  }
  
  private void calculateVertexNormals(){
	  Polygon thePolygon = null;
	  Vertex theVertex = null;
	  for(int i=0;i<myPolygons.length;i++){
		  thePolygon = myPolygons[i];
		  for(int j=0;j<thePolygon.w.length;j++){
			  theVertex = thePolygon.w[j];
			  theVertex.normal = new GVector(0,0,0);
			  for(int k=0;k<myPolygons.length;k++){
				  if(myPolygons[k].containsVertex(theVertex)){
					  theVertex.normal.add(myPolygons[k].myNormalVector);
				  }
			  }
			  theVertex.normal.normalize();
		  }
	  }
  }

  public void world2Cam(Camera aCamera) throws PolygonException, MatrixException{
    for(int i=0;i<mySize;i++){
      myPolygons[i].world2Cam(aCamera);
    }
    myCamCenterPoint = aCamera.world2Cam(myCenterPoint);
    myCamDistance = myCamCenterPoint.x * myCamCenterPoint.x +
               		myCamCenterPoint.y * myCamCenterPoint.y +
               		myCamCenterPoint.z * myCamCenterPoint.z;
  }

  /*
  public void translate(Camera aCamera) throws TranslateException{
  	try{
	    for(int i=0;i<mySize;i++){
	      myPolygons[i].translate(aCamera);
	    }
	    myCenterPoint = aCamera.world2Cam(myCenterPoint);
  	}catch(MatrixException e){
  		throw new TranslateException("Could not translate shape with camera", e);
  	}catch(PolygonException f){
  		throw new TranslateException("Could not translate shape with camera", f);
  	}
  }
  */
  
  public void translate(iTransformator aTransformator) throws TranslateException{
    try{
      for(int i=0;i<mySize;i++){
        myPolygons[i].translate(aTransformator);
      }
      myCenterPoint = aTransformator.transform(myCenterPoint);
    }catch(MatrixException e){
      throw new TranslateException("Could not translate shape with camera", e);
    }catch(PolygonException f){
      throw new TranslateException("Could not translate shape with camera", f);
    }
  }

  public void clip2Frustrum(Frustrum aFrustrum) throws PolygonException{
	visible = false;
    for(int i=0;i<mySize;i++){
      myPolygons[i].clip2Frustrum(aFrustrum);
      visible |= myPolygons[i].visible;
    }
  }

  public int compareTo(Object aObject){
	Shape theShape = (Shape)aObject;
    if (myCamDistance == theShape.myCamDistance) return 0;
    else if (myCamDistance < theShape.myCamDistance) return -1;
    else return 1;
  }

  public String toString(){
	  StringBuffer theBuffer = new StringBuffer();
	  theBuffer.append("<Shape: ");
	  theBuffer.append(myCenterPoint.toString());
	  theBuffer.append("/>");
	  return theBuffer.toString();
  }
  
  public void setColor(Color aColor){
  	for(int i=0;i<myPolygons.length;i++){
  		myPolygons[i].setColor(aColor);
  	}
  }
  
  public void setTexture(String aTexture){
    setTexture(aTexture, true);
  }
  
  public void setTexture(String aTexture, boolean isTransparent){
    for(int i=0;i<myPolygons.length;i++){
      myPolygons[i].setTexture(aTexture, isTransparent);
    }
  }
  
  public Point3D getCenterPoint(){
  	return myCenterPoint;
  }
  
  public void setDoubleSidedPolygons(boolean doubleSided){
    for(int i=0;i<myPolygons.length;i++){
      myPolygons[i].doubleSided = doubleSided;
    }
  }
}