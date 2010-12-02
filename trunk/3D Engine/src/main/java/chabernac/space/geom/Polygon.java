package chabernac.space.geom;

//import chabernac.utils.Debug;
import java.awt.Color;
import java.io.IOException;

import org.apache.log4j.Logger;

import chabernac.math.MatrixException;
import chabernac.space.Camera;
import chabernac.space.Frustrum;
import chabernac.space.PolygonException;
import chabernac.space.Vertex;
import chabernac.space.iTransformator;
import chabernac.space.iTranslatable;
import chabernac.space.texture.Texture2;
import chabernac.space.texture.TextureFactory;
import chabernac.space.texture.TextureImage;
import chabernac.utils.ArrayTools;

public class Polygon implements iTranslatable{
  private static Logger LOGGER = Logger.getLogger(Polygon.class);
  
  private int mySize;
  public int myCamSize;
  public Vertex w[];
  public Vertex c[];
  public GVector myNormalVector = null;
  public GVector myNormalCamVector = null;
  private int myCurrentVertex = 0;
  private boolean isConvex;
  private boolean isOptimized;
  public Point3D myCenterPoint = null;
  public Point3D myCamCenterPoint = null;
  public Color color = new Color(0,0,0);
  public Color lightedColor = new Color(0,0,0);
  public int myGrowSize = 10;
  public boolean visible = true;
  private Texture2 myTexture = null;
  private TextureImage myTextureImage = null;
  private boolean isSphericalTexture;
  public boolean doubleSided = false;
  private String myTextureName = null;
  private boolean isTransparentTexture = true;
  private String myBumpMap = null;

  public Polygon(Vertex[] worldVertexes){
    mySize = worldVertexes.length;
    initialize();
    w = worldVertexes;
    done();

  }

  public Polygon(int aSize){
    mySize = aSize;
    initialize();
  }

  private void initialize(){
    //TODO testje verwijderen
    //myColor = new Color((int)Math.random() % 255, (int)Math.random() % 255, (int)Math.random() % 255);
    //Debug.log(Polygon.class,"color: " + System.currentTimeMillis() % 255);
    /*
  	int red = Math.abs((int)System.currentTimeMillis()) % 255;
  	int green = Math.abs((int)System.currentTimeMillis()) % 255;
  	int blue = Math.abs((int)System.currentTimeMillis()) % 255;
     */
    int red = (int)(Math.random() * 255);
    int green = (int)(Math.random() * 255);
    int blue = (int)(Math.random() * 255);
    //Debug.log(Polygon.class,"red: "  + red + " green: " + green + " blue: " + blue);
    if(color == null) color = new Color(red, green, blue);
    w = new Vertex[mySize];
    c = new Vertex[mySize * 2];
    clear();
  }

  public void addVertex(Vertex aVertex){
    if(myCurrentVertex >= w.length){
      w = (Vertex[])ArrayTools.growArray(w, myGrowSize);
      c = (Vertex[])ArrayTools.growArray(c, myGrowSize * 2);
      mySize += myGrowSize;
      myCamSize += myGrowSize * 2;
    }
    w[myCurrentVertex++] = aVertex;
  }

  public void done(){
    optimize();
    calculateCenterPoint();
    calculateNormalVector();
    /*
    if(myTextureName != null) {
      calculateTexturePoints();
    }
    */
  }
  
  public void calculateTexturePoints(){
    if(myTextureImage == null && myTextureName == null){
      return;
    }
    
	  System.out.println("Calculating texture points");
    try {
      //we take the x vector of the texture parallel to the first 2 points of the polygon
      GVector theXVector = new GVector(w[0].myPoint, w[1].myPoint);
      //now we multiply the x vector with the plane's normal vector to obtain the y vector in the plane and orthogonal with the x vector
      GVector theYVector = myNormalVector.produkt(theXVector);
      theXVector.normalize();
      theYVector.normalize();
      
      if(myTextureName != null){
        myTexture = new Texture2(w[0].myPoint, theXVector, theYVector, myTextureName, isTransparentTexture );
      } else if(myTextureImage != null){
        myTexture = new Texture2(w[0].myPoint, theXVector, theYVector, myTextureImage);
      }
      if(myBumpMap != null){
        myTexture.setBumpMap(TextureFactory.getBumpMap(myBumpMap));
      }
      myTexture.setSpherical(isSphericalTexture);
      
      for(int i=0;i<w.length;i++){
        w[i].myTextureCoordinate = myTexture.getTextureCoordinate(w[i]);
      }
    } catch (IOException e) {
      LOGGER.error("Could not load texture: " + myTextureName, e);
    }
  }

