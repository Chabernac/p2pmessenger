package chabernac.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import chabernac.io.iResource;
import chabernac.utils.ImageTools;

public class ImageLoader {
  private static Logger LOGGER = Logger.getLogger(ImageLoader.class);
  

  public ImageLoader(){
    
  }

  public BufferedImage loadImage(iResource aResource, boolean isTransparent) throws IOException{
    BufferedImage theImage = null;

    if(aResource == null || !aResource.exists()){
      theImage = generateGrid();
    } else {
      InputStream theStream = aResource.getInputStream();
      theImage = ImageIO.read(theStream);
    }
    
    
    if(isTransparent){
      theImage = ImageTools.makeTransparent(theImage, new Color[]{Color.white});
    } 
    
    return theImage;
  }

  private static BufferedImage generateGrid(){
    BufferedImage theImage = new BufferedImage(300,300, BufferedImage.TYPE_INT_ARGB);
    int offset = 20;
    Graphics g = theImage.getGraphics();
    Color theColor = new Color(255,255,255,0);
    g.setColor(theColor);
    g.fillRect(0, 0, theImage.getWidth(), theImage.getHeight());
    g.setColor(Color.red);
    g.fillRect(0, theImage.getHeight() / 2 - 10, theImage.getWidth(), offset * 2);
    g.setColor(Color.blue);
    g.fillRect(theImage.getWidth() / 2 - 10, 0, offset * 2, theImage.getHeight());
    /*
      g.setColor(Color.green);
      g.drawRect(0, 0, theImage.getWidth(), theImage.getHeight());
      g.drawRect(1, 1, theImage.getWidth() - 2, theImage.getHeight() - 2);
     */
    return theImage;
  }



}
