package chabernac.space.shapes;

import chabernac.space.MoveableShape;
import chabernac.space.PolygonException;
import chabernac.space.Vertex;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Polygon;



public class Cube extends MoveableShape{
  private Point3D myOrigPoint =null;
  private float myWidth;
  private float myHeight;
  private float myDepth;

  public Cube(Point3D aOrigPoint, float aWidth, float aHeight, float aDepth, boolean isRoom) throws PolygonException{
    super(6, isRoom);
    myOrigPoint = aOrigPoint;
    myWidth = aWidth;
    myHeight = aHeight;
    myDepth = aDepth;
    createPolygons();
  }

  public Cube(Point3D aOrigPoint, float aWidth, float aHeight, float aDepth) throws PolygonException{
	  this(aOrigPoint, aWidth, aHeight, aDepth, false);
  }

  public Cube(Point3D aOrigPoint, float aRibbe, boolean isRoom) throws PolygonException{
	  this(aOrigPoint, aRibbe, aRibbe, aRibbe, isRoom);
  }

  public Cube(Point3D aOrigPoint, float aRibbe) throws PolygonException{
	  this(aOrigPoint, aRibbe, false);
  }

  private void createPolygons() throws PolygonException{
    Point3D thePoint1 = myOrigPoint;
    Point3D thePoint2 = new Point3D(myOrigPoint.x + myWidth, myOrigPoint.y, myOrigPoint.z);
    Point3D thePoint3 = new Point3D(thePoint2.x, thePoint2.y + myHeight, myOrigPoint.z);
    Point3D thePoint4 = new Point3D(myOrigPoint.x, thePoint3.y, myOrigPoint.z);
    Point3D thePoint5 = new Point3D(myOrigPoint.x, myOrigPoint.y, myOrigPoint.z + myDepth);
    Point3D thePoint6 = new Point3D(thePoint2.x, myOrigPoint.y, thePoint5.z);
    Point3D thePoint7 = new Point3D(thePoint6.x , thePoint3.y, thePoint5.z);
    Point3D thePoint8 = new Point3D(thePoint5.x , thePoint3.y, thePoint5.z);

    Polygon thePolygon = null;

    thePolygon = new Polygon(4);
    thePolygon.addVertex(new Vertex(thePoint1));
    thePolygon.addVertex(new Vertex(thePoint2));
    thePolygon.addVertex(new Vertex(thePoint3));
    thePolygon.addVertex(new Vertex(thePoint4));
    //thePolygon.myColor = new Color(200,0,0);
    thePolygon.done();
    addPolygon(thePolygon);

    thePolygon = new Polygon(4);
    thePolygon.addVertex(new Vertex(thePoint1));
    thePolygon.addVertex(new Vertex(thePoint2));
    thePolygon.addVertex(new Vertex(thePoint6));
    thePolygon.addVertex(new Vertex(thePoint5));
    //thePolygon.myColor = new Color(0,200,0);
    thePolygon.done();
    addPolygon(thePolygon);


    thePolygon = new Polygon(4);
    thePolygon.addVertex(new Vertex(thePoint2));
    thePolygon.addVertex(new Vertex(thePoint3));
    thePolygon.addVertex(new Vertex(thePoint7));
    thePolygon.addVertex(new Vertex(thePoint6));
    //thePolygon.myColor = new Color(0,0,200);
    thePolygon.done();
    addPolygon(thePolygon);

    thePolygon = new Polygon(4);
    thePolygon.addVertex(new Vertex(thePoint1));
    thePolygon.addVertex(new Vertex(thePoint4));
    thePolygon.addVertex(new Vertex(thePoint8));
    thePolygon.addVertex(new Vertex(thePoint5));
    //thePolygon.myColor = new Color(200,200,0);
    thePolygon.done();
    addPolygon(thePolygon);

    thePolygon = new Polygon(4);
    thePolygon.addVertex(new Vertex(thePoint5));
    thePolygon.addVertex(new Vertex(thePoint6));
    thePolygon.addVertex(new Vertex(thePoint7));
    thePolygon.addVertex(new Vertex(thePoint8));
    //thePolygon.myColor = new Color(0,200,200);
    thePolygon.done();
    addPolygon(thePolygon);

    thePolygon = new Polygon(4);
    thePolygon.addVertex(new Vertex(thePoint4));
    thePolygon.addVertex(new Vertex(thePoint3));
    thePolygon.addVertex(new Vertex(thePoint7));
    thePolygon.addVertex(new Vertex(thePoint8));
    //thePolygon.myColor = new Color(200,0,200);
    thePolygon.done();
    addPolygon(thePolygon);

    done();
  }
  
  public void nextTranslation(){}

}