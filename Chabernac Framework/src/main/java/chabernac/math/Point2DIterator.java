package chabernac.math;

import java.awt.geom.Point2D;
import java.util.Iterator;

public class Point2DIterator implements Iterator
{
	private Point2D.Double myStartPoint = null;
	private Point2D.Double myEndPoint = null;
	private Point2D.Double myCurrentPoint = null;
	private double myDistance;
	double a;
	double b;
	int time = 0;
	private boolean lastPoint = false;
	private boolean stop = false;

	public Point2DIterator(Point2D.Double startPoint, Point2D.Double endPoint, double distance){
		this.myStartPoint = startPoint;
		this.myEndPoint = endPoint;
		this.myDistance = distance;
		myCurrentPoint = new Point2D.Double();
		calculateAB();
	}

	private void calculateAB(){
		double m = (myEndPoint.getY() - myStartPoint.getY())/(myEndPoint.getX() - myStartPoint.getX());
		if(myEndPoint.getY() == myStartPoint.getY()){
			b = myDistance;
			a = 0;
		} else if(myEndPoint.getX() == myStartPoint.getX()){
			b = 0;
			a = myDistance;
		} else {
		b = Math.abs(Math.sqrt(Math.pow(myDistance,2) / (1 + Math.pow(m,2))));
		a = Math.abs(b * m);
		}
		if(myEndPoint.getY() < myStartPoint.getY()){a = -Math.abs(a);}
		if(myEndPoint.getX() < myStartPoint.getX()){b = -Math.abs(b);}
	}

	public boolean hasNext(){
		myCurrentPoint.setLocation(b * time + myStartPoint.getX(), a * time + myStartPoint.getY());
		time++;
		if(stop){return false;}
		if(lastPoint){
			stop = true;
			return true;
		}
		if(time > 2 && (Math.abs(myCurrentPoint.getX() - myEndPoint.getX()) <= Math.abs(b) && Math.abs(myCurrentPoint.getY() - myEndPoint.getY()) <= Math.abs(a))){
			lastPoint = true;
			return true;
		}
		return true;
	}

	public Object next(){
		if(stop){
			return myEndPoint;
		} else {
			return myCurrentPoint;
		}
	}

	public void remove(){}
}