package chabernac.space.geom;


public class Vertex2D {
  private static final int POOL_SIZE = 10;
  private static final Vertex2D[] STACK = new Vertex2D[POOL_SIZE];
  private static int countFree;
  
	private Point2D myPoint = null;
	private Point2D myTexturePoint = null;
	private float myInverseDepth;
	private float myLightning;
	private Point2D myPerspectiveCorrectTexturePoint = null;
	
  private Vertex2D(){
    
  }
  
	public Vertex2D(Point2D aPoint, float anInverseDepth, float aLightning){
	  myPoint = aPoint;
    myInverseDepth = anInverseDepth;
    myLightning = aLightning;
	}
	
	public Vertex2D(Point2D aPoint, Point2D aTexturePoint, float aDepth, float aLightning){
		myPoint = aPoint;
		myTexturePoint = aTexturePoint;
		myInverseDepth = aDepth;
		myLightning = aLightning;
		myPerspectiveCorrectTexturePoint = new Point2D( aTexturePoint.x * myInverseDepth, aTexturePoint.y * myInverseDepth);
	}
	
	public Point2D getPoint(){
		return myPoint;
	}
	
	public Point2D getTexturePoint(){
		return myTexturePoint;
	}
	
	public float getInverseDepth(){
		return myInverseDepth;
	}
	
	public float getLightning(){
		return myLightning;
	}
	
  public Point2D getPerspectiveCorrectTexturePoint() {
    return myPerspectiveCorrectTexturePoint;
  }

  public static Vertex2D getInstance(Point2D aPoint, Point2D aTexturePoint, float aDepth, float aLightning){
    Vertex2D result;
    if (countFree == 0) {
      result = new Vertex2D();
    } else {
      result = STACK[--countFree];
    }
    
    result.myPoint = aPoint;
    result.myTexturePoint = aTexturePoint;
//    result.myDepth = aDepth;
//    result.myInverseDepth = 1 / aDepth;
    result.myInverseDepth = aDepth;
    result.myLightning = aLightning;
    result.myPerspectiveCorrectTexturePoint = new Point2D( aTexturePoint.x * result.myInverseDepth, aTexturePoint.y * result.myInverseDepth);
    return result;
  }

  public static void freeInstance(Vertex2D aVertex) {
    if (countFree < POOL_SIZE) {
      STACK[countFree++] = aVertex;
    }
  }
  
  public String toString(){
    return myPoint.toString();
  }

}

