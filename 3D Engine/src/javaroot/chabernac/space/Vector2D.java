package chabernac.space;

import chabernac.space.geom.Point2D;

public class Vector2D {
	public double x,y;
	
	public Vector2D(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Vector2D(Point2D p1, Point2D p2){
		x = p2.x - p1.x;
		y = p2.y - p1.y;
	}
	
	public void invert(){
		x *= -1;
		y *= -1;
	}
	
	public void add(Vector2D aVector){
		x += aVector.x;
		y += aVector.y;
	}
		
	

}
