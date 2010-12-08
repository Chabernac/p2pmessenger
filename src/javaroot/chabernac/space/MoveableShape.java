package chabernac.space;


//import chabernac.utils.Debug;

public abstract class MoveableShape extends Shape{

  public Camera[] myTranslationCameras = null;

  public MoveableShape(int nrOfPolygons){
    super(nrOfPolygons);
  }

  public MoveableShape(int nrOfPolygons, boolean isRoom){
    super(nrOfPolygons, isRoom);
  }

  public void world2Cam(Camera aCamera){
	//Debug.log(this,"world2Cam in Moveableshape: " + myTranslationCameras);
    if(myTranslationCameras != null && myTranslationCameras.length > 0){
	  //Debug.log(this,"Performing next translation");
      nextTranslation();
      for(int i=0;i<myTranslationCameras.length;i++){
        translate(myTranslationCameras[i]);
      }
    }
    super.world2Cam(aCamera);
  }

  public abstract void nextTranslation();
}