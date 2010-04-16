package chabernac.space;

import java.awt.Color;
import java.awt.Graphics;

import chabernac.space.buffer.iBufferStrategy;
import chabernac.space.geom.GVector;
import chabernac.space.geom.GeomFunctions;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;
import chabernac.space.shading.iLightManager;
import chabernac.space.texture.Texture;
import chabernac.utils.Debug;

public class Graphics3D{
  public static final boolean debug = false;
	
  private iLightManager myLightManager = null;
  private Point3D myEyePoint = null;
  private Frustrum myFrustrum = null;
  private Camera myCamera = null;
  private World myWorld= null;
  private boolean drawNormals = false;
  private boolean drawRibs = true;
  private boolean drawBackFacing = false;
  private boolean drawPlanes = true;
  private boolean drawTextureNormals = false;
  
  private iBufferStrategy myBuffer = null;
  
  public Graphics3D(Frustrum aFrustrum, Point3D anEyePoint, Camera aCamera, World aWorld, iBufferStrategy aBuffer){
    myFrustrum = aFrustrum;
    myEyePoint = anEyePoint;
    myCamera = aCamera;
    myWorld = aWorld;
    myBuffer = aBuffer;
  }
  
  public void drawPoint(Point3D aPoint, Graphics g){
  	//Debug.log(this,"Drawing point: " + aPoint.toString());
	Point2D thePoint = GeomFunctions.cam2Screen(aPoint, myEyePoint);
	g.drawOval((int)thePoint.x, (int)thePoint.y, 1, 1);
  }

  public void drawLine(Point3D aStartPoint, Point3D anEndPoint, int aColor){
	Point2D theStartPoint = GeomFunctions.cam2Screen(aStartPoint, myEyePoint);
	Point2D theEndPoint = GeomFunctions.cam2Screen(anEndPoint, myEyePoint);
	Vertex2D theStartVertex = new Vertex2D(theStartPoint, aStartPoint.z, 1);
	Vertex2D theEndVertex = new Vertex2D(theEndPoint, anEndPoint.z, 1);
	myBuffer.drawLine(theStartVertex, theEndVertex, aColor);
	  
	/*
    Point2D theStartPoint = GeomFunctions.cam2Screen(aStartPoint, myEyePoint);
    Point2D theEndPoint = GeomFunctions.cam2Screen(anEndPoint, myEyePoint);
    aGraphics.drawLine((int)theStartPoint.getX(), (int)theStartPoint.getY(), (int)theEndPoint.getX(), (int)theEndPoint.getY());
    */
  }
  
  private void drawWorldAxis(){
	  GVector theXVector = new GVector(100,0,0);
	  GVector theYVector = new GVector(0,100,0);
	  GVector theZVector = new GVector(0,0,100);
	  
	  GVector theCamXVector = myCamera.world2Cam(theXVector);
	  GVector theCamYVector = myCamera.world2Cam(theYVector);
	  GVector theCamZVector = myCamera.world2Cam(theZVector);
	  
	  Point3D theOrigin = myCamera.world2Cam(new Point3D(0,0,0));
	  
	  Point3D theXEndPoint = theOrigin.addition(theCamXVector);
	  Point3D theYEndPoint = theOrigin.addition(theCamYVector);
	  Point3D theZEndPoint = theOrigin.addition(theCamZVector);
	
	  Debug.log(this,"Drawing line from " + theOrigin + " --> " + theXEndPoint);
	  
	  drawLine(theOrigin, theXEndPoint, Color.red.getRGB());
  
	  Debug.log(this,"Drawing line from " + theOrigin + " --> " + theYEndPoint);
  
	  drawLine(theOrigin, theYEndPoint, Color.green.getRGB());
	  
	  Debug.log(this,"Drawing line from " + theOrigin + " --> " + theZEndPoint);
	  
	  drawLine(theOrigin, theZEndPoint, Color.blue.getRGB());
  }
  
