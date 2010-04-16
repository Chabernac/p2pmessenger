package chabernac.geom;

public class Location
{
	private int x;
	private int y;

public Location(int x, int y)
{
this.x = x;
this.y = y;
}
public void setX(int x){this.x = x;}
public void setY(int y){this.y = y;}
public int getX(){return x;}
public int getY(){return y;}
public void setLocation(Location aLocation){
	x = aLocation.getX();
	y = aLocation.getY();
}

public String toString(){
	return "X: " + x + " Y: " + y;
}

public boolean equals(Location aLocation){
	if(x == aLocation.getX() && y == aLocation.getY()){
		return true;
	} else {
		return false;
	}
}

}