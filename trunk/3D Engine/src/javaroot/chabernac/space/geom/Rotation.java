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
	public double myRoll;
	public double myPitch;
	public double myYaw;
	
	public double myPitchSin;
	public double myPitchCos;
	public double myYawSin;
	public double myYawCos;
	public double myRollSin;
	public double myRollCos;
	
	public Rotation(){
		this(0F, 0F, 0F);
	} 
	
	public Rotation(double aRoll, double aPitch, double aYaw){
		myRoll = aRoll;
		myPitch = aPitch; 
		myYaw = aYaw;
		preCalculate();
	}
	
	public void preCalculate(){
	  myPitchSin = (double)Math.sin(myPitch);
	  myPitchCos = (double)Math.cos(myPitch);
	  myYawSin   = (double)Math.sin(myYaw);
	  myYawCos   = (double)Math.cos(myYaw);
	  myRollSin  = (double)Math.sin(myRoll);
	  myRollCos  = (double)Math.cos(myRoll);
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
	
	public void setPitch(double aPitch){
		myPitch = aPitch;
		preCalculate();
	}
	
	public double getPitch(){
		return myPitch;
	}
	
	public void setYaw(double aYaw) {
		myYaw = aYaw;
		preCalculate();
	}
	
	public double getYaw(){
		return myYaw;
	}
	
	public void setRoll(double aRoll){
		myRoll = aRoll;
		preCalculate();
	}
	
	public double getRoll(){
		return myRoll;
	}
}
