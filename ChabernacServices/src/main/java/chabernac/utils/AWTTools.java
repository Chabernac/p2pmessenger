package chabernac.utils;

import java.awt.Rectangle;
import java.io.File;

public class AWTTools {
  public static void normalizeRectangle(Rectangle aRect){
    if(aRect.width < 0){
      aRect.width *= -1;
      aRect.x -= aRect.width;
    }
    
    if(aRect.height < 0){
      aRect.height *= -1;
      aRect.y -= aRect.height;
    }
  }
      
  
  public static Rectangle createNormalizedRectangle(int x, int y, int width, int height){
    Rectangle theRect = new Rectangle(x,y,width,height);
    normalizeRectangle(theRect);
    return theRect;
  }
  
	public static boolean isImage(File aFile){
		String theFileName = aFile.getName();
		String theExtension = theFileName.substring(theFileName.indexOf('.') + 1, theFileName.length());
		if(theExtension.equalsIgnoreCase("JPEG")) return true;
		if(theExtension.equalsIgnoreCase("JPG")) return true;
		if(theExtension.equalsIgnoreCase("GIF")) return true;
		if(theExtension.equalsIgnoreCase("BMP")) return true;
		if(theExtension.equalsIgnoreCase("PNG")) return true;
		if(theExtension.equalsIgnoreCase("TIF")) return true;
		return false;
	}
}
