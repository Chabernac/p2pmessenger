package chabernac.space.geom;

public class Point2D{
  public double x;
  public double y;
  
  public Point2D(double x, double y){
    this.x = x;
    this.y = y;
  }
  
  public double getX(){ return x; }
  public double getY(){ return y; }
  
  public String toString(){
    return "<Point2D x: " + x + " y: " + y + ">";
  }
  
  public boolean equals(Point2D aPoint){
	  if(x != aPoint.x) return false;
	  if(y != aPoint.y) return false;
	  return true;
  }
}