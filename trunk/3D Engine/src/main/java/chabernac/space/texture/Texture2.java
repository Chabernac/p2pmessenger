/*
 * Created on 13-aug-2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;

import chabernac.space.CoordinateSystem;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;

public class Texture2 {
	private BufferedImage myImage = null;
	private CoordinateSystem mySystem = null;
	

	public Texture2(Point3D anOrigin, GVector anXUnit, GVector anYUnit, String aTexture) throws IOException{
		myImage = TextureFactory.getTexture(aTexture);
		mySystem = new CoordinateSystem(anOrigin, anXUnit, anYUnit);
	}
	
	public Point2D getTextureCoordinate(Point3D aPoint){
		Point3D thePoint = mySystem.transform(aPoint);
		return new Point2D(thePoint.x, thePoint.y);
		
	}
	
	public Point2D getTextureCoordinate(Point2D aPoint){
		double x = aPoint.x;
		double y = aPoint.y;
		
		int theWidth = myImage.getWidth();
		int theHeight = myImage.getHeight();
		
		while(x < 0) x += theWidth;
		while(x > theWidth) x -= theWidth;
		while(y < 0) y += theHeight;
		while(y > theHeight) y -= theHeight;
		
		return new Point2D(x, y);
	}
	
	public int getColor(Point2D aPoint){
		aPoint = getTextureCoordinate(aPoint);
		return myImage.getRGB((int)aPoint.x, (int)aPoint.y);
	}
	

}
