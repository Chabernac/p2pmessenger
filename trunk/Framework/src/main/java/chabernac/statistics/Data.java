package chabernac.statistics;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class Data {
  private Vector myData = null;
  private String myName = "";
  private double myMinY, myMaxY, myMinX, myMaxX = 0;
  
  public Data(String aName){
    myData = new Vector();
    setName(aName);
  }
  
  public void addValue(double x, double y){
    if(myData.size() == 0){
      myMinX = x;
      myMaxX = x;
      myMinY = y;
      myMaxY = y;
    } else {
      if(x < myMinX) myMinX = x;
      else if(x > myMaxX) myMaxX = x;
      if( y < myMinY) myMinY = y;
      else if(y > myMaxY) myMaxY = y;
    }
    myData.add(new Point2D.Double(x,y));
  }
  
  public Vector getDataVector(){
    return myData;
  }
  
  public void sort(){
    Collections.sort(myData, new DoubleSorter());
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

}
