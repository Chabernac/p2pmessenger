/*
 * Created on 31-jul-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.buffer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import chabernac.space.World;


public abstract class AbstractBuffer implements iBufferStrategy {
  protected BufferedImage myImage = null;
  protected int myWidth, myHeight,mySize;
  //Graphics object only serves for debugging purposes
  protected Graphics g = null;
  protected Graphics myGraphics = null;
  private int debugMode = 0;
  private int myBackGroundColor = new Color(0,0,0,0).getRGB();
  protected World myWorld = null;

  
  private Map<Object, Rect> myDrawingAreas = new HashMap<Object, Rect>();
  
//  private int minX = -1, maxX = -1, minY = -1, maxY = -1;
  
  public AbstractBuffer(World aWorld, int aWidth, int aHeight){
    myWidth = aWidth;
    myHeight = aHeight;
    myWorld = aWorld;
    mySize = myWidth * myHeight;
    init();
  }
  
  private void init(){
    myImage = new BufferedImage(myWidth, myHeight, BufferedImage.TYPE_INT_ARGB);
    myGraphics = myImage.getGraphics();
    clearFull();
  }

  public final Image getImage(){
    return myImage;
  }


  public void clearFull(){
    new Rect(0,0,myWidth,myHeight).clear( myBackGroundColor );
  }

  public final void clear(){
    clearBuffer();

    for(Rect theRect : myDrawingAreas.values()){
      theRect.clear( myBackGroundColor );
      theRect.reset();
    }
  }

  protected void drawSegment(Segment aSegment, int y, Object anObject){
    if(!myDrawingAreas.containsKey( anObject )){
      myDrawingAreas.put( anObject, new Rect() );
    }
    
    Rect theRect = myDrawingAreas.get(anObject);
    
    if((theRect.minY == -1 || y < theRect.minY) && y > 0) theRect.minY = y;
    if((theRect.maxY == -1 || y > theRect.maxY) && y < myHeight) theRect.maxY = y;
    if((theRect.minX == -1 || aSegment.getX() < theRect.minX) && aSegment.getX() > 0) theRect.minX = aSegment.getX();
    if((theRect.maxX == -1 || aSegment.getXEnd() > theRect.maxX) && aSegment.getXEnd() < myWidth) theRect.maxX = aSegment.getXEnd();

    while(aSegment.hasNext()){
      aSegment.next();
      setValueAt(aSegment.getX(), y, aSegment.getInverseZ(), aSegment.getColor(), false);
    } 
  }

  protected abstract void clearBuffer();


  public final int getHeight(){
    return myHeight;
  }

  public final int getWidth(){
    return myWidth;
  }


  protected void setPixelAt(int x, int y, int aColor){
    //TODO we sometimes pain pixels at the border giving out of bounds exceptions
    //this should in fact never happen and the following 2 lines could be removed
    if(x >= myWidth) return;
    if(y >= myHeight) return;
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
  
  private class Rect{
    private int minX = -1;
    private int maxX = -1;
    private int minY = -1;
    private int maxY = -1;
    
    public Rect(){
    }
    
    public Rect( int aMinX, int aMinY, int aMaxX, int aMaxY ) {
      super();
      minX = aMinX;
      maxX = aMaxX;
      minY = aMinY;
      maxY = aMaxY;
    }

    public void reset(){
      minX = -1;
      maxX = -1;
      minY = -1;
      maxY = -1;
    }
    
    public void clear(int aColor){
      for(int x = minX;x<maxX;x++){
        for(int y = minY;y<maxY;y++){
          setPixelAt( x, y, aColor);
        }
      }
    }
  }

}