  public void optimize(){
    if(myCurrentVertex < mySize){
      Vertex[] theTempVertex = new Vertex[myCurrentVertex];
      System.arraycopy(w, 0, theTempVertex, 0,myCurrentVertex);
      w = theTempVertex;
      theTempVertex = new Vertex[myCurrentVertex * 2];
      System.arraycopy(c, 0, theTempVertex, 0,myCurrentVertex * 2);
      c = theTempVertex;
      mySize = myCurrentVertex;
    }
    isOptimized = true;
  }

  public void clear(){
    for(int i=0;i<mySize;i++){
      w[i] = null;
      c[i] = null;
    }
    myCurrentVertex = 0;
  }

  /*
   * a polygon which is convex can only contain 1 vertex more after clipping
   * to a frustrum, a polygon which is not can almost contain twice as much
   * vertexes after clipping to a frustrum
   * @return convex or not
   */
  public boolean isConvex(){
    return isConvex;
  }

  public void calculateNormalVector() throws PolygonException{
    calculateNormalVector(null, true);
  }

  /*
   * Calculate the normal vector of the polygon, the resulting vector will
   * face away from aCenterPoint.  aCenterPoint could be the center of the
   * sphere the polygon is part of.
   */
  public void calculateNormalVector(Point3D aCenterPoint, boolean toWardsPoint) throws PolygonException{
    if(myCurrentVertex >= 3){
      if(aCenterPoint != null) myNormalVector = new GVector(w[0].myPoint, w[1].myPoint, w[2].myPoint, aCenterPoint, toWardsPoint);
      //Debug.log(this,"calculatig normal vector with 4 points");
      else myNormalVector = new GVector(w[0].myPoint, w[1].myPoint, w[2].myPoint);
      myNormalVector.normalize();
      //Debug.log(this,"");
    } else {
      throw new PolygonException("Not enough vertexes to calculate normal vector");
    }
  }

  public void calculateCenterPoint(){
    if(myCenterPoint != null) return;
    double x = 0,y = 0,z = 0;
    for(int i=0;i<mySize;i++){
      x += w[i].myPoint.x;
      y += w[i].myPoint.y;
      z += w[i].myPoint.z;
    }
    myCenterPoint = new Point3D(x / mySize, y / mySize, z / mySize);
  }

  public void world2Cam(Camera aCamera) throws PolygonException, MatrixException{
    if(!isOptimized) optimize();
    myCamSize = 0;
    while(myCamSize < mySize){
      c[myCamSize] = aCamera.world2Cam(w[myCamSize]);
      myCamSize++;
    }
    if(myNormalVector == null)  calculateNormalVector();
    myNormalCamVector = aCamera.world2Cam(myNormalVector);
    myCamCenterPoint = aCamera.world2Cam(myCenterPoint);
    //if(myTexture != null) myTexture.world2Cam(aCamera);
  }

  /*
   * method to move a polygon in world space
   *
   */
  /*
  public void translate(Camera aCamera) throws PolygonException, MatrixException{
    if(!isOptimized) optimize();
    int i = 0;
    while(i < mySize){
      w[i] = aCamera.world2Cam(w[i]);
      i++;
    }
    if(myNormalVector == null)  calculateNormalVector();
    //TODO shoud this not be if else?
    else myNormalVector = aCamera.world2Cam(myNormalVector);
    myCenterPoint = aCamera.world2Cam(myCenterPoint);
  }
  */
  
  public void translate(iTransformator aTransformator) throws PolygonException, MatrixException{
    if(!isOptimized) optimize();
    int i = 0;
    while(i < mySize){
      w[i] = aTransformator.transform(w[i]);
      i++;
    }
    if(myNormalVector == null)  {
      calculateNormalVector();
    }
    myNormalVector = aTransformator.transform(myNormalVector);
    myCenterPoint = aTransformator.transform(myCenterPoint);
    myTexture.translate(aTransformator);
  }

