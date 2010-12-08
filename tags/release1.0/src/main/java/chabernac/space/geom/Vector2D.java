package chabernac.space.geom;


public class Vector2D {
  private static final int POOL_SIZE = 10;
  private static final Vector2D[] STACK = new Vector2D[POOL_SIZE];
  private static int countFree;
  
	public double x,y;
	
  private Vector2D(){
    
  }
  
	public Vector2D(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Vector2D(Point2D p1, Point2D p2){
		x = p2.x - p1.x;
		y = p2.y - p1.y;
	}
	
	public void invert(){
		x *= -1;
		y *= -1;
	}
	
	public void add(Vector2D aVector){
		x += aVector.x;
		y += aVector.y;
	}
		
  public static Vector2D getInstance(double x, double y) {
    Vector2D result;
    if (countFree == 0) {
      result = new Vector2D();
    } else {
      result = STACK[--countFree];
    }
    result.x = x;
    result.y = y;
    return result;
  }

  public static void freeInstance(Vector2D aVector) {
    if (countFree < POOL_SIZE) {
      STACK[countFree++] = aVector;
    }
  }

}
