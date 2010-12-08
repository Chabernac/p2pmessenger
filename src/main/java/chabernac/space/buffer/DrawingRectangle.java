/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.buffer;


public class DrawingRectangle {
    protected int minX = -1;
    protected int maxX = -1;
    protected int minY = -1;
    protected int maxY = -1;
    protected int midX = -1;
    protected int midY = -1;

    public DrawingRectangle(){
    }

    public DrawingRectangle( int aMinX, int aMinY, int aMaxX, int aMaxY ) {
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
    
    public int getWidth(){
      return maxX - minX + 1;
    }
    
    public int getHeight(){
      return maxY - minY + 1;
    }
    
    public int getX(){
      return minX;
    }
    
    public int getY(){
      return minY;
    }
    
    protected void calculateCenter(){
      if(midX == -1) midX = (minX + maxX) / 2;
      if(midY == -1) midY = (minY + maxY) / 2;
    }
    
    public boolean intersects(DrawingRectangle aRectangle){
      calculateCenter();
      aRectangle.calculateCenter();
      
      int midXDistance = Math.abs(aRectangle.midX - midX);
      int midYDistance = Math.abs(aRectangle.midY - midY);
      
      if(midXDistance > (getWidth() + aRectangle.getWidth()) >> 1) return false;
      if(midYDistance > (getHeight() +  aRectangle.getHeight()) >> 1) return false;
      
      return true;
    }
    
    public int surface(){
      return getWidth() * getHeight();
    }
    
    public DrawingRectangle merge(DrawingRectangle aRectangle){
      DrawingRectangle theNewRect = new DrawingRectangle();
      theNewRect.minX = minX < aRectangle.minX ? minX : aRectangle.minX;
      theNewRect.maxX = maxX > aRectangle.maxX ? maxX : aRectangle.maxX;
      theNewRect.minY = minY < aRectangle.minY ? minY : aRectangle.minY;
      theNewRect.maxY = maxY > aRectangle.maxY ? maxY : aRectangle.maxY;
      return theNewRect;
    }
}
