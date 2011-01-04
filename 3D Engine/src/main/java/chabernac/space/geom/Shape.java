package chabernac.space.geom;

//import chabernac.utils.Debug;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import chabernac.math.MatrixException;
import chabernac.space.Camera;
import chabernac.space.Frustrum;
import chabernac.space.PolygonException;
import chabernac.space.TranslateException;
import chabernac.space.Vertex;
import chabernac.space.iTransformator;
import chabernac.space.iTranslatable;
import chabernac.space.texture.TextureImage;
import chabernac.utils.ArrayTools;

public class Shape implements Comparable, iTranslatable{
  public int mySize;
  private int myCurrentPolygon = 0;
  public Polygon[] myPolygons;
  public  Point3D myCenterPoint = null;
  public  Point3D myCamCenterPoint = null;
  public float myCamDistance;
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
      myPolygons = (Polygon[])ArrayTools.growArray(myPolygons, myGrowSize);
      mySize += myGrowSize;
    }
    myPolygons[myCurrentPolygon++] = aPolygon;
  }

  public void done() throws PolygonException{
    optimize();
    calculateCenterPoint();
    calculateNormalVectors();
    calculateVertexNormals();
    calculateTextureCoordinates();
  }

  private void calculateTextureCoordinates() {
    for(int i=0;i<mySize;i++){
      myPolygons[i].createTexture();
    }
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
    float x = 0, y = 0, z = 0;
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
    setTexture(aTexture, true, false);
  }

  public void setTexture(TextureImage aTextureImage, boolean isSpherical){
    for(int i=0;i<myPolygons.length;i++){
      myPolygons[i].setTexture(aTextureImage, isSpherical);
    }
  }

  public void setTexture(String aTexture, boolean isTransparent, boolean isSpherical){
    System.out.println("Setting texture on all polygons");
    for(int i=0;i<myPolygons.length;i++){
      myPolygons[i].setTexture(aTexture, isTransparent, isSpherical);
    }
    System.out.println("Setting texture on all polygons done");
  }

  public void setTexture(String aTexture, String aBumpMap, boolean isTransparent, boolean isSpherical){
    System.out.println("Setting texture on all polygons");
    for(int i = 0; i < myPolygons.length; i++){
      myPolygons[i].setTexture(aTexture, aBumpMap, isTransparent, isSpherical);
    }
    System.out.println("Setting texture on all polygons done");
  }

  public Point3D getCenterPoint(){
    return myCenterPoint;
  }

  public void setfloatSidedPolygons(boolean floatSided){
    for(int i=0;i<myPolygons.length;i++){
      myPolygons[i].doubleSided = floatSided;
    }
  }

  public boolean isRoom() {
    return isRoom;
  }

  public void setRoom(boolean anIsRoom) {
    isRoom = anIsRoom;
  }

  public void setDoubleSided(boolean isDoubleSided) {
    for(Polygon thePolygon : myPolygons){
      thePolygon.doubleSided = isDoubleSided;
    }
  }


  public void triangulate(){
    boolean isTriangulateFurther = true;

    while(isTriangulateFurther){
      List<Polygon> thePolygons = new ArrayList<Polygon>();
      for(Polygon thePolygon : myPolygons){
        thePolygons.addAll(thePolygon.triangulate());
      }

      isTriangulateFurther = thePolygons.size() > myPolygons.length;

      if(isTriangulateFurther){
        myPolygons = thePolygons.toArray(new Polygon[]{});
        mySize = myPolygons.length;
      }
    }


    calculateVertexNormals();
    calculateNormalVectors();
  }
}