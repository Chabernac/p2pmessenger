package chabernac.space;

import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;

//import chabernac.utils.Debug;

public class OldCamera{
  public Point3D myLocation = null;
  public Rotation myRotation = null;
  
  /*
  public float myRotation.myPitch;
  public float myYaw;
  public float myRoll;

  public float myRotation.myPitchSin;
  public float myRotation.myPitchCos;
  public float myYawSin;
  public float myYawCos;
  public float myRollSin;
  public float myRollCos;
  */
  
  public float mySpeed;

  public OldCamera(Point3D aLocation, Rotation aRotation, float aSpeed){
    myLocation = aLocation;
    myRotation = aRotation;
    mySpeed = aSpeed;
  }

  /*
  public Point3D getLocation(){ return myLocation; }
  public double getPitch(){ return myRotation.myPitch; }
  public double getYaw(){ return myYaw; }
  public double getRoll(){ return myRoll; }

  public void setLocation(Point3D aLocation){ myLocation = aLocation; }
  public void setPitch(double pitch){ myRotation.myPitch = pitch; }
  public void setYaw(double yaw){ myYaw = yaw; }
  public void setRoll(double roll){ myRoll = roll; }
  */

  public Point3D world2Cam(Point3D aPoint){
  	
	Point3D thePoint = new Point3D(aPoint.x - myLocation.x,
								   aPoint.y - myLocation.y,
								   aPoint.z - myLocation.z);
	
	Point3D thePoint2 = new Point3D((float)(thePoint.z * myRotation.myYawSin + thePoint.x * myRotation.myYawCos),
									thePoint.y,
									(float)(thePoint.z * myRotation.myYawCos - thePoint.x * myRotation.myYawSin));

	thePoint  = new Point3D(thePoint2.x,
						  (float)(thePoint2.y * myRotation.myPitchCos - thePoint2.z * myRotation.myPitchSin),
						  (float)(thePoint2.y * myRotation.myPitchSin + thePoint2.z * myRotation.myPitchCos));

	return new Point3D((float)(thePoint.y * myRotation.myRollSin + thePoint.x * myRotation.myRollCos),
					   (float)(thePoint.y * myRotation.myRollCos - thePoint.x * myRotation.myRollSin),
						thePoint.z);
  }

  public GVector world2Cam(GVector aVector){
	GVector theVector = new GVector((float)(aVector.z * myRotation.myYawSin + aVector.x * myRotation.myYawCos),
									 aVector.y,
									(float)(aVector.z * myRotation.myYawCos - aVector.x * myRotation.myYawSin));

	GVector theVector2 = new GVector( theVector.x,
									  (float)(theVector.y * myRotation.myPitchCos - theVector.z * myRotation.myPitchSin),
									  (float)(theVector.y * myRotation.myPitchSin + theVector.z * myRotation.myPitchCos));

	theVector =  new GVector((float)(theVector2.y * myRotation.myRollSin + theVector2.x * myRotation.myRollCos),
					   (float)(theVector2.y * myRotation.myRollCos - theVector2.x * myRotation.myRollSin),
					   theVector2.z);
	//Debug.log(GeomFunctions.class,"Vector: " + aVector.toString() + " rotated: " + theVector.toString());
	return theVector;
  }

  public GVector cam2World(GVector aVector){
	  GVector theVector = new GVector((float)(- aVector.z * myRotation.myYawSin + aVector.x * myRotation.myYawCos),
									   aVector.y,
									  (float)(aVector.z * myRotation.myYawCos + aVector.x * myRotation.myYawSin));

	  GVector theVector2 = new GVector( theVector.x,
										(float)(theVector.y * myRotation.myPitchCos + theVector.z * myRotation.myPitchSin),
										(float)(- theVector.y * myRotation.myPitchSin + theVector.z * myRotation.myPitchCos));

	  theVector =  new GVector((float)(- theVector2.y * myRotation.myRollSin + theVector2.x * myRotation.myRollCos),
						 (float)(theVector2.y * myRotation.myRollCos + theVector2.x * myRotation.myRollSin),
						 theVector2.z);
	  //Debug.log(GeomFunctions.class,"Vector: " + aVector.toString() + " rotated: " + theVector.toString());
	  return theVector;
  }
  
