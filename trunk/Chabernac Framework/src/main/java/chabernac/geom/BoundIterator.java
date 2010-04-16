package chabernac.geom;

import java.util.*;
import chabernac.utils.*;

public class BoundIterator implements Iterator{

	private Bound myStartBound = null;
	private Bound myEndBound = null;
	private Bound myCurrentBound = null;
	private float myXStep;
	private float myYStep;
	private float myWidthStep;
	private float myHeightStep;
	private float myX;
	private float myY;
	private float myWidth;
	private float myHeight;

	private int mySteps;

	public BoundIterator(Bound aStartBound, Bound aEndBound, int aSteps){
		Debug.log(this,"Startbound: " + aStartBound);
		Debug.log(this,"Endbound: " + aEndBound);
		myStartBound = aStartBound;
		myEndBound = aEndBound;
		mySteps = aSteps;
		initialize();
		setupSteps();
	}

	private void initialize(){
		myCurrentBound = new Bound(myStartBound);
		myX = myCurrentBound.getX();
		myY = myCurrentBound.getY();
		myWidth = myCurrentBound.getWidth();
		myHeight = myCurrentBound.getHeight();
	}

	private void setupSteps(){
		myXStep = ((float)(myEndBound.getX() - myStartBound.getX())) / mySteps;
		myYStep = ((float)(myEndBound.getY() - myStartBound.getY())) / mySteps;
		myWidthStep = ((float)(myEndBound.getWidth() - myStartBound.getWidth())) / mySteps;
		myHeightStep = ((float)(myEndBound.getHeight() - myStartBound.getHeight())) / mySteps;
		Debug.log(this,"XStep: " + myXStep);
		Debug.log(this,"YStep: " + myYStep);
	}

	public boolean hasNext(){
		return !myCurrentBound.equals(myEndBound);
	}

	public Object next(){
		myX += myXStep;
		myY += myYStep;
		myWidth += myWidthStep;
		myHeight += myHeightStep;
		Bound theBound = new Bound(Math.round(myX),Math.round(myY),Math.round(myWidth),Math.round(myHeight));
		myCurrentBound = theBound;
		return myCurrentBound;
	}

	public void remove(){
	}
}