package chabernac.space.buffer;

import junit.framework.TestCase;

public class DrawingRectangleTest extends TestCase {
  public void testIntersect(){
    DrawingRectangle theRect1 = new DrawingRectangle(0,0,10,10);
    DrawingRectangle theRect2 = new DrawingRectangle(5,5,15,15);
    DrawingRectangle theRect3 = new DrawingRectangle(14,14,16,16);
    DrawingRectangle theRect4 = new DrawingRectangle(2,2,4,4);
    
    DrawingRectangle theRect5 = new DrawingRectangle(2,2,6,4);
    DrawingRectangle theRect6 = new DrawingRectangle(3,0,5,6);
    
    assertTrue(theRect1.intersects(theRect2));
    assertFalse(theRect1.intersects(theRect3));
    assertTrue(theRect2.intersects(theRect3));
    assertTrue(theRect1.intersects(theRect4));
    assertTrue(theRect4.intersects(theRect1));
    assertFalse(theRect4.intersects(theRect3));
    assertFalse(theRect3.intersects(theRect4));
    
    assertTrue(theRect5.intersects(theRect6));
    assertTrue(theRect6.intersects(theRect5));
    
    
  }
}
