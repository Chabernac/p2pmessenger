package chabernac.space;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Collection;

import chabernac.space.buffer.DrawingRectangle;
import chabernac.space.buffer.DrawingRectangleContainer;
import chabernac.space.buffer.iBufferStrategy;
import chabernac.space.geom.GVector;
import chabernac.space.geom.GeomFunctions;
import chabernac.space.geom.Line2D;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.PointShape;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Polygon2D;
import chabernac.space.geom.Shape;
import chabernac.space.geom.Vertex2D;
import chabernac.space.geom.VertexLine2D;
import chabernac.space.shading.iVertexShader;
import chabernac.space.texture.Texture2;

public class Graphics3D{
  //public static final boolean debug = false;

  private iVertexShader myLightManager = null;
  private Point3D myEyePoint = null;
  private Frustrum myFrustrum = null;
  private Camera myCamera = null;
  private World myWorld= null;
  private int myBackGroundColor = Color.black.getRGB();
  //private Graphics myGraphics = null;
  private boolean drawNormals = false;
  private boolean drawVertexNormals = false;
  private boolean drawRibs = false;
  private boolean drawBackFacing = false;
  private boolean drawPlanes = true;
  private boolean drawTextureNormals = false;
  private boolean drawLightSources = false;
  private boolean drawWorldOrigin = false;
  private boolean drawTextureCoordinates = false;
  private boolean drawCamZ = false;
  private boolean isShowDrawingAreas = false;
  private boolean isUseClipping = false;
  
  private boolean isUseTextureShading = true;

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
    myBuffer.drawLine(new VertexLine2D( theStartVertex, theEndVertex, aColor));

