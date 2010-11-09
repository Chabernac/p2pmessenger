package chabernac.easteregg;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JFrame;

public class Mystify extends DefaultEasterEggPaintable{
	private Rectangle myBounds = null;
	private Random myRandom = null;
	private Frame[] myFrames = null;
	private int myNrOfFrames = 4;
	private int myNrOfCorners = 4;
	private boolean isFill = true;

	public Mystify(JFrame aRootFrame, Rectangle aBounds, int nrOfFrames){
		super(aRootFrame);
		myBounds = aBounds;
		myRandom = new Random();
	}
	
	private void init(){
		myFrames= new Frame[myNrOfFrames];
		for(int i=0;i<myFrames.length;i++){
			myFrames[i] = new Frame(myNrOfCorners);
		}
	}
	

	public int getNrOfFrames() {
		return myNrOfFrames;
	}

	public void setNrOfFrames(int myNrOfFrames) {
		this.myNrOfFrames = myNrOfFrames;
	}

	public int getNrOfCorners() {
		return myNrOfCorners;
	}

	public void setNrOfCorners(int myNrOfCorners) {
		this.myNrOfCorners = myNrOfCorners;
	}

	public void paint(Graphics g, Rectangle aRect, BufferedImage anImage){
		myBounds  = aRect;
		Graphics2D theG = (Graphics2D)g;
//		g.setColor(Color.white);
		//theG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0F));
    if(!isPaintBackground()){
  		theG.clearRect(0, 0, myBounds.width, myBounds.height);
  		theG.setColor(Color.black); 
    } else {
      g.drawImage(anImage, 0, 0, null);
    }

		for(int i=0;i<myFrames.length;i++){
			myFrames[i].paint(g, aRect);
			myFrames[i].move();
		}
	}

	private class Frame{
		private Point[] myPoints = null;
		private Color myColor;

		public Frame(int aNrOfPoints){
			int r = (int)Math.abs(myRandom.nextLong() % 255);
			int g = (int)Math.abs(myRandom.nextLong() % 255);
			int b = (int)Math.abs(myRandom.nextLong() % 255);

			myColor = new Color(r,g,b);
			myPoints = new Point[aNrOfPoints];
			for(int i=0;i<myPoints.length;i++){
				Point2D.Double theDirection = new Point2D.Double( (myRandom.nextFloat() - 0.5) * 10, (myRandom.nextFloat() - 0.5) * 10); 
				myPoints[i] = new Point(Math.abs(myRandom.nextLong() % myBounds.width), Math.abs(myRandom.nextLong() % myBounds.height), theDirection);
			}
		}

		public void move(){
			for(int i=0;i<myPoints.length;i++){
				myPoints[i].move();
			}
		}

		public void paint(Graphics g, Rectangle aRect){
			Polygon thePolygon = new Polygon();
			for(int i=0;i<myPoints.length;i++){
//				int j = (i + 1) % myPoints.length;
//g.drawLine((int)myPoints[i].x, (int)myPoints[i].y, (int)myPoints[j].x, (int)myPoints[j].y);

				thePolygon.addPoint((int)myPoints[i].x, (int)myPoints[i].y);
			}
			g.setColor(myColor);
			if(isFill){
				g.fillPolygon(thePolygon);
			} else {
				g.drawPolygon(thePolygon);
			}
		}
	}

	private class Point extends java.awt.geom.Point2D.Double{
		private Point2D.Double myDirection = null;

		public Point(double x, double y, Point2D.Double aDirection){
			super(x, y);
			myDirection = aDirection;
		}

		public void move(){
			x += myDirection.x;
			y += myDirection.y;
			if(x  < 0){
				x = 0;
				myDirection.x *= -1;
			}
			if(x > myBounds.width){
				x = myBounds.width;
				myDirection.x *= -1;
			}
			if(y  < 0){
				y = 0;
				myDirection.y *= -1;
			}
			if(y > myBounds.height){
				y = myBounds.height;
				myDirection.y *= -1;
			}
		}
	}
	
	
	
	public boolean isFill() {
		return isFill;
	}

	public void setFill(boolean isFill) {
		this.isFill = isFill;
	}

	public void setParameter(Object aParameter){
		super.setParameter(aParameter);
		if(aParameter instanceof String){
			String theParam = (String)aParameter;
			StringTokenizer theTokenizer = new StringTokenizer(theParam, "=");
			if(theTokenizer.countTokens() == 2){
				String theKey = theTokenizer.nextToken();
				String theValue = theTokenizer.nextToken();
				if(theKey.equals("frames")){
					setNrOfFrames(Integer.parseInt(theValue));
				}
				if(theKey.equals("corners")){
					setNrOfCorners(Integer.parseInt(theValue));
				}
				if(theKey.equals("fill")){
					setFill(new Boolean(theValue).booleanValue());
				}
			}
		}
	}
	
	public void start(){
		init();
		super.start();
	}
}
