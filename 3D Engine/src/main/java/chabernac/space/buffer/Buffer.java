/*
 * Created on 31-jul-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.buffer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import chabernac.space.World;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Polygon2D;
import chabernac.space.geom.Vertex2D;
import chabernac.space.geom.VertexLine2D;
import chabernac.space.shading.BumpShading;
import chabernac.space.shading.DepthShading;
import chabernac.space.shading.PhongShader;
import chabernac.space.shading.SpecularShader;
import chabernac.space.shading.TextureShader;
import chabernac.space.shading.iPixelShader;


public class Buffer implements iBufferStrategy {
  private Font myFont = new Font("Arial", Font.PLAIN, 10);
  protected BufferedImage myImage = null;
  protected int myWidth, myHeight,mySize;
  //Graphics object only serves for debugging purposes
  protected Graphics g = null;
  protected Graphics myGraphics = null;
  private int debugMode = 0;
  private int myBackGroundColor = new Color(0,0,0,0).getRGB();
  protected World myWorld = null;

  private Map<Object, DrawingRectangleContainer> myDrawingAreas = new WeakHashMap<Object, DrawingRectangleContainer>();
  private double myMinY, myMaxY;
  
  private iDepthBuffer myDepthBuffer = null;
  private iPixelShader[] myPixelShaders = null; 
  
  public Buffer(World aWorld, int aWidth, int aHeight){
    myWidth = aWidth;
    myHeight = aHeight;
    myWorld = aWorld;
    mySize = myWidth * myHeight;
    init();
    createPixelShaders();
  }
  private void init(){
    myImage = new BufferedImage(myWidth, myHeight, BufferedImage.TYPE_INT_ARGB);
    myGraphics = myImage.getGraphics();
    //we should be able to specify the depth buffering technique
    myDepthBuffer = new ZBuffer( myWidth, myHeight );
    clearFull();
  }
  
  private void createPixelShaders(){
    List<iPixelShader> theShaders = new ArrayList<iPixelShader>();
    
    theShaders.add(new TextureShader(true));
    theShaders.add(new BumpShading( myWorld ));
//    theShaders.add(new PhongShader( myWorld ));
//    theShaders.add(new DepthShading( 5000 ));
//    theShaders.add(new SpecularShader( myWorld ));
    
    myPixelShaders = theShaders.toArray( new iPixelShader[]{} );
  }

  public final Image getImage(){
    return myImage;
  }


  public void clearFull(){
    DrawingRectangleContainer theContainer = new DrawingRectangleContainer();
    theContainer.getClearingRect().minX = 0;
    theContainer.getClearingRect().maxX = myWidth - 1;
    theContainer.getClearingRect().minY = 0;
    theContainer.getClearingRect().maxY = myHeight - 1;
    myDrawingAreas.clear();
    myDrawingAreas.put( new Integer(myBackGroundColor), theContainer );
    clear(theContainer.getClearingRect());
  }

  public final void clear(){
    if(myDepthBuffer != null){
      myDepthBuffer.clearBuffer();
    }

    for(DrawingRectangleContainer theRect : myDrawingAreas.values()){
      clear(theRect.getClearingRect());
    }
  }
  
  public final void cycleDone(){
    for(DrawingRectangleContainer theRect : myDrawingAreas.values()){
      theRect.clearAndSwitch();
    }
  }
  
  public Collection<DrawingRectangleContainer> getDrawingRectangles(){
    return myDrawingAreas.values();
  }

  protected void drawSegment(Segment aSegment, int y, Object anObject){
    if(!myDrawingAreas.containsKey( anObject )){
      myDrawingAreas.put( anObject, new DrawingRectangleContainer() );
    }

    DrawingRectangleContainer theRectContainer = myDrawingAreas.get(anObject);
    DrawingRectangle theRect = theRectContainer.getDrawingRect();

    if(theRect.minY == -1 || y < theRect.minY)  theRect.minY = y;
    if(theRect.maxY == -1 || y > theRect.maxY)  theRect.maxY = y;
    if(theRect.minX == -1 || aSegment.getXStart() < theRect.minX)  theRect.minX = aSegment.getXStart();
    if(theRect.maxX == -1 || aSegment.getXEnd() > theRect.maxX)  theRect.maxX = aSegment.getXEnd();

    Pixel thePixel = aSegment.getPixel();
    
    while(aSegment.hasNext()){
      aSegment.next();
      
      setPixelAt( thePixel.x, y, thePixel.invZ, thePixel);
    } 
  }
  
  private void clear(DrawingRectangle aRect){
    if(aRect.minX == -1) return;
    for(int x = aRect.minX;x<=aRect.maxX;x++){
      for(int y = aRect.minY;y<=aRect.maxY;y++){
        setPixelAt( x, y, myBackGroundColor);
      }
    }
  }

  public final int getHeight(){
    return myHeight;
  }

  public final int getWidth(){
    return myWidth;
  }
  
  public void setPixelAt(int x, int y, double anInverseDepth, int aColor){
    if(myDepthBuffer.isDrawPixel( x, y, anInverseDepth )){
      setPixelAt(x, y, aColor);
    }
  }

  public void setPixelAt(int x, int y, double anInverseDepth, Pixel aPixel){
    if(myDepthBuffer.isDrawPixel( x, y, anInverseDepth )){
      
      for(iPixelShader theShader : myPixelShaders){
        theShader.calculatePixel( aPixel );
      }
      
      aPixel.applyLightning();
      
      setPixelAt(x, y, aPixel.color);
    }
  }

  protected void setPixelAt(int x, int y, int aColor){
    
    //TODO we sometimes pain pixels at the border giving out of bounds exceptions
    //this should in fact never happen and the following 2 lines could be removed
        if(x >= myWidth) return;    
        if(y >= myHeight) return;
        if(x < 0) return;
        if(y < 0) return;

    myImage.setRGB( x, y, aColor );
  }


  protected int getPixelAt(int x, int y){
    return myImage.getRGB( x, y );
  }

  public void setDebugMode(int aDebugMode){
    debugMode = aDebugMode;
  }

  public int getDebugMode(){
    return debugMode;
  }

  public void setGraphics(Graphics g){
    this.g = g;
  }

  public Graphics getGraphics(){
    return g;
  }

  public int getBackGroundColor() {
    return myBackGroundColor;
  }

  public void setBackGroundColor(int aBackGroundColor) {
    if(myBackGroundColor != aBackGroundColor){
      myBackGroundColor = aBackGroundColor;
      clearFull();
    }
  }

  public Map<Object, DrawingRectangleContainer> getDrawingAreas() {
    return myDrawingAreas;
  }
  
  public void drawLine(VertexLine2D aLine){
    if(!myDrawingAreas.containsKey( aLine )){
      myDrawingAreas.put( aLine, new DrawingRectangleContainer() );
    }
    
    DrawingRectangleContainer theRectContainer = myDrawingAreas.get(aLine);
    DrawingRectangle theRect = theRectContainer.getDrawingRect();
    Point2D theP1 = aLine.getStart().getPoint();
    Point2D theP2 = aLine.getEnd().getPoint();
    
    theRect.minX = (int)Math.floor( theP1.x < theP2.x ? theP1.x : theP2.x) - 10;
    theRect.maxX = (int)Math.ceil(theP1.x > theP2.x ? theP1.x : theP2.x) + 10;
    theRect.minY = (int)Math.floor(theP1.y < theP2.y ? theP1.y : theP2.y) - 10;
    theRect.maxY = (int)Math.ceil(theP1.y > theP2.y ? theP1.y : theP2.y) + 10;
    
    Vertex2D theTempVertex = null;

    Vertex2D theStartPoint = aLine.getStart();
    Vertex2D theEndPoint = aLine.getEnd();
    double xDiff =  theEndPoint.getPoint().x - theStartPoint.getPoint().x;
    double yDiff =  theEndPoint.getPoint().y - theStartPoint.getPoint().y;

    if(Math.abs(xDiff) > Math.abs(yDiff)){
      if(theStartPoint.getPoint().x > theEndPoint.getPoint().x){
        theTempVertex = theStartPoint;
        theStartPoint = theEndPoint;
        theEndPoint = theTempVertex;
      }
      double zDiff = theEndPoint.getInverseDepth() - theStartPoint.getInverseDepth();
      double deltaY = yDiff / xDiff;
      double deltaZ = zDiff / xDiff;
      double y = theStartPoint.getPoint().y;
      double z = theStartPoint.getInverseDepth();
      for(int x=(int)Math.ceil(theStartPoint.getPoint().x);x<(int)Math.floor(theEndPoint.getPoint().x);x++){
        setPixelAt( (int)x, (int)y, z, aLine.getColor());
        y += deltaY;
        z += deltaZ;
      }

    } else {
      if(theStartPoint.getPoint().y > theEndPoint.getPoint().y){
        theTempVertex = theStartPoint;
        theStartPoint = theEndPoint;
        theEndPoint = theTempVertex;
      }
      double zDiff = theEndPoint.getInverseDepth() - theStartPoint.getInverseDepth();
      double deltaX = xDiff / yDiff;
      double deltaZ = zDiff / yDiff;
      double x = theStartPoint.getPoint().x;
      double z = theStartPoint.getInverseDepth();
      for(int y=(int)Math.ceil(theStartPoint.getPoint().y);y<(int)Math.floor(theEndPoint.getPoint().y);y++){
        setPixelAt( (int)x, y, z, aLine.getColor());
        x += deltaX;
        z += deltaZ;
      }
    }
  }
  
  public void drawText(Point2D aPoint, String aText, Color aColor) {
    FontMetrics theMetrics = myGraphics.getFontMetrics(myFont);
    int theWidth = theMetrics.stringWidth(aText) + 2;
    int theHeight = theMetrics.getHeight();
    int theAscent = theMetrics.getAscent();
    BufferedImage theCharImage = new BufferedImage(theWidth, theHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics theGraphics = theCharImage.getGraphics();
    Color theTransparentColor = new Color(0,0,0,0); 
    int theTransparentC =  theTransparentColor.getRGB();
    theGraphics.setColor(theTransparentColor);
    theGraphics.fillRect(0, 0, theWidth, theHeight);
    theGraphics.setColor(aColor);
    theGraphics.drawString(aText, 0, theAscent);
    int[] thePixels = (int[])((DataBufferInt)theCharImage.getData().getDataBuffer()).getData();

    int baseX = (int)aPoint.x;
    int baseY = (int)aPoint.y;
    int x = baseX;
    int y = baseY;
    int j = 0;
    for(int i=0;i<thePixels.length;i++, x++, j++){
      if(j == theWidth){
        x = baseX;
        y++;
        j = 0;
      }

      if(x < myWidth && y < myHeight && thePixels[i] != theTransparentC){
        //System.out.println(thePixels[i] + " =?= " + theTransparentC);
        setPixelAt(x, y, thePixels[i]);
      }
    }
  }
  
  public void drawPolygon(Polygon2D aPolygon, Polygon anOrigPolygon) {
    //TimeTracker.start();
    findMinMaxY(aPolygon);
    //TimeTracker.logTime("finding min max y");
    Vertex2D[] theScanLine;
    Color theColor = aPolygon.getColor();
    for(int y = (int)Math.ceil(myMinY);y <= myMaxY;y++){
      //TimeTracker.start();
      theScanLine = aPolygon.intersectHorizontalLine(y);
      //TimeTracker.logTime("Intersecting with horizontal line: " + y);
      if(theScanLine.length == 2 && theScanLine[0] != null && theScanLine[1] != null){
        Segment theSegment = Segment.getInstance(myWorld, theScanLine[0],theScanLine[1], theColor.getRGB(), aPolygon.getTexture() ); 
        //Segment theSegment = new Segment(theScanLine[0],theScanLine[1], theColor.getRGB(), aPolygon.getTexture() );
        drawSegment(theSegment, y, anOrigPolygon);
        Segment.freeInstance(theSegment);
        //TimeTracker.logTime("Drawing segment");;
      }
      Point2D.freeInstance(theScanLine[0].getPoint());
      Point2D.freeInstance(theScanLine[1].getPoint());
      Vertex2D.freeInstance(theScanLine[0]);
      Vertex2D.freeInstance(theScanLine[1]);
    }
  }
  
  public void findMinMaxY(Polygon2D aPolygon){
    double[] minmax = BufferTools.findMinMaxY(aPolygon);
    myMinY = minmax[0];
    myMaxY = minmax[1];
  }
  
  public Font getFont() {
    return myFont;
  }

  public void setFont(Font anFont) {
    myFont = anFont;
  }
  @Override
  public void setPixelShaders(iPixelShader[] aPixelShaders) {
    myPixelShaders = aPixelShaders;
  }

}