    /*
		 Point2D theStartPoint = GeomFunctions.cam2Screen(aStartPoint, myEyePoint);
		 Point2D theEndPoint = GeomFunctions.cam2Screen(anEndPoint, myEyePoint);
		 aGraphics.drawLine((int)theStartPoint.getX(), (int)theStartPoint.getY(), (int)theEndPoint.getX(), (int)theEndPoint.getY());
     */
  }

  public void drawText(String aText, Point3D aPoint, Color aColor){
    Point2D thePoint = GeomFunctions.cam2Screen(aPoint, myEyePoint);
    myBuffer.drawText(thePoint, aText, aColor);
  }

  private void drawWorldAxis(){
    drawCoordinateSystem(new CoordinateSystem(new Point3D(0,0,0), new GVector(1,0,0), new GVector(0,1,0), new GVector(0,0,1)));
  }

  public void drawCoordinateSystem(CoordinateSystem aSystem){
    double enLargement = 100;

    Point3D theOrigin = myCamera.world2Cam(aSystem.getOrigin());

    GVector theCamXVector = myCamera.world2Cam(aSystem.getXUnit().multip(enLargement));
    GVector theCamYVector = myCamera.world2Cam(aSystem.getYUnit().multip(enLargement));
    GVector theCamZVector = myCamera.world2Cam(aSystem.getZUnit().multip(enLargement));

    Point3D theXEndPoint = theOrigin.addition(theCamXVector);
    Point3D theYEndPoint = theOrigin.addition(theCamYVector);
    Point3D theZEndPoint = theOrigin.addition(theCamZVector);

    //		Debug.log(this,"Drawing line from " + theOrigin + " --> " + theXEndPoint);

    drawLine(theOrigin, theXEndPoint, Color.red.getRGB());

    //		Debug.log(this,"Drawing line from " + theOrigin + " --> " + theYEndPoint);

    drawLine(theOrigin, theYEndPoint, Color.green.getRGB());

    //		Debug.log(this,"Drawing line from " + theOrigin + " --> " + theZEndPoint);

    drawLine(theOrigin, theZEndPoint, Color.yellow.getRGB());
  }

  public void drawTextureNormals(Texture2 aTexture){
    drawCoordinateSystem(aTexture.getSystem());
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

        if(drawPlanes) {
          fillPolygon(aShape.myPolygons[i]);
        }
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

        if(drawVertexNormals){
          Vertex[] theCamVertextes = aShape.myPolygons[i].c;
          for(int j=0;j<aShape.myPolygons[i].myCamSize;j++){
            GVector theVector = (GVector)(theCamVertextes[j].normal.clone());
            theVector.multiply(100);
            Point3D thePoint = (Point3D)(theCamVertextes[j].myPoint.clone());
            thePoint.add(theVector);
            drawLine(theCamVertextes[j].myPoint, thePoint, Color.white.getRGB());
          }
        }

        if(drawTextureNormals && aShape.myPolygons[i].getTexture() != null){
          drawTextureNormals(aShape.myPolygons[i].getTexture());
        }

        if(drawTextureCoordinates){
          Vertex[] theCamVertextes = aShape.myPolygons[i].c;
          for(int j=0;j<aShape.myPolygons[i].myCamSize;j++){
            Point2D theTextureCoordinate = theCamVertextes[j].myTextureCoordinate; 
            String theText = (int)theTextureCoordinate.x + "," + (int)theTextureCoordinate.y;
            Color theColor = Color.white;
            if((int)theTextureCoordinate.x == 0){
              //System.out.println("RED: " + theTextureCoordinate);
              theColor = Color.red;
            }
            drawText(theText, theCamVertextes[j].myPoint, theColor);
          }
        }

        if(drawCamZ){
          Vertex[] theCamVertextes = aShape.myPolygons[i].c;
          for(int j=0;j<aShape.myPolygons[i].myCamSize;j++){
            Point3D theCoordinate = theCamVertextes[j].myPoint; 
            String theText = Double.toString(theCoordinate.z);
            Color theColor = Color.white;
            drawText(theText, theCoordinate, theColor);
          }
        }

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
    myBuffer.drawPolygon(convertPolygon(aPolygon), aPolygon);
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

  public void drawWorld(Graphics aG){
    Rectangle theOrigClip = aG.getClipBounds();
    
    myBuffer.setGraphics(aG);
    myBuffer.setBackGroundColor(myBackGroundColor);

    myBuffer.clear();

    myWorld.getTranslateManagerContainer().doTranslation();

    myWorld.world2Cam(myCamera);

    //myWorld.sort();

    myWorld.clip2Frustrum(myFrustrum);

    if(myLightManager != null) myLightManager.calculateLight(myWorld);

    for(int i=myWorld.mySize - 1;i>=0;i--){
      drawShape(myWorld.myShapes[i], aG);
    }

    if(drawWorldOrigin) drawWorldAxis();

    if(isUseClipping){
      Image theImage = myBuffer.getImage();
      Collection<DrawingRectangleContainer> theDrawingAreas = myBuffer.getDrawingRectangles();
      for(DrawingRectangleContainer theRect : theDrawingAreas){
        DrawingRectangle theSpanningRect = theRect.getSpanningRect();
        aG.setClip( theSpanningRect.getX(), theSpanningRect.getY(), theSpanningRect.getWidth() + 1, theSpanningRect.getHeight() + 1);
        aG.drawImage(theImage, 0,0, null);
      }  
    } else {
      aG.drawImage(myBuffer.getImage(), 0,0, null);
    }
    
    aG.setClip( theOrigClip );
    
    for(int i=myWorld.myPointShapeSize - 1;i>=0;i--){
      drawPointShape(myWorld.myPointShapes[i], aG);
    }

    if(drawLightSources){
      for(int i=0;i<myWorld.getLightSources().size();i++){
        drawLightSource( (LightSource)(myWorld.getLightSources().get(i)), aG );
      }
    }

    if(isShowDrawingAreas) showDrawingAreas(aG);
    

    myBuffer.cycleDone();

  }

  private void showDrawingAreas(Graphics aG){
    for(DrawingRectangleContainer theRectContainer : myBuffer.getDrawingRectangles()){
//      aG.setColor( Color.red );
//      DrawingRectangle theClaeringRect = theRectContainer.getClearingRect();
//      aG.drawRect( theClaeringRect.getX(), theClaeringRect.getY(), theClaeringRect.getWidth(), theClaeringRect.getHeight());

      aG.setColor( Color.blue);
      DrawingRectangle theDrawingRect = theRectContainer.getDrawingRect();
      aG.drawRect( theDrawingRect.getX(), theDrawingRect.getY(), theDrawingRect.getWidth(), theDrawingRect.getHeight());
    }
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

  public void setLightManager(iVertexShader aLightManager){
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

  public Point3D getEyePoint(){
    return myEyePoint;
  }

  public void setEyePoint(Point3D anEyePoint){
    myEyePoint = anEyePoint;
  }

  public Camera getCamera(){
    return myCamera;
  }

  public void setCamera(Camera aCamera){
    myCamera = aCamera;
  }

  public World getWorld(){
    return myWorld;
  }

  public void setWorld(World aWorld){
    myWorld = aWorld;
  }

  public Frustrum getFrustrum(){
    return myFrustrum;
  }

  public void setFrustrum(Frustrum aFrustrum){
    myFrustrum = aFrustrum;
  }

  public void setBufferStrategy(iBufferStrategy aStrategy){
    myBuffer = aStrategy;
  }

  public boolean isDrawVertexNormals() {
    return drawVertexNormals;
  }

  public void setDrawVertexNormals(boolean anDrawVertexNormals) {
    drawVertexNormals = anDrawVertexNormals;
  }

  public void setBackGroundColor(Color aColor){
    myBackGroundColor = aColor.getRGB();
  }

  public Color getBackGroundColor(){
    return new Color(myBackGroundColor);
  }

  public boolean isDrawTextureCoordinates() {
    return drawTextureCoordinates;
  }

  public void setDrawTextureCoordinates(boolean anDrawTextureCoordinates) {
    drawTextureCoordinates = anDrawTextureCoordinates;
  }

  public boolean isDrawCamZ() {
    return drawCamZ;
  }

  public void setDrawCamZ(boolean anDrawCamZ) {
    drawCamZ = anDrawCamZ;
  }

  public boolean isShowDrawingAreas() {
    return isShowDrawingAreas;
  }

  public void setShowDrawingAreas( boolean aShowDrawingAreas ) {
    isShowDrawingAreas = aShowDrawingAreas;
  }

  public boolean isUseClipping() {
    return isUseClipping;
  }

  public void setUseClipping( boolean aUseClipping ) {
    isUseClipping = aUseClipping;
  }

  public boolean isDrawLightSources() {
    return drawLightSources;
  }

  public void setDrawLightSources( boolean aDrawLightSources ) {
    drawLightSources = aDrawLightSources;
  }
}

