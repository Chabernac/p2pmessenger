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
import java.util.Map;
import java.util.WeakHashMap;

import chabernac.space.World;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Polygon2D;
import chabernac.space.geom.Vertex2D;
import chabernac.space.geom.VertexLine2D;
import chabernac.space.shading.BumpShader;
import chabernac.space.shading.DepthShader;
import chabernac.space.shading.PhongShader;
import chabernac.space.shading.SpecularShader;
import chabernac.space.shading.TextureShader;
import chabernac.space.shading.iPixelShader;


public class Graphics3D2D implements iBufferStrategy {
  private Font myFont = new Font("Arial", Font.PLAIN, 10);
  protected BufferedImage myImage = null;
  protected int myWidth, myHeight,mySize;
  //Graphics object only serves for debugging purposes
  protected Graphics myGraphics = null;
  private int myBackGroundColor = new Color(0,0,0,0).getRGB();
  protected World myWorld = null;

  private Map<Object, DrawingRectangleContainer> myDrawingAreas = new WeakHashMap<Object, DrawingRectangleContainer>();

  private iDepthBuffer myDepthBuffer = null;
  private iPixelShader[] myPixelShaders = null;

  private boolean isUsePartialClearing = true;

  public static enum Shader{TEXTURE, BUMP, DEPTH, PHONG, SPECULAR};
  
  private iPixelSetter myPixelSetter = new SinglePixelSetter();
  
  private iPixelListener myPixelListener = null;

  private int myYStep = 1;
  private int myXStep = 1;
  private float xMax, yMax;
  
  private int[] myPixels;

  public Graphics3D2D(World aWorld, int aWidth, int aHeight){
    myWidth = aWidth;
    myHeight = aHeight;
    myWorld = aWorld;
    mySize = myWidth * myHeight;
    init();
    setPixelShaders( new Shader[]{Shader.TEXTURE, Shader.BUMP, Shader.PHONG} );
  }
  
  private void init(){
    myImage = new BufferedImage(myWidth, myHeight, BufferedImage.TYPE_INT_ARGB);
    myGraphics = myImage.getGraphics();
    DataBufferInt db = (DataBufferInt) myImage.getRaster().getDataBuffer();
    myPixels = db.getData();
    //we should be able to specify the depth buffering technique
    myDepthBuffer = new ZBuffer( myWidth, myHeight );
    definePixelSetter();
    clearFull();
  }
  
  public void setDimensions(int aWidth, int aHeight){
    myWidth = aWidth;
    myHeight = aHeight;
    init();
  }
  
  private void definePixelSetter(){
    if(myXStep == 1 && myYStep == 1){
      myPixelSetter = new SinglePixelSetter();
    } else {
      myPixelSetter = new MultiPixelSetter( myXStep, myYStep );
    }
  }

  public void setPixelShaders(Shader[] aShaderList){
    myPixelShaders = new iPixelShader[aShaderList.length];
    int i=0;
    for(Shader theShader : aShaderList){
      if(theShader == Shader.TEXTURE) myPixelShaders[i++] = new TextureShader( ); 
      else if(theShader == Shader.BUMP) myPixelShaders[i++] = new BumpShader( myWorld );
      else if(theShader == Shader.DEPTH) myPixelShaders[i++] = new DepthShader( 5000 );
      else if(theShader == Shader.PHONG) myPixelShaders[i++] = new PhongShader( myWorld );
      else if(theShader == Shader.SPECULAR) myPixelShaders[i++] = new SpecularShader( myWorld );
    }
  }

  public void setPixelShaders(iPixelShader[] aShaders){
    myPixelShaders = aShaders;
  }

  public final Image getImage(){
    return myImage;
  }

  public void clearFull(){
    for(int i=0;i<myPixels.length;i++){
      myPixels[i] = myBackGroundColor;
    }
  }