  public void drawTextureNormals(Texture aTexture){
	  Point3D theStartPoint = aTexture.myCamOrigin;
	  Point3D theMEndPoint = theStartPoint.addition(aTexture.myCamMVector.norm().multip(100));
	  Point3D theNEndPoint = theStartPoint.addition(aTexture.myCamNVector.norm().multip(100));
	  Point3D theAEndPoint = theStartPoint.addition(aTexture.myCamAVector.norm().multip(100));
	  Point3D theBEndPoint = theStartPoint.addition(aTexture.myCamBVector.norm().multip(100));
	  Point3D theCEndPoint = theStartPoint.addition(aTexture.myCamCVector.norm().multip(100));
	  Point3D theOriginEndPoint = theStartPoint.addition(new GVector(aTexture.myCamOrigin));
	  
	  Vertex2D theStartVertex = new Vertex2D(GeomFunctions.cam2Screen(theStartPoint, myEyePoint), theStartPoint.z, 1);
	  Vertex2D theMEndVertex = new Vertex2D(GeomFunctions.cam2Screen(theMEndPoint, myEyePoint), theMEndPoint.z, 1);
	  Vertex2D theNEndVertex = new Vertex2D(GeomFunctions.cam2Screen(theNEndPoint, myEyePoint), theNEndPoint.z, 1);
	  Vertex2D theAEndVertex = new Vertex2D(GeomFunctions.cam2Screen(theAEndPoint, myEyePoint), theAEndPoint.z, 1);
	  Vertex2D theBEndVertex = new Vertex2D(GeomFunctions.cam2Screen(theBEndPoint, myEyePoint), theBEndPoint.z, 1);
	  Vertex2D theCEndVertex = new Vertex2D(GeomFunctions.cam2Screen(theCEndPoint, myEyePoint), theCEndPoint.z, 1);
	  Vertex2D theOriginEndVertex = new Vertex2D(GeomFunctions.cam2Screen(theOriginEndPoint, myEyePoint), theOriginEndPoint.z, 1);
	  
	  myBuffer.drawLine(theStartVertex, theMEndVertex, Color.red.getRGB());
	  myBuffer.drawLine(theStartVertex, theNEndVertex, Color.red.getRGB());
//	  System.out.println("Drawing A vertex from " + theStartVertex.getPoint() + " to " + theAEndVertex.getPoint());
	  myBuffer.drawLine(theStartVertex, theAEndVertex, Color.blue.getRGB());
	  myBuffer.drawLine(theStartVertex, theBEndVertex, Color.blue.getRGB());
	  myBuffer.drawLine(theStartVertex, theCEndVertex, Color.blue.getRGB());
	  myBuffer.drawLine(theStartVertex, theOriginEndVertex, Color.green.getRGB());
	  myBuffer.drawLine(new Vertex2D(GeomFunctions.cam2Screen(new Point3D(0,0,0), myEyePoint),0,0), theStartVertex, Color.green.getRGB());
	  
	  /*
	  Point3D theStartPoint = aTexture.myCamOrigin;
	  Point3D theEndPoint = theStartPoint.addition(aTexture.myCamTestVector);
	  
	  Vertex2D theStartVertex = new Vertex2D(GeomFunctions.cam2Screen(theStartPoint, myEyePoint), theStartPoint.z, 1);
	  Vertex2D theEndVertex = new Vertex2D(GeomFunctions.cam2Screen(theEndPoint, myEyePoint), theEndPoint.z, 1);
	  myBuffer.drawLine(theStartVertex, theEndVertex);
	  */

	  
  }

  public void drawPolygon(Polygon aPolygon){
    int ii;
    for(int i=0;i<aPolygon.myCamSize;i++){
      ii = (i+1) % aPolygon.myCamSize;
      drawLine(aPolygon.c[i].myPoint, aPolygon.c[ii].myPoint, Color.white.getRGB());
    }
  }

  public void drawShape(Shape aShape, Graphics g){
	if(!aShape.visible) return;
    for(int i=0;i<aShape.mySize;i++){
	  if(drawBackFacing || aShape.myPolygons[i].doubleSided || !isBackFacing(aShape.myPolygons[i])){
        if(drawPlanes) fillPolygon(aShape.myPolygons[i]);
        if(drawRibs){
			g.setColor(Color.black);
			//Draw the outlines of the polygons
			drawPolygon(aShape.myPolygons[i]);
        }
        if(drawNormals){
	        // Draw the normal vectors.
	        GVector theVector = (GVector)(aShape.myPolygons[i].myNormalCamVector).clone();
	        theVector.multiply(100);
	        Point3D thePoint = (Point3D)(aShape.myPolygons[i].myCamCenterPoint).clone();
	        thePoint.add(theVector);
	        drawLine(aShape.myPolygons[i].myCamCenterPoint, thePoint, Color.white.getRGB());
        }
        /*
        if(drawTextureNormals && aShape.myPolygons[i].getTexture() != null){
        	drawTextureNormals(aShape.myPolygons[i].getTexture());
        }
        */
      }
    }
  }
  
  public Polygon2D convertPolygon(Polygon aPolygon){
	  Polygon2D thePolygon = new Polygon2D(aPolygon.myCamSize);
	  for(int i=0;i<aPolygon.myCamSize;i++){
		  //hier
		  thePolygon.addVertex(new Vertex2D(GeomFunctions.cam2Screen(aPolygon.c[i].myPoint, myEyePoint),  aPolygon.c[i].myTextureCoordinate, aPolygon.c[i].myPoint.z, aPolygon.c[i].lightIntensity));
	  }
	  thePolygon.setColor(aPolygon.getColor());
	  //aPolygon.getTexture().cam2screen(myEyePoint);
	  thePolygon.setTexture(aPolygon.getTexture());
	  
	  thePolygon.done();
	  return thePolygon;
  }
  
  public void drawPointShape(PointShape aShape, Graphics g){
  	g.setColor(aShape.myColor);
  	for(int i=0;i<aShape.myCamSize;i++){
  		drawPoint(aShape.c[i], g);
  	}
  }
  
