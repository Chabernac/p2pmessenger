package chabernac.space;

import org.apache.log4j.Logger;

import chabernac.math.MatrixException;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;


public class PathShape extends MoveableShape{
  private static final Logger LOGGER = Logger.getLogger(PathShape.class);

  private Point3D[] myPath;
  private float mySpeed = 1;
  private int myCurrentNode = 0;
  private Point3D myCurrentLocation = null;
  private GVector myCurrentTranslation = null;

  public PathShape(int nrOfPolygons, Point3D[] aPath, float aSpeed) throws MatrixException{
    this(nrOfPolygons, false, aPath, aSpeed);
  }

  public PathShape(int nrOfPolygons, boolean isRoom, Point3D[] aPath, float aSpeed) throws MatrixException{
    super(nrOfPolygons, isRoom);
    myPath = aPath;
    mySpeed = aSpeed;
    myTranslationCameras = new Camera[1];
    myTranslationCameras[0] = new Camera(new Point3D(0,0,0), new Rotation(0F, 0F, 0F), 1F);
  }

  public void nextTranslation(){
    //Debug.log(this,"Calculating next translation");
    try{
      if(myCurrentTranslation == null){ detCurrentTranslation(); }
      if(myCurrentLocation == null){ myCurrentLocation = (Point3D)myPath[0].clone(); }
      myCurrentLocation.add(myCurrentTranslation);
      int ii = (myCurrentNode + 1) % myPath.length;
      GVector theVector = myPath[ii].minus(myCurrentLocation);
      //Debug.log(this,"Current location: " + myCurrentLocation + " dot produkt: " + theVector.dotProdukt(myCurrentTranslation));
      if(theVector.dotProdukt(myCurrentTranslation) < 0 ){
        myCurrentNode = (myCurrentNode + 1) % myPath.length;
        detCurrentTranslation();
      }
    }catch(MatrixException e){ 
      LOGGER.error("Nexttranslation failed", e);
    }
  }

  private void detCurrentTranslation() throws MatrixException{
    int ii = (myCurrentNode + 1) % myPath.length;
    myCurrentTranslation = myPath[ii].minus(myPath[myCurrentNode]);
    //Debug.log(this,myPath[ii] + " minus " + myPath[myCurrentNode] + " = " + myCurrentTranslation );
    myCurrentTranslation.normalize();
    myCurrentTranslation.multiply(mySpeed);
    //Debug.log(this,"Current translation: " + myCurrentTranslation);
    myTranslationCameras[0].setLocation(new Point3D(myCurrentTranslation.x, myCurrentTranslation.y, myCurrentTranslation.z));
  }


}