/*
 * Created on 13-aug-2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.texture;

import java.io.IOException;

import org.apache.log4j.Logger;

import chabernac.space.CoordinateSystem;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;
import chabernac.utils.TimeTracker;

public class Texture2 {
	
	private static Logger LOGGER = Logger.getLogger(Texture2.class);
	
	private TextureImage myImage = null;
	private CoordinateSystem mySystem = null;
	
	
	public Texture2(Point3D anOrigin, GVector anXUnit, GVector anYUnit, String aTexture, boolean isTransparent) throws IOException{
		myImage = TextureFactory.getTexture(aTexture, isTransparent);
		mySystem = new CoordinateSystem(anOrigin, anXUnit, anYUnit);
	}
	
	
	
	public Point2D getTextureCoordinate(Point3D aPoint){
		Point3D thePoint = mySystem.getTransformator().transform(aPoint);
		return new Point2D(thePoint.x, thePoint.y);
		
	}
	
	public int getColor(Point2D aPoint){
		long t = TimeTracker.start();
		int theColor = myImage.getColorAt((int)Math.floor(aPoint.x), (int)Math.floor(aPoint.y));
		t = TimeTracker.logTime("retrieving color from buffered image");
		return theColor;
	}
	
	public CoordinateSystem getSystem() {
		return mySystem;
	}
	
	
	
	
}
