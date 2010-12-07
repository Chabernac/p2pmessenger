package chabernac.space;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Collection;

import chabernac.space.buffer.DrawingRectangle;
import chabernac.space.buffer.DrawingRectangleContainer;
import chabernac.space.buffer.Graphics3D2D;
import chabernac.space.buffer.iBufferStrategy;
import chabernac.space.geom.GVector;
import chabernac.space.geom.GeomFunctions;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.PointShape;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Polygon2D;
import chabernac.space.geom.Shape;
import chabernac.space.geom.Vertex2D;
import chabernac.space.geom.VertexLine2D;
import chabernac.space.shading.FlatShading;
import chabernac.space.shading.GouroudShading;
import chabernac.space.shading.iVertexShader;
import chabernac.space.texture.Texture2;
import chabernac.space.texture.TextureImage;

public class Graphics3D{
  public static enum VertextShader{FLAT, GOUROUD};

  private iVertexShader[] myVertexShaders = null;
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
  private boolean isUseClipping = true;
  private boolean isDrawBumpVectors = false;
  private boolean isSingleFullRepaint = true;

  private Graphics3D2D myGraphics3D2D = null;

  public Graphics3D(Frustrum aFrustrum, Point3D anEyePoint, Camera aCamera, World aWorld, Graphics3D2D aBuffer){
    myFrustrum = aFrustrum;
    myEyePoint = anEyePoint;
    myCamera = aCamera;
    myWorld = aWorld;
    myGraphics3D2D = aBuffer;
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
    myGraphics3D2D.drawLine(new VertexLine2D( theStartVertex, theEndVertex, aColor));

    /*
     Point2D theStartPoint = GeomFunctions.cam2Screen(aStartPoint, myEyePoint);
     Point2D theEndPoint = GeomFunctions.cam2Screen(anEndPoint, myEyePoint);
     aGraphics.drawLine((int)theStartPoint.getX(), (int)theStartPoint.getY(), (int)theEndPoint.getX(), (int)theEndPoint.getY());
     */
  }

  public void drawText(String aText, Point3D aPoint, Color aColor){
    Point2D thePoint = GeomFunctions.cam2Screen(aPoint, myEyePoint);
    myGraphics3D2D.drawText(thePoint, aText, aColor);
  }

  private void drawWorldAxis(){
    drawCoordinateSystem(new CoordinateSystem(new Point3D(0,0,0), new GVector(1,0,0), new GVector(0,1,0), new GVector(0,0,1)));
  }

  public void drawCoordinateSystem(CoordinateSystem aSystem){
    double enLargement = 100;

    Point3D theOrigin = aSystem.getOrigin();

    GVector theCamXVector = myCamera.world2Cam(aSystem.getXUnit().multip(enLargement));
    GVector theCamYVector = myCamera.world2Cam(aSystem.getYUnit().multip(enLargement));
    GVector theCamZVector = myCamera.world2Cam(aSystem.getZUnit().multip(enLargement));

    Point3D theXEndPoint = theOrigin.addition(theCamXVector);
    Point3D theYEndPoint = theOrigin.addition(theCamYVector);
    Point3D theZEndPoint = theOrigin.addition(theCamZVector);

    //    Debug.log(this,"Drawing line from " + theOrigin + " --> " + theXEndPoint);

    drawLine(theOrigin, theXEndPoint, Color.red.getRGB());

    //    Debug.log(this,"Drawing line from " + theOrigin + " --> " + theYEndPoint);

    drawLine(theOrigin, theYEndPoint, Color.green.getRGB());

    //    Debug.log(this,"Drawing line from " + theOrigin + " --> " + theZEndPoint);

    drawLine(theOrigin, theZEndPoint, Color.yellow.getRGB());
  }

