package chabernac.space.geom;

public class Point2D{
  private static final int POOL_SIZE = 100;
  private static final Point2D[] STACK = new Point2D[POOL_SIZE];
  private static int countFree;

  public float x;
  public float y;

  private Point2D(){

  }

  public Point2D(float x, float y){
    this.x = x;
    this.y = y;
  }

  public float getX(){ return x; }
  public float getY(){ return y; }

  public String toString(){
    return "<Point2D x: " + x + " y: " + y + ">";
  }

  public boolean equals(Point2D aPoint){
    if(x != aPoint.x) return false;
    if(y != aPoint.y) return false;
    return true;
  }

  public static synchronized Point2D getInstance(float x, float y) {
    Point2D result;
    if (countFree == 0) {
      result = new Point2D();
    } else {
      result = STACK[--countFree];
    }
    result.x = x;
    result.y = y;
    return result;
  }

  public static synchronized void freeInstance(Point2D aPoint) {
    if (countFree < POOL_SIZE) {
      STACK[countFree++] = aPoint;
    }
  }

}