  public final void clear(){
    if(myDepthBuffer != null){
      myDepthBuffer.clearBuffer();
    }

    if(isUsePartialClearing){
      for(DrawingRectangleContainer theRect : myDrawingAreas.values()){
        clear(theRect.getClearingRect());
      }
    } else {
      clearFull();
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

  private void defineDrawingRect(Segment aSegment, int y, Object anObject){
    if(!myDrawingAreas.containsKey( anObject )){
      myDrawingAreas.put( anObject, new DrawingRectangleContainer() );
    }

    DrawingRectangleContainer theRectContainer = myDrawingAreas.get(anObject);
    DrawingRectangle theRect = theRectContainer.getDrawingRect();

    if(theRect.minY == -1 || y < theRect.minY)  theRect.minY = y;
    if(theRect.maxY == -1 || y > theRect.maxY)  theRect.maxY = y;
//    if(theRect.minX == -1 || aSegment.getXStart() < theRect.minX)  theRect.minX = aSegment.getXStart();
//    if(theRect.maxX == -1 || aSegment.getXEnd() > theRect.maxX)  theRect.maxX = aSegment.getXEnd();

  }

  protected void drawSegment(Segment aSegment, int y, Object anObject){
    if(isUsePartialClearing){
      defineDrawingRect(aSegment, y, anObject );
    }

    Pixel thePixel = aSegment.getPixel();

    while(aSegment.hasNext()){
      aSegment.next();

      myPixelSetter.setPixel(thePixel);
      
      if(myPixelListener != null) myPixelListener.pixelCalculated(thePixel);
    } 
  }

  private void clear(DrawingRectangle aRect){
    if(aRect.minX == -1) return;
    for(int x = aRect.minX;x<=aRect.maxX;x++){
      for(int y = aRect.minY;y<=aRect.maxY;y++){
        myPixels[y * myWidth + x] = myBackGroundColor;
      }
    }
  }

  public final int getHeight(){
    return myHeight;
  }

  public final int getWidth(){
    return myWidth;
  }

  public void setPixelAt(int x, int y, float anInverseDepth, int aColor){
    if(myDepthBuffer.isDrawPixel( x, y, anInverseDepth )){
      setPixelAt(x, y, aColor);
    }
  }

  protected void setPixelAt(int x, int y, int aColor){

    //TODO we sometimes paint pixels at the border giving out of bounds exceptions
    //this should in fact never happen and the following 2 lines could be removed
    if(x >= myWidth) return;    
    if(y >= myHeight) return;
    if(x < 0) return;
    if(y < 0) return;

    myPixels[y * myWidth + x] = aColor;
  }
  
  protected int getPixelAt(int x, int y){
    return myPixels[y * myWidth + x];
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
    if(isUsePartialClearing){
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
    }

    Vertex2D theTempVertex = null;

    Vertex2D theStartPoint = aLine.getStart();
    Vertex2D theEndPoint = aLine.getEnd();
    float xDiff =  theEndPoint.getPoint().x - theStartPoint.getPoint().x;
    float yDiff =  theEndPoint.getPoint().y - theStartPoint.getPoint().y;

    if(Math.abs(xDiff) > Math.abs(yDiff)){
      if(theStartPoint.getPoint().x > theEndPoint.getPoint().x){
        theTempVertex = theStartPoint;
        theStartPoint = theEndPoint;
        theEndPoint = theTempVertex;
      }
      float zDiff = theEndPoint.getInverseDepth() - theStartPoint.getInverseDepth();
      float deltaY = yDiff / xDiff;
      float deltaZ = zDiff / xDiff;
      float y = theStartPoint.getPoint().y;
      float z = theStartPoint.getInverseDepth();
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
      float zDiff = theEndPoint.getInverseDepth() - theStartPoint.getInverseDepth();
      float deltaX = xDiff / yDiff;
      float deltaZ = zDiff / yDiff;
      float x = theStartPoint.getPoint().x;
      float z = theStartPoint.getInverseDepth();
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

    float[] minmax = BufferTools.findMinMaxY(aPolygon);
    yMax = minmax[1];

    //TimeTracker.logTime("finding min max y");
    Vertex2D[] theScanLine;
    for(int y = (int)Math.ceil(minmax[0]);y <= minmax[1];y += myYStep){
      //TimeTracker.start();
      theScanLine = aPolygon.intersectHorizontalLine(y);
      //TimeTracker.logTime("Intersecting with horizontal line: " + y);
      if(theScanLine.length == 2 && theScanLine[0] != null && theScanLine[1] != null){
        //        synchronized(this){
        //TODO is the math.floor necessar?
        Segment theSegment = Segment.getInstance(theScanLine[0],theScanLine[1], (y * myWidth + (int)Math.floor(theScanLine[0].getPoint().x)), aPolygon.getTexture(), myXStep ); 
        //        Segment theSegment = new Segment(theScanLine[0],theScanLine[1], aPolygon.getTexture() );
        xMax = theScanLine[1].getPoint().x;
        drawSegment(theSegment, y, anOrigPolygon);
        Segment.freeInstance(theSegment);
        //        }
        //TimeTracker.logTime("Drawing segment");;
      }
      Point2D.freeInstance(theScanLine[0].getPoint());
      Point2D.freeInstance(theScanLine[1].getPoint());
      Vertex2D.freeInstance(theScanLine[0]);
      Vertex2D.freeInstance(theScanLine[1]);
    }
  }

  public Font getFont() {
    return myFont;
  }

  public void setFont(Font anFont) {
    myFont = anFont;
  }
  public boolean isUsePartialClearing() {
    return isUsePartialClearing;
  }
  public void setUsePartialClearing( boolean aUsePartialClearing ) {
    isUsePartialClearing = aUsePartialClearing;
  }
  
  public iPixelListener getPixelListener() {
    return myPixelListener;
  }

  public void setPixelListener(iPixelListener anPixelListener) {
    myPixelListener = anPixelListener;
  }

  public int getYStep() {
    return myYStep;
  }
  public void setYStep( int aYStep ) {
    myYStep = aYStep;
    definePixelSetter();
  }
  public int getXStep() {
    return myXStep;
  }
  public void setXStep( int aXStep ) {
    myXStep = aXStep;
    definePixelSetter();
  }



  private interface iPixelSetter{
    public void setPixel(Pixel aPixel);
  }

  private class SinglePixelSetter implements iPixelSetter{
    public void setPixel(Pixel aPixel){
      if(myDepthBuffer.isDrawPixel( aPixel.index, aPixel.invZ)){
        for(iPixelShader theShader : myPixelShaders){
          theShader.calculatePixel( aPixel );
        }

        aPixel.applyLightning();

        myPixels[aPixel.index] = aPixel.color;
      }      
    }
  }
  
  private class MultiPixelSetter implements iPixelSetter{
    private final int xStep;
    private final int yStep;
    
    public MultiPixelSetter( int aXStep, int aYStep ) {
      super();
      xStep = aXStep;
      yStep = aYStep;
    }

    public void setPixel(Pixel aPixel){
      if(myDepthBuffer.isDrawPixel( aPixel.index, aPixel.invZ )){

        for(iPixelShader theShader : myPixelShaders){
          theShader.calculatePixel( aPixel );
        }

        aPixel.applyLightning();

        for(int dy=0;dy<yStep;dy++){
          int theIndex = aPixel.index + myWidth * dy;
          for(int dx=0;dx<xStep;dx++){
            //TODO we're sometimes drawing too much, find a way to not draw more then the polygon's borders
            myPixels[theIndex + dx] = aPixel.color;
          }
        }
      }      
    }
  }


}