  public void drawTextureNormals(Texture2 aTexture){
    drawCoordinateSystem(aTexture.getCamSystem());
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
        
        if(isDrawBumpVectors){
          drawBumpMap(aShape.myPolygons[i].getTexture());
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
//    thePolygon.setColor(aPolygon.getColor());
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
    myGraphics3D2D.drawPolygon(convertPolygon(aPolygon), aPolygon);
    
  }
  
  public void drawBumpMap(Texture2 aTexture){
    if(aTexture.getBumpMap() == null) return;
    TextureImage theImage = aTexture.getBumpMap().getImage();
    
    for(int x=0;x<theImage.width;x+=10){
      for(int y=0;y<theImage.height;y+=10){
        GVector theVector = aTexture.getBumpMap().getNormalAt(x, y).multip(30);
        Point3D theCamPoint = aTexture.getCamSystem().getTransformator().inverseTransform(new Point3D(x,y,0));
        drawLine(theCamPoint, theCamPoint.addition(theVector), Color.red.getRGB());
      }
    }
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
    g.setColor(aPolygon.color);
    g.fillPolygon(xPoints, yPoints, aPolygon.myCamSize);
  }

  public void drawWorld(Graphics aG, long aCycle){
    Rectangle theOrigClip = aG.getClipBounds();
    
    myGraphics3D2D.setBackGroundColor(myBackGroundColor);

    myGraphics3D2D.clear();

    myWorld.getTranslateManagerContainer().doTranslation();

    myWorld.world2Cam(myCamera);

//    myWorld.sort();

    myWorld.clip2Frustrum(myFrustrum);

    for(iVertexShader theShader : myVertexShaders){
      theShader.applyShading( myWorld );
    }

    for(int i=myWorld.mySize - 1;i>=0;i--){
      drawShape(myWorld.myShapes[i], aG);
    }

    if(drawWorldOrigin) drawWorldAxis();

    if(!isSingleFullRepaint && isUseClipping && ((aCycle & 256) != 256)){
      Image theImage = myGraphics3D2D.getImage();
      Collection<DrawingRectangleContainer> theDrawingAreas = myGraphics3D2D.getDrawingRectangles();
      for(DrawingRectangleContainer theRect : theDrawingAreas){
        DrawingRectangle theSpanningRect = theRect.getSpanningRect();
        aG.setClip( theSpanningRect.getX(), theSpanningRect.getY(), theSpanningRect.getWidth() + 1, theSpanningRect.getHeight() + 1);
        aG.drawImage(theImage, 0,0, null);
      }  
    } else {
      aG.drawImage(myGraphics3D2D.getImage(), 0,0, null);
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
    

    myGraphics3D2D.cycleDone();

    isSingleFullRepaint = false;
  }

  private void showDrawingAreas(Graphics aG){
    for(DrawingRectangleContainer theRectContainer : myGraphics3D2D.getDrawingRectangles()){
      aG.setColor( Color.red );
      DrawingRectangle theClaeringRect = theRectContainer.getClearingRect();
      aG.drawRect( theClaeringRect.getX(), theClaeringRect.getY(), theClaeringRect.getWidth(), theClaeringRect.getHeight());

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

  public void setVertexShaders(VertextShader[] aVertexShaders){
    myVertexShaders = new iVertexShader[aVertexShaders.length];
    int i=0;
    for(VertextShader theShader : aVertexShaders){
      if(theShader == VertextShader.FLAT) myVertexShaders[i++] = new FlatShading( 0.3 );
      else if(theShader == VertextShader.GOUROUD) myVertexShaders[i++] = new GouroudShading( 0.3 );
    }
  }
  
  public void setVertexShaders(iVertexShader[] aVertexShaders){
    myVertexShaders = aVertexShaders;
  }

  public iBufferStrategy getBufferStrategy(){
    return myGraphics3D2D;
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

  public Graphics3D2D getGraphics3D2D() {
    return myGraphics3D2D;
  }

  public void setGraphics3D2D( Graphics3D2D aGraphics3d2d ) {
    myGraphics3D2D = aGraphics3d2d;
  }

  public boolean isDrawBumpVectors() {
    return isDrawBumpVectors;
  }

  public void setDrawBumpVectors(boolean anIsDrawBumpVectors) {
    isDrawBumpVectors = anIsDrawBumpVectors;
  }

  public boolean isSingleFullRepaint() {
    return isSingleFullRepaint;
  }

  public void setSingleFullRepaint(boolean anIsSingleFullRepaint) {
    isSingleFullRepaint = anIsSingleFullRepaint;
  }
}

