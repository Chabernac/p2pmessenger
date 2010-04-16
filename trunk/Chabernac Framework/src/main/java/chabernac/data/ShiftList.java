package chabernac.data;


import java.util.Random;

import chabernac.geom.Location;
import chabernac.utils.Debug;

public class ShiftList{
	private Object myShiftList[][] = null;
	private Location myNullLocation = null;
	private int myWidth;
	private int myHeight;
	private ShiftListListener myListener = null;

	public ShiftList(Object aShiftList[][], Location aNullLocation){
		myShiftList = aShiftList;
		myNullLocation = aNullLocation;
		myWidth = aShiftList.length;
		myHeight = aShiftList[0].length;
	}

	public void shift(Location aShiftPoint){
		if(aShiftPoint.getX() == myNullLocation.getX() && aShiftPoint.getY() != myNullLocation.getY()){
			shiftVertical(aShiftPoint);
		} else if(aShiftPoint.getY() == myNullLocation.getY() && aShiftPoint.getX() != myNullLocation.getX()){
			shiftHorizontal(aShiftPoint);
		}
	}

	public void randomize(int aShifts){
		Random theRandomGenerator = new Random();
		boolean horizontal = theRandomGenerator.nextBoolean();
		Location theLocation = null;
		for (int i=0;i<aShifts;i++){
			try{
				//Thread.sleep(200);
			}catch(Exception e){
				Debug.log(this,"Could not sleep");
			}
			if(horizontal = !horizontal){
				while((theLocation = new Location(Math.abs(theRandomGenerator.nextInt() % myWidth),myNullLocation.getY())).equals(myNullLocation)){}
				shiftHorizontal(theLocation);
			} else {
				while((theLocation = new Location(myNullLocation.getX(),Math.abs(theRandomGenerator.nextInt() % myHeight))).equals(myNullLocation)){}
				shiftVertical(theLocation);
			}
		}
	}

	public void setListener(ShiftListListener aListener){
		myListener = aListener;
	}

	private void shiftHorizontal(Location aShiftPoint){
		Location theNullLocation = myNullLocation;
		myNullLocation = aShiftPoint;

		int y = theNullLocation.getY();
		Object theNullObject = myShiftList[theNullLocation.getX()][theNullLocation.getY()];

		int shift  = 0;
		if(theNullLocation.getX() - aShiftPoint.getX() < 0){
			shift = 1;
		} else {
			shift = -1;
		}
		int i;
		for(i=theNullLocation.getX();i!=aShiftPoint.getX();i += shift){
			myShiftList[i][y] = myShiftList[i + shift][y];
			if(myListener != null){
				myListener.shiftListChanged(new Location(i + shift,y),new Location(i,y), myShiftList[i][y]);
				try{
					//Thread.sleep(100);
				}catch(Exception e){
					Debug.log(this,"Could not sleep");
				}
			}
		}
		//i += shift;
		myShiftList[i][y] = theNullObject;
		if(myListener != null){
				myListener.shiftListChanged(theNullLocation, new Location(i,y), myShiftList[i][y]);
		}
		//myNullLocation.setLocation(aShiftPoint);
	}

	private void shiftVertical(Location aShiftPoint){
		Location theNullLocation = myNullLocation;
		myNullLocation = aShiftPoint;

		int x = theNullLocation.getX();
		Object theNullObject = myShiftList[theNullLocation.getX()][theNullLocation.getY()];

		int shift  = 0;
		if(theNullLocation.getY() - aShiftPoint.getY() < 0){
			shift = 1;
		} else {
			shift = -1;
		}
		int i;
		for(i=theNullLocation.getY();i!=aShiftPoint.getY();i += shift){
			myShiftList[x][i] = myShiftList[x][i + shift];
			if(myListener != null){
					myListener.shiftListChanged(new Location(x,i + shift),new Location(x,i), myShiftList[x][i]);
					try{
						//Thread.sleep(100);
					}catch(Exception e){
						Debug.log(this,"Could not sleep");
					}
			}
		}
		//i += shift;
		myShiftList[x][i] = theNullObject;
		if(myListener != null){
			myListener.shiftListChanged(theNullLocation, new Location(x,i), myShiftList[x][i]);
		}
		//myNullLocation.setLocation(aShiftPoint);
	}



	public Object[][] getObjectList(){
		return myShiftList;
	}

	public Location getNullLocation(){
		return myNullLocation;
	}
}
