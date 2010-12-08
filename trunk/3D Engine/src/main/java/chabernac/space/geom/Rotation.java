/*
 * Created on 15-dec-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.geom;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Rotation {
	public float myRoll;
	public float myPitch;
	public float myYaw;
	
	public float myPitchSin;
	public float myPitchCos;
	public float myYawSin;
	public float myYawCos;
	public float myRollSin;
	public float myRollCos;
	
	public Rotation(){
		this(0F, 0F, 0F);
	} 
	
	public Rotation(float aRoll, float aPitch, float aYaw){
		myRoll = aRoll;
		myPitch = aPitch; 
		myYaw = aYaw;
		preCalculate();
	}
	
	public void preCalculate(){
	  myPitchSin = (float)Math.sin(myPitch);
	  myPitchCos = (float)Math.cos(myPitch);
	  myYawSin   = (float)Math.sin(myYaw);
	  myYawCos   = (float)Math.cos(myYaw);
	  myRollSin  = (float)Math.sin(myRoll);
	  myRollCos  = (float)Math.cos(myRoll);
	}
	
	public void add(Rotation aRotation){
		myRoll += aRotation.myRoll;
		myPitch += aRotation.myPitch;
		myYaw += aRotation.myYaw;
		preCalculate();
	}
	
	//TODO implement code to rotate correctly form a viewers perspective	
	public void rotate(Rotation aRotation){
		add(aRotation);
	}
	
	public Object clone(){
		return new Rotation(myRoll, myPitch, myYaw);
	}
	
	public void invert(){
		myRoll *= -1;
		myPitch *= -1;
		myYaw *= -1;
		preCalculate();
	}
	
	public String toString(){
		return "<rotation pitch=" + myPitch + " yaw=" + myYaw + " roll=" + myRoll + "/>"; 
	}
	
	public void setPitch(float aPitch){
		myPitch = aPitch;
		preCalculate();
	}
	
	public float getPitch(){
		return myPitch;
	}
	
	public void setYaw(float aYaw) {
		myYaw = aYaw;
		preCalculate();
	}
	
	public float getYaw(){
		return myYaw;
	}
	
	public void setRoll(float aRoll){
		myRoll = aRoll;
		preCalculate();
	}
	
	public float getRoll(){
		return myRoll;
	}
}