  public void clip2Plane(Plane aPlane) throws PolygonException{
    Vertex[] theTempVertexes = new Vertex[c.length];

    double dist1, dist2; // distances of points to plane
    double distratio; // fraction of distance between two points

    int ii, j=0;

    for(int i=0;i<myCamSize;i++){
      ii = (i+1) % myCamSize;
      dist1 = aPlane.distanceToPoint(c[i].myPoint);
      //System.out.println("Distance of point: " + c[i].myPoint.toString() + " to plane: " + aPlane.toString() + ": " + dist1);
      dist2 = aPlane.distanceToPoint(c[ii].myPoint);
      //System.out.println("Distance of point: " + c[ii].myPoint.toString() + " to plane: " + aPlane.toString() + ": " + dist2);
      //System.out.println("Dist1: " + dist1 + " dist2: " + dist2);
      if(dist1 < 0 && dist2 < 0){}
      else if(dist1 >= 0 && dist2 >= 0){
        theTempVertexes[j++] = c[i];
      } else if(dist1 > 0){
        distratio = dist1/(dist1-dist2);
        theTempVertexes[j++] = c[i];
        Vector2D theTextureDistance = Texture2.distance(myTexture, c[i].myTextureCoordinate, c[ii].myTextureCoordinate);
        theTempVertexes[j] = new Vertex(new Point3D(c[i].myPoint.x + (c[ii].myPoint.x - c[i].myPoint.x) * distratio,
            c[i].myPoint.y + (c[ii].myPoint.y - c[i].myPoint.y) * distratio,
            c[i].myPoint.z + (c[ii].myPoint.z - c[i].myPoint.z) * distratio),
            new Point2D(c[i].myTextureCoordinate.x + theTextureDistance.x * distratio,
                c[i].myTextureCoordinate.y + theTextureDistance.y * distratio),
                c[i].lightIntensity + (c[ii].lightIntensity - c[i].lightIntensity) * distratio);
        theTempVertexes[j].normal = new GVector( c[i].normal.x  + (c[ii].normal.x - c[i].normal.x) * distratio,
            c[i].normal.y + (c[ii].normal.y - c[i].normal.y) * distratio,
            c[i].normal.z + (c[ii].normal.z - c[i].normal.z) * distratio );
        j++;
        Vector2D.freeInstance(theTextureDistance);
      } else{
        Vector2D theTextureDistance = Texture2.distance(myTexture, c[ii].myTextureCoordinate, c[i].myTextureCoordinate);
        distratio = dist2/(dist2-dist1);
        theTempVertexes[j] = new Vertex(new Point3D(c[ii].myPoint.x + (c[i].myPoint.x - c[ii].myPoint.x) * distratio,
            c[ii].myPoint.y + (c[i].myPoint.y - c[ii].myPoint.y) * distratio,
            c[ii].myPoint.z + (c[i].myPoint.z - c[ii].myPoint.z) * distratio),
            new Point2D(c[ii].myTextureCoordinate.x + theTextureDistance.x * distratio,
                c[ii].myTextureCoordinate.y + theTextureDistance.y * distratio),
                c[ii].lightIntensity + (c[i].lightIntensity - c[ii].lightIntensity) * distratio);
        theTempVertexes[j].normal = new GVector( c[ii].normal.x  + (c[i].normal.x - c[ii].normal.x) * distratio,
            c[ii].normal.y + (c[i].normal.y - c[ii].normal.y) * distratio,
            c[ii].normal.z + (c[i].normal.z - c[ii].normal.z) * distratio );
        j++;
        Vector2D.freeInstance(theTextureDistance);
      }							
    }
    c = theTempVertexes;
    myCamSize = j;

  }


  public void clip2Frustrum(Frustrum aFrustrum) throws PolygonException{
    for(int i=0;i<aFrustrum.myPlanes.length;i++){
      clip2Plane(aFrustrum.myPlanes[i]);
    }
    visible = !(c.length == 0);
  }

  public String toString(){
    StringBuffer theBuffer = new StringBuffer();
    theBuffer.append("<Polygon:\n");
    theBuffer.append(" centerpoint: " + myCenterPoint);
    theBuffer.append(" camSize: " + myCamSize);
    for(int i=0;i<myCamSize;i++){
      theBuffer.append(i + ": ");
      if(c[i] != null) theBuffer.append(c[i].toString());
      theBuffer.append("\n");
    }
    theBuffer.append(" normalVector: " + myNormalVector.toString());
    theBuffer.append("/>");
    return theBuffer.toString();
  }


  public void setColor(Color aColor) {
    color = aColor;
    lightedColor = aColor;
  }

  public boolean containsVertex(Vertex aVertex){
    for(int i=0;i<w.length;i++){
      if(w[i].equals(aVertex)) return true;
    }
    return false;
  }

  public Color getColor() {
    return color;
  }


  public void setTexture(TextureImage aTextureImage,  boolean isSphericalTexture){
    myTextureImage = aTextureImage;
    this.isSphericalTexture = isSphericalTexture;
    calculateTexturePoints();
  }
  
  public void setTexture(String aTexture){
    setTexture(aTexture, true, false);
  }
  
  public void setTexture(String aTexture, boolean isTransparent, boolean isSphericalTexture){
    myTextureName = aTexture;
    isTransparentTexture = isTransparent;
    this.isSphericalTexture = isSphericalTexture;
    calculateTexturePoints();
  }
  
  public void setTexture(String aTexture, String aBumpMap, boolean isTransparent, boolean isSphericalTexture){
      myTextureName = aTexture;
      myBumpMap = aBumpMap;
      isTransparentTexture = isTransparent;
      this.isSphericalTexture = isSphericalTexture;
      calculateTexturePoints();
  }

  public Texture2 getTexture(){
    return myTexture;
  }

  public Point3D getCenterPoint() {
    return myCamCenterPoint;
  }

}