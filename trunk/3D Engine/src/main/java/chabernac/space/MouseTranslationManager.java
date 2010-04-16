package chabernac.space;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import chabernac.space.geom.Point3D;



public class MouseTranslationManager extends OneLocationTranslateManager implements MouseMotionListener, MouseWheelListener{
	private int myDepth;
	private int myCurrentDepth;
	private Graphics3D myGraphics = null;

	public MouseTranslationManager(Graphics3D aGraphics, int aDepth, int aSpeed){
		super(new Point3D(0,0,aDepth), aSpeed);
		myDepth = aDepth;
		myCurrentDepth = aDepth;
		myGraphics = aGraphics;
	}


	public void mouseDragged(MouseEvent e) {

	}

	public void mouseMoved(MouseEvent e) {
		if(myGraphics != null && myGraphics.getEyePoint() != null){
			setDestination(new Point3D(e.getX() - myGraphics.getEyePoint().x, myGraphics.getEyePoint().y - e.getY(), myCurrentDepth));
		}
	}


	public void mouseWheelMoved(MouseWheelEvent e) {
		if(myGraphics != null && myGraphics.getEyePoint() != null){
			myCurrentDepth += 50 * (e.getWheelRotation() * e.getScrollAmount());
			if(myCurrentDepth < myDepth){
				myCurrentDepth = myDepth;
			}
			setDestination(new Point3D(e.getX() - myGraphics.getEyePoint().x, myGraphics.getEyePoint().y - e.getY(), myCurrentDepth));
		}
	}

}