/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.buffer;


public class DrawingRectangleContainer {
  private DrawingRectangle myClearingRect;
  private DrawingRectangle myDrawingRect;
  private DrawingRectangle mySpanningRect;
  
  public DrawingRectangleContainer(){
    myClearingRect = new DrawingRectangle();
    myDrawingRect = new DrawingRectangle();
    mySpanningRect = new DrawingRectangle();
  }
  
  public void clearAndSwitch(){
    DrawingRectangle theTemp = myClearingRect;
    myClearingRect = myDrawingRect;
    myDrawingRect = theTemp;
    myDrawingRect.reset();
  }

  public DrawingRectangle getClearingRect() {
    return myClearingRect;
  }

  public DrawingRectangle getDrawingRect() {
    return myDrawingRect;
  }
  
  public DrawingRectangle getSpanningRect(){
    mySpanningRect.maxX = myClearingRect.maxX > myDrawingRect.maxX ? myClearingRect.maxX : myDrawingRect.maxX; 
    mySpanningRect.maxY = myClearingRect.maxY > myDrawingRect.maxY ? myClearingRect.maxY : myDrawingRect.maxY;
    mySpanningRect.minX = myClearingRect.minX < myDrawingRect.minX ? myClearingRect.minX : myDrawingRect.minX;
    mySpanningRect.minY = myClearingRect.minY < myDrawingRect.minY ? myClearingRect.minY : myDrawingRect.minY;
    return mySpanningRect;
  }
}
