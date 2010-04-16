/**
 *
 *
 * @version v1.0.0      Apr 5, 2005
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Apr 5, 2005 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
package chabernac.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import chabernac.io.GenericResource;
import chabernac.io.iResource;
import chabernac.utils.ImageTools;

public class ImageFactory {
  private static Logger LOGGER = Logger.getLogger(ImageFactory.class);

  private static ImageLoader theLoader = new ImageLoader();
  private static Hashtable loadedImages = new Hashtable();

  private static String[] PATHS = new String[]{"images","textures"};
  private static String[] EXTENSIONS = new String[]{"jpg", "jpeg", "gif", "png"};

  public static BufferedImage loadImage(String aName, boolean isTransparent){
    try{
      if(!loadedImages.containsKey(aName)) {
        iResource theResource = searchResource(aName);
        Image theImage = theLoader.loadImage(theResource, isTransparent);
        if(theImage == null) return null;
        loadedImages.put(aName, theImage);
      }
      return (BufferedImage)loadedImages.get(aName);
    }catch(IOException e){
      LOGGER.error("Error loading image", e);
      return null; 
    }
  }

  public static BufferedImage createImage(String aText, Font aFont, int aMinimumWidth, int aMinimumHeight, Color aForeGround, Color aBackGround, boolean isTranspartent){
    String theName = aText + aFont.toString() + aMinimumWidth + aMinimumHeight  + aForeGround.toString() + aBackGround.toString() +  isTranspartent;

    if(!loadedImages.containsKey(theName)){
      BufferedImage theImage = new BufferedImage(aMinimumWidth,aMinimumHeight, BufferedImage.TYPE_INT_ARGB);
      Graphics g = theImage.getGraphics();
      g.setFont(aFont);
      
      int theTextWith = g.getFontMetrics().stringWidth(aText);
      int theTextHeight = g.getFontMetrics().getHeight();
      
      if(theTextWith > aMinimumWidth || theTextHeight > aMinimumHeight){
        theImage = new BufferedImage(Math.max(aMinimumWidth, theTextWith),Math.max(aMinimumHeight, theTextHeight), BufferedImage.TYPE_INT_ARGB);
        g = theImage.getGraphics();
      }
      
      int theWith = theImage.getWidth();
      int theHeight = theImage.getHeight();

      g.setColor(aBackGround);
      g.fillRect(0, 0, theWith, theHeight);
      g.setColor(aForeGround);
      g.setFont(aFont);

      g.drawString(aText, (theWith - theTextWith) / 2, (theTextHeight  + theHeight) / 2);

      if(isTranspartent){
        theImage = ImageTools.makeTransparent(theImage, new Color[]{aBackGround});
      }
      loadedImages.put(theName, theImage);
    }

    return (BufferedImage)loadedImages.get(theName);
  }

  private static iResource searchResource(String aName){
    for(int i=0;i<PATHS.length;i++){
      for(int j=0;j<EXTENSIONS.length;j++){
        iResource theGenericResource = new GenericResource(PATHS[i] + "/" + aName + "." + EXTENSIONS[j]);
        if(theGenericResource.exists()){
          return theGenericResource;
        }
      }
    }
    return null;
  }

}
