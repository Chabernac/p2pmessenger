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
      return maxX - minX;
    }
    
    public int getHeight(){
      return maxY - minY;
    }
    
    public int getX(){
      return minX;
    }
    
    public int getY(){
      return minY;
    }
}