  /*
  public Rotation cam2World(Rotation aCamRotation){

  	float theCamRollCos = Math.cos(aCamRotation.myRoll);
	float theCamRollSin = Math.sin(aCamRotation.myRoll);
	float theCamPitchCos = Math.cos(aCamRotation.myRotation.myPitch);
	float theCamPitchSin = Math.sin(aCamRotation.myRotation.myPitch);
	float theCamYawCos = Math.cos(aCamRotation.myYaw);
	float theCamYawSin = Math.sin(aCamRotation.myYaw);

  	  	
  	Rotation theRotation = new Rotation();
	theRotation.myYaw   = aCamRotation.myYaw   * myRotation.myRollCos  * myRotation.myPitchCos + aCamRotation.myPitch * myRotation.myRollSin + aCamRotation.myRoll * myRotation.myPitchSin;
	theRotation.myPitch = aCamRotation.myPitch * myRotation.myRollCos  * myRotation.myYawCos   + aCamRotation.myYaw   * myRotation.myRollSin + aCamRotation.myRoll * myRotation.myYawSin;
	theRotation.myRoll  = aCamRotation.myRoll  * myRotation.myPitchCos * myRotation.myYawCos   + aCamRotation.myPitch * myRotation.myYawSin  + aCamRotation.myYaw  * myRotation.myPitchSin;
	return aCamRotation;
  }
  
  public void camRotate(Rotation aRotation){
	Rotation theRotation = cam2World(aRotation);
	myYaw += theRotation.myYaw;
	myRotation.myPitch += theRotation.myPitch;
	myRoll += theRotation.myRoll;
  }
  */
 
  public void move(GVector aCamVector){
    if(Math.abs(mySpeed) > 5) aCamVector.z += mySpeed;
    myLocation.add(cam2World(aCamVector));
  }
  
  /*
   * method to translate this camera according to the params of the given camera
   */
  public void translate(OldCamera aCamera){
  	//GVector theVector1 = new GVector(aCamera.myLocation);
	//GVector theVector2 = cam2World(theVector1);
	//Debug.log(this,"Vector1: " + theVector1.toString() +  ", Vector2: " + theVector2.toString());	
	myLocation.add(cam2World(new GVector(aCamera.myLocation)));
	myRotation.rotate(aCamera.myRotation);
  }
   
  public void add(OldCamera aOldCamera){
  	myLocation.add(aOldCamera.myLocation);
	myRotation.add(aOldCamera.myRotation);
  }
  
  
  public void strafeLeft(float aDistance) { move(new GVector(-aDistance,0,0)); }  
  public void strafeRight(float aDistance){ move(new GVector(aDistance,0,0));  }  
  public void strafeUp(float aDistance)   { move(new GVector(0,aDistance,0));  }  
  public void strafeDown(float aDistance) { move(new GVector(0,-aDistance,0)); }  
  public void forward(float aDistance)    { move(new GVector(0,0,aDistance)); }  
  public void backward(float aDistance)   { move(new GVector(0,0,-aDistance)); }  
  public void left(float anAngle)         { myRotation.myPitch += anAngle; }  
  public void right(float anAngle)        { myRotation.myPitch -= anAngle; }  
  public void up(float anAngle)           { myRotation.myYaw   += anAngle; }  
  public void down(float anAngle)         { myRotation.myYaw   -= anAngle; }  
  public void rollLeft(float anAngle)     { myRotation.myRoll  += anAngle; }  
  public void rollRight(float anAngle)    { myRotation.myRoll  -= anAngle; }  
  
  public void accelerate(float anAcceleration){
    mySpeed += anAcceleration;
    move(new GVector(0,0,0));
  }
  
  //override to modify parameters of translation OldCamera.
  public void nextTranslation(){}
}