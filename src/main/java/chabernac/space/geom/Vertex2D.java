package chabernac.space.geom;


public class Vertex2D {
  private static final int POOL_SIZE = 10;
  private static final Vertex2D[] STACK = new Vertex2D[POOL_SIZE];
  private static int countFree;
  
	private Point2D myPoint = null;
	private Point2D myTexturePoint = null;
	private double myDepth, myInverseDepth;
	private double myLightning;
	
  private Vertex2D(){
    
  }
  
	public Vertex2D(Point2D aPoint, double aDepth, double aLightning){
		this(aPoint, null, aDepth, aLightning);
	}
	
	public Vertex2D(Point2D aPoint, Point2D aTexturePoint, double aDepth, double aLightning){
		myPoint = aPoint;
		myTexturePoint = aTexturePoint;
		myDepth = aDepth;
		myInverseDepth = 1D / myDepth;
		myLightning = aLightning;
	}
	
	public Point2D getPoint(){
		return myPoint;
	}
	
	public Point2D getTexturePoint(){
		return myTexturePoint;
	}
	
	public double getDepth(){
		return myDepth;
	}
	
	public double getInverseDepth(){
		return myInverseDepth;
	}
	
	public double getLightning(){
		return myLightning;
	}
  
  public static Vertex2D getInstance(Point2D aPoint, Point2D aTexturePoint, double aDepth, double aLightning){
    Vertex2D result;
    if (countFree == 0) {
      result = new Vertex2D();
    } else {
      result = STACK[--countFree];
    }
    
    result.myPoint = aPoint;
    result.myTexturePoint = aTexturePoint;
    result.myDepth = aDepth;
    result.myInverseDepth = 1 / aDepth;
    result.myLightning = aLightning;
    return result;
  }

  public static void freeInstance(Vertex2D aVertex) {
    if (countFree < POOL_SIZE) {
      STACK[countFree++] = aVertex;
    }
  }

}