  public void fillPolygon(Polygon aPolygon){
	  if(!aPolygon.visible) return;
	  if(aPolygon.myCamSize < 2) return;
	  myBuffer.drawPolygon(convertPolygon(aPolygon));
  }

  public void fillPolygon(Polygon aPolygon, Graphics g){
    int xPoints[] = new int[aPolygon.myCamSize];
    int yPoints[] = new int[aPolygon.myCamSize];
    Point2D thePoint = null;
    for(int i=0;i<aPolygon.myCamSize;i++){
      thePoint = GeomFunctions.cam2Screen(aPolygon.c[i].myPoint, myEyePoint);
      xPoints[i] = (int)thePoint.x;
      yPoints[i] = (int)thePoint.y;
    }
    g.setColor(aPolygon.lightedColor);
    g.fillPolygon(xPoints, yPoints, aPolygon.myCamSize);
  }

  public void drawWorld(Graphics g){
	myBuffer.setGraphics(g);
		 
	if(debug) System.out.println("--------------------------");
	long time1 = System.currentTimeMillis();
	myBuffer.clear();
	long time2 = System.currentTimeMillis();
	if(debug) System.out.println("Clearing buffer: " + (time2 - time1));
	
  	myWorld.getTranslateManagerContainer().doTranslation();
  	long time3 = System.currentTimeMillis();
  	if(debug) System.out.println("Translations: " + (time3 - time2));
  	
  	myWorld.world2Cam(myCamera);
  	long time4 = System.currentTimeMillis();
  	if(debug) System.out.println("World --> Camera: " + (time4 - time3));
  	
  	//myWorld.sort();
  	
  	myWorld.clip2Frustrum(myFrustrum);
  	long time5 = System.currentTimeMillis();
  	if(debug) System.out.println("Clipping: " + (time5 - time4));
  	
  	if(myLightManager != null) myLightManager.calculateLight(myWorld);
  	long time6 = System.currentTimeMillis();
  	if(debug) System.out.println("Light calculation: " + (time6 - time5));
  	
  	for(int i=myWorld.mySize - 1;i>=0;i--){
  		drawShape(myWorld.myShapes[i], g);
  	}
  	long time7 = System.currentTimeMillis();
  	if(debug) System.out.println("Drawing shapes: " + (time7 - time6));
  	
  	drawWorldAxis();
  	
  	g.drawImage(myBuffer.getImage(), 0,0, null);
  	long time8 = System.currentTimeMillis();
  	if(debug) System.out.println("Drawing image to screen: " + (time8 - time7));
  	
	for(int i=myWorld.myPointShapeSize - 1;i>=0;i--){
		drawPointShape(myWorld.myPointShapes[i], g);
	}
	long time9 = System.currentTimeMillis();
	if(debug) System.out.println("Drawing point shapes to screen: " + (time9 - time8));
	
	for(int i=0;i<myWorld.getLightSources().size();i++){
		drawLightSource( (LightSource)(myWorld.getLightSources().get(i)), g );
	}
	long time10 = System.currentTimeMillis();
	if(debug) System.out.println("Drawing light sources: " + (time10 - time9));
  }

private void drawLightSource(LightSource source, Graphics g) {
	Point3D theLocation = source.getCamLocation();
	Point2D thePoint = GeomFunctions.cam2Screen(theLocation, myEyePoint);
	g.setColor(Color.white);
	g.fillOval((int)thePoint.x - 5, (int)thePoint.y - 5, 10,10);
}

public boolean isBackFacing(Polygon aPolygon){
	  double theDotProd = aPolygon.myCamCenterPoint.x * aPolygon.myNormalCamVector.x +
	                     aPolygon.myCamCenterPoint.y * aPolygon.myNormalCamVector.y +
	                     (aPolygon.myCamCenterPoint.z + myEyePoint.z ) * aPolygon.myNormalCamVector.z;
	  if(theDotProd < 0) return false;
      return true;
  }

	public boolean isDrawBackFacing() {
		return drawBackFacing;
	}
	
	public boolean isDrawNormals() {
		return drawNormals;
	}
	
	public boolean isDrawPlanes() {
		return drawPlanes;
	}
	
	public boolean isDrawRibs() {
		return drawRibs;
	}
	
	public void setDrawBackFacing(boolean b) {
		drawBackFacing = b;
	}
	
	public void setDrawNormals(boolean b) {
		drawNormals = b;
	}
	
	public void setDrawPlanes(boolean b) {
		drawPlanes = b;
	}
	
	public void setDrawRibs(boolean b) {
		drawRibs = b;
	}
	
	public void setLightManager(iLightManager aLightManager){
		myLightManager = aLightManager;
	}
	
	public iBufferStrategy getBufferStrategy(){
		return myBuffer;
	}

	public boolean isDrawTextureNormals() {
		return drawTextureNormals;
	}

	public void setDrawTextureNormals(boolean drawTextureNormals) {
		this.drawTextureNormals = drawTextureNormals;
	}
	
	

}

