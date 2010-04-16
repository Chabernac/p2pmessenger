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
import chabernac.space.Vector2D;
import chabernac.space.Vertex;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.PolarPoint3D;
import chabernac.utils.TimeTracker;

public class Texture2 {

	private static Logger LOGGER = Logger.getLogger(Texture2.class);

	private TextureImage myImage = null;
	private CoordinateSystem mySystem = null;
	private boolean isSpherical = false;
	private double mySphereRadius = 200;

	public Texture2(Point3D anOrigin, GVector anXUnit, GVector anYUnit, TextureImage anImage){
		myImage = anImage;
		mySystem = new CoordinateSystem(anOrigin, anXUnit, anYUnit);
	}

	public Texture2(Point3D anOrigin, GVector anXUnit, GVector anYUnit, String aTexture, boolean isTransparent) throws IOException{
		myImage = TextureFactory.getTexture(aTexture, isTransparent);
		mySystem = new CoordinateSystem(anOrigin, anXUnit, anYUnit);
	}


	public Point2D getTextureCoordinate(Vertex aVertex){
		if(isSpherical){
			PolarPoint3D thePoint = new PolarPoint3D(new Point3D(aVertex.normal));

			if(thePoint.getAlpha() <= 0){
				System.out.println("Alpha: " + thePoint.getAlpha());
			}
			double u = thePoint.getAlpha() / (2 * Math.PI) ;
			double v = 0.5 - thePoint.getBeta() / Math.PI;
			return new Point2D(u * myImage.width, v * myImage.height);
		} else {
			Point3D thePoint = mySystem.getTransformator().transform(aVertex.myPoint);
			return new Point2D(thePoint.x, thePoint.y);
		}
	}

	public static Vector2D distance(Texture2 aTexture, Point2D ap1, Point2D ap2){
		double width = ap2.x - ap1.x;
		double height = ap2.y - ap1.y;
		if(aTexture != null && aTexture.isSpherical && Math.abs(width) > aTexture.myImage.halfWidth){
			if(width < 0){
				width += aTexture.myImage.width;
			} else {
				width -= aTexture.myImage.width;
			}
		}
		return Vector2D.getInstance(width, height);
		//return new Vector2D(width, height);
	}

	public int getColor(int x, int y){
		return myImage.getColorAt(x, y);
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

	public boolean isSpherical() {
		return isSpherical;
	}

	public void setSpherical(boolean isSpherical) {
		this.isSpherical = isSpherical;
	}

	public double getSphereRadius() {
		return mySphereRadius;
	}

	public void setSphereRadius(double anSphereRadius) {
		mySphereRadius = anSphereRadius;
	}

	public TextureImage getImage(){
		return myImage;
	}



}
