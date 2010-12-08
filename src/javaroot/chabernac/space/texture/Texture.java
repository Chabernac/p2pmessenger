package chabernac.space.texture;



import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import chabernac.space.Camera;
import chabernac.space.Vector2D;
import chabernac.space.geom.GVector;
import chabernac.space.geom.GeomFunctions;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;
import chabernac.utils.Debug;

public class Texture {
	private Point3D myOrigin = null;
	private Point3D myXUnit = null;
	private Point3D myYUnit = null;
	
	private GVector myOriginVector = null;
	private GVector myMVector = null;
	private GVector myNVector = null;
	private GVector myAVector = null;
	private GVector myBVector = null;
	private GVector myCVector = null;
	
	public GVector myCamOriginVector = null;
	public GVector myCamMVector = null;
	public GVector myCamNVector = null;
	public GVector myCamAVector = null;
	public GVector myCamBVector = null;
	public GVector myCamCVector = null;
	
	public Point3D myCamOrigin = null;
	private Point3D myCamXUnit = null;
	private Point3D myCamYUnit = null;
	
	private Point2D myScreenOrigin = null;
	private Point2D myScreenXUnit = null;
	private Point2D myScreenYUnit = null;
	
	private Vector2D myXVector = null;
	private Vector2D myYVector = null;
	
	private BufferedImage myImage = null;
	private int imageWidth = 0;
	private int imageHeight = 0;
	
	private GVector myTestVector = new GVector(100,100,-100);
	public GVector myCamTestVector = null;
	
	public Texture(Point3D aOrigin, Point3D aXUnit, Point3D aYUnit, BufferedImage anImage){
		myOrigin = aOrigin;
		myXUnit = aXUnit;
		myYUnit = aYUnit;
		myImage = anImage;
		try{
			myImage = ImageIO.read(new File("E:\\Projects\\JAVA\\3D Engine\\textures\\metal005.jpg"));
			imageWidth = myImage.getWidth();
			imageHeight = myImage.getHeight();
		}catch(IOException e){
			Debug.log(this,"could not read image", e);
		}
		calculateMagicVectors();
	}
	
	private void calculateMagicVectors(){
		myOriginVector = new GVector(myOrigin);
		myMVector = new GVector(myOrigin, myXUnit);
		myMVector.normalize();
		myMVector.multiply(imageWidth);
		myNVector = new GVector(myOrigin, myYUnit);
		myNVector.normalize();
		myNVector.multiply(imageHeight);
		myAVector = myOriginVector.produkt(myNVector);
		myBVector = myMVector.produkt(myOriginVector);
		myCVector = myNVector.produkt(myMVector);
	}
	
	public void world2Cam(Camera aCamera){
		myCamOrigin = aCamera.world2Cam(myOrigin);
//		myCamXUnit = aCamera.world2Cam(myXUnit);
//		myCamYUnit = aCamera.world2Cam(myYUnit);
		
		GVector theOriginVector = new GVector(myCamOrigin);
		
//		myCamOriginVector = aCamera.world2Cam(myOriginVector);
		myCamMVector = aCamera.world2Cam(myMVector);
		myCamNVector = aCamera.world2Cam(myNVector);
		
		myCamAVector = theOriginVector.produkt(myCamNVector);
		myCamBVector = theOriginVector.produkt(myCamMVector);
		myCamCVector = myCamNVector.produkt(myCamMVector);
		
//		myCamAVector.normalize();
//		myCamBVector.normalize();
//		myCamCVector.normalize();
		

		
//		myCamAVector = aCamera.world2Cam(myAVector);
//		myCamBVector = aCamera.world2Cam(myBVector);
//		myCamCVector = aCamera.world2Cam(myCVector);
		
	}
	
	public int getColorAtScreenLocation(int x, int y, double z){
		GVector theSVector = new GVector(x - 400, y - 300, z + 700);
		theSVector.normalize();
		double a = theSVector.dotProdukt(myCamAVector);
		double b = theSVector.dotProdukt(myCamBVector);
		double c = theSVector.dotProdukt(myCamCVector);
		int u = (int)(imageWidth * a / c);
		int  v = (int)(imageHeight * b / c);
		if(u < 0 || u >= imageWidth || v < 0 || v >= imageHeight) {
			//Debug.log(this,"Trying to retrieve invalid pixel at: " + u + ", " + v);
			return Color.black.getRGB();
		}
		return myImage.getRGB(u, v);
	}
	
//	public void cam2screen(Point3D anEyePoint){
//		myScreenOrigin = GeomFunctions.cam2Screen(myCamOrigin, anEyePoint);
//		myScreenXUnit = GeomFunctions.cam2Screen(myCamXUnit, anEyePoint);
//		myScreenYUnit = GeomFunctions.cam2Screen(myCamYUnit, anEyePoint);
//		myXVector = new Vector2D(myScreenOrigin, myScreenXUnit);
//		myYVector = new Vector2D(myScreenOrigin, myScreenYUnit);
//	}
//	
//	public int getColorAtScreenLocation(int x, int y){
//		Vector2D P = new Vector2D(myScreenOrigin, new Point2D(x, y));
//		double u = P.x;
//		double v = P.y;
//		
//		if((myXVector.x == 0 && myYVector.x == 0) || (myXVector.y == 0 && myYVector.y ==0)) return Color.black.getRGB();
//		
//		double a,b;
//		
//		if(myXVector.x == 0){
//			b = u / myYVector.x;
//			a = (v - b * myYVector.y) / myXVector.y;
//		} else if(myYVector.x == 0){
//			a = u / myXVector.x;
//			b = (v - a * myXVector.y ) / myYVector.y;
//		} else {
//		    a = (v * myYVector.x - u * myYVector.y) / (myXVector.y * myYVector.x - myXVector.x * myYVector.y);
//		    b = (u - a * myXVector.x) / myYVector.x;
//		}
//		
//		a = Math.floor(a);
//		b = Math.floor(b);
//		
//		if(a < 0){
//			//System.out.println("a: " + a);
//			a = imageWidth - (Math.abs(a) % imageWidth) - 1;
//			//if( a == imageWidth) System.out.println("Impossible happened");
//		} else {
//			a = a  % imageWidth;
//		}
//		
//		if(b < 0){
//			b = imageHeight - (Math.abs(b) % imageHeight) - 1;
//		} else {
//			b = b % imageHeight;
//		}
//		
//		if(a < 0 || a >= imageWidth || b < 0 || b >= imageHeight){
//			System.out.println("Trying to access invalide coordinate at: " + a + ", " + b);
//		}
//		
//		return myImage.getRGB((int)a, (int) b);
//	}

}
