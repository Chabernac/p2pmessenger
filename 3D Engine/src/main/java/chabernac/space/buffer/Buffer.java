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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import chabernac.space.Polygon;
import chabernac.space.Polygon2D;
import chabernac.space.Vertex2D;
import chabernac.space.World;
import chabernac.space.geom.Point2D;


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

  private Map<Object, DrawingRectangleContainer> myDrawingAreas = new HashMap<Object, DrawingRectangleContainer>();
  private double myMinY, myMaxY;
  
  private iDepthBuffer myDepthBuffer = null;
  
  public Buffer(World aWorld, int aWidth, int aHeight){
    myWidth = aWidth;
    myHeight = aHeight;
    myWorld = aWorld;
    mySize = myWidth * myHeight;
    init();
  }

  private void init(){
    myImage = new BufferedImage(myWidth, myHeight, BufferedImage.TYPE_INT_ARGB);
    myGraphics = myImage.getGraphics();
    //we should be able to specify the depth buffering technique
    myDepthBuffer = new ZBuffer( myWidth, myHeight );
    clearFull();
  }

  public final Image getImage(){
    return myImage;
  }


  public void clearFull(){
    setBackGroundColor( myBackGroundColor );
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
    if(theRect.minX == -1 || aSegment.getX() < theRect.minX)  theRect.minX = aSegment.getX();
    if(theRect.maxX == -1 || aSegment.getXEnd() > theRect.maxX)  theRect.maxX = aSegment.getXEnd();

    while(aSegment.hasNext()){
      aSegment.next();
      setPixelAt( aSegment.getX(),y, aSegment.getInverseZ(), aSegment.applyShading());
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
      DrawingRectangleContainer theContainer = new DrawingRectangleContainer();
      theContainer.getClearingRect().minX = 0;
      theContainer.getClearingRect().maxX = myWidth - 1;
      theContainer.getClearingRect().minY = 0;
      theContainer.getClearingRect().maxY = myHeight - 1;
      myDrawingAreas.put( new Integer(aBackGroundColor), theContainer );
      clear(theContainer.getClearingRect());
    }
  }

  public Map<Object, DrawingRectangleContainer> getDrawingAreas() {
    return myDrawingAreas;
  }
  
  public void drawLine(Vertex2D aStartPoint, Vertex2D anEndEPoint, int aColor){
    Vertex2D theTempVertex = null;

    double xDiff =  anEndEPoint.getPoint().x - aStartPoint.getPoint().x;
    double yDiff =  anEndEPoint.getPoint().y - aStartPoint.getPoint().y;

    if(Math.abs(xDiff) > Math.abs(yDiff)){
      if(aStartPoint.getPoint().x > anEndEPoint.getPoint().x){
        theTempVertex = aStartPoint;
        aStartPoint = anEndEPoint;
        anEndEPoint = theTempVertex;
      }
      double zDiff = anEndEPoint.getInverseDepth() - aStartPoint.getInverseDepth();
      double deltaY = yDiff / xDiff;
      double deltaZ = zDiff / xDiff;
      double y = aStartPoint.getPoint().y;
      double z = aStartPoint.getInverseDepth();
      for(int x=(int)Math.ceil(aStartPoint.getPoint().x);x<(int)Math.floor(anEndEPoint.getPoint().x);x++){
        setPixelAt( (int)x, (int)y, z, aColor);
        y += deltaY;
        z += deltaZ;
      }

    } else {
      if(aStartPoint.getPoint().y > anEndEPoint.getPoint().y){
        theTempVertex = aStartPoint;
        aStartPoint = anEndEPoint;
        anEndEPoint = theTempVertex;
      }
      double zDiff = anEndEPoint.getInverseDepth() - aStartPoint.getInverseDepth();
      double deltaX = xDiff / yDiff;
      double deltaZ = zDiff / yDiff;
      double x = aStartPoint.getPoint().x;
      double z = aStartPoint.getInverseDepth();
      for(int y=(int)Math.ceil(aStartPoint.getPoint().y);y<(int)Math.floor(anEndEPoint.getPoint().y);y++){
        setPixelAt( (int)x, y, z, aColor);
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

}
