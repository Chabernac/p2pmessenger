/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.chart;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * Object which contains data used to draw a chart 
 *
 * @version v1.0.0      Sep 11, 2006
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Sep 11, 2006 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:Guy.Chauliac@axa.be"> Guy Chauliac </a>
 */
public class Data {
  private Vector myData = null;
  private String myName = "";
  private double myMinY = 0 ;
  private double myMaxY = 0 ;
  private double myMinX = 0 ;
  private double myMaxX = 0 ;
  private static final int ADD = 1;
  private static final int SUBSTRACT = 2;
  private static final int MULTIPLY = 3;
  private static final int DIVIDE = 4;
  
  public Data(String aName){
    myData = new Vector();
    setName(aName);
  }
  
  public void addValue(double x, double y){
    myData.add(new Point2D.Double(x,y));
  }
  
  public Vector getDataVector(){
    return myData;
  }
  
  public void sort(){
    Collections.sort(myData, new DoubleSorter());
  }
  
  public void fillZeros(){
    sort();
    double previous, current = -1;
    for(int i=0;i<myData.size();i++){
      previous = current;
      current = ((Point2D.Double)myData.elementAt(i)).x;
      if(previous != -1 && (current - previous) >= 2 ){
        for(double value=previous + 1;value<current;value++){
          addValue(value, 0);
        }
        fillZeros();
      }
    }
    sort();
  }
  
  public void cumulate(){
    sort();
    for(int i=1;i<myData.size();i++){
      ((Point2D.Double)myData.elementAt(i)).y += ((Point2D.Double)myData.elementAt(i - 1)).y; 
    }
  }
  
  public void addData(Data aData){
    addData(aData, ADD);
  }
  
  public void substractData(Data aData){
    addData(aData, SUBSTRACT);
  }
  
  public void multiplyData(Data aData){
    addData(aData, MULTIPLY);
  }
  
  public void divideData(Data aData){
    addData(aData, DIVIDE);
  }
  
  private void addData(Data aData, int operation){
    Point2D.Double thePoint1 = null;
    Point2D.Double thePoint2 = null;
    for(int i=0;i<aData.getDataVector().size();i++){
      thePoint1 = (Point2D.Double)aData.getDataVector().elementAt(i);
      thePoint2 = getPoint(thePoint1.x);
      if(thePoint2 == null){
        addValue(thePoint1.x, thePoint1.y);
      } else {
        switch(operation){
          case ADD:       thePoint2.y += thePoint1.y; break;
          case SUBSTRACT: thePoint2.y -= thePoint1.y; break;
          case MULTIPLY:  thePoint2.y *= thePoint1.y; break;
          case DIVIDE:    thePoint2.y /= thePoint1.y; break;
        }
      }
    }
  }
  
  public void findBorders(){
    if(myData.size() == 0) return;
    Point2D.Double thePoint = (Point2D.Double)myData.elementAt(0);
    myMinX = myMaxX = thePoint.x;
    myMinY = myMaxY = thePoint.y;
    
    for(int i=1;i<myData.size();i++){
      thePoint = (Point2D.Double)myData.elementAt(i);
      if(thePoint.x < myMinX) myMinX = thePoint.x;
      else if(thePoint.x > myMaxX) myMaxX = thePoint.x;
      if( thePoint.y < myMinY) myMinY = thePoint.y;
      else if(thePoint.y > myMaxY) myMaxY = thePoint.y;
    }
  }
  
  public Point2D.Double getPoint(double aXValue){
    Point2D.Double thePoint = null; 
    for(int i=0;i<myData.size();i++){
      thePoint = (Point2D.Double)myData.elementAt(i);
      if(thePoint.x == aXValue) return thePoint;
    }
    return null;
  }
  
  public String getName(){
    return myName;
  }
  
  public void setName(String aName){
    myName = aName;
  }
  
  public double getMinX(){ return myMinX; }
  public double getMaxX(){ return myMaxX; }
  public double getMinY(){ return myMinY; }
  public double getMaxY(){ return myMaxY; }
  
  private class DoubleSorter implements Comparator{
    public int compare(Object o1, Object o2){
      Point2D.Double p1 = (Point2D.Double)o1;
      Point2D.Double p2 = (Point2D.Double)o2;
      return (int)(p1.x - p2.x);
    }
  }

  public String toString(){
    String theData = "";
    Point2D.Double thePoint = null;
    for(int i=0;i<myData.size();i++){
      thePoint = (Point2D.Double)myData.elementAt(i);
      theData += thePoint.toString() + "\n";
    }
    return theData;
  }
}
