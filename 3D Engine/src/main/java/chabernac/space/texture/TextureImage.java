/*
 * Created on 18-aug-2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;

public class TextureImage{
  public BufferedImage image = null;
  public int width = 0;
  public int height = 0;
  public int halfWidth = 0;
  public int[] colors;
  private boolean isUseBilinearInterpolation = false;
  
  public TextureImage(BufferedImage anImage){
    image = anImage;
    width = image.getWidth();
    halfWidth = width / 2;
    height = image.getHeight();
    createBuffer();
  }

  private void createBuffer(){
    colors = getRGB(image);
  }

  public int getColorAt(float x, float y){
    if(x < 0 || y <0){
      return 0;
    }
//    while(x < 0) x += width;
    while(x >= width) x -= width;
//    while(y < 0) y += height;
    while(y >= height) y -= height;

    if(isUseBilinearInterpolation){
      int x1 = (int)Math.round( x );
      if(x1 >= width) x1 -= 1;
      int x2 = x1 - 1;
      if(x2 < 0 || x2 >= width) x2 = x1;
      
      int y1 = (int)Math.round( y );
      if(y1 >= height) y1 -= 1;
      int y2 = y1 - 1;
      if(y2 < 0 || y2 >= height) y2 = y1;
      
      Color theColor1 = new Color( colors[y1 * width + x1]  );
      Color theColor2 = new Color( colors[y1 * width + x2]  );
      Color theColor3 = new Color( colors[y2 * width + x1]  );
      Color theColor4 = new Color( colors[y2 * width + x2]  );
      
      int alpha = (theColor1.getAlpha() + theColor2.getAlpha() + theColor3.getAlpha() + theColor4.getAlpha()) / 4;
      int red = (theColor1.getRed() + theColor2.getRed() + theColor3.getRed() + theColor4.getRed()) / 4;
      int green = (theColor1.getGreen() + theColor2.getGreen() + theColor3.getGreen() + theColor4.getGreen()) / 4;
      int blue = (theColor1.getBlue() + theColor2.getBlue() + theColor3.getBlue() + theColor4.getBlue()) / 4;
      
      return new Color( red, green, blue, alpha ).getRGB();
    } else {
      return colors[(int)y * width + (int)x];
    }
  }
  
  public int getX(int x){
    while(x < 0) x += width;
    while(x >= width) x -= width;
    return x;
  }
  
  public int getY(int y){
    while(y < 0) y += height;
    while(y >= height) y -= height;
    return y;
  }

  
  public boolean isUseBilinearInterpolation() {
    return isUseBilinearInterpolation;
  }

  public void setUseBilinearInterpolation( boolean aUseBilinearInterpolation ) {
    isUseBilinearInterpolation = aUseBilinearInterpolation;
  }

  /**
   * fast method for loading RGB array from a BufferedImage
   * 
   * @param anImage
   * @return
   */
  public static int[] getRGB(BufferedImage anImage){
    int theWidth = anImage.getWidth();
    int theHeight = anImage.getHeight();
    int[] theColors = new int[theWidth * theHeight];


    int theType = anImage.getRaster().getDataBuffer().getDataType();
    int theBands = anImage.getRaster().getNumBands();

    if(theType == DataBuffer.TYPE_BYTE && theBands > 1){
      System.out.println("Loading image in fast way");
      byte[] theData = ((DataBufferByte)anImage.getRaster().getDataBuffer()).getData();

      //gray scale
      for(int i=0,j=0;i<theData.length;i+=theBands,j++){
        byte alpha = 0;
        byte blue = 0;
        byte green = 0;
        byte red = 0;
        if(theBands == 1){
          alpha = (byte)0xFF;
          blue =  (byte)(theData[i] >> 4 & 0x40);
          green = (byte)(theData[i] >> 2 & 0xC0);
          red = theData[i];
        } else if(theBands == 3){
          alpha = (byte)0xFF;
          blue =  theData[i];
          green = theData[i + 1];
          red = theData[i + 2];
        } else if(theBands == 4){
          red = theData[i];
          blue =  theData[i + 2];
          green = theData[i + 1];
          alpha = theData[i + 3];
        }
        //alpha = (byte)240;
        theColors[j] = (alpha << 24 & 0xFF000000) | (red << 16 & 0x00FF0000) | (green << 8 & 0x0000FF00) | (blue << 0 & 0x000000FF);
      }
    } else {
      System.out.println("Loading image in slow way");
      for(int x=0;x<theWidth;x++){
        for(int y=0;y<theHeight;y++){
          theColors[y * theWidth + x] = anImage.getRGB(x, y);
        }
      }
    }

    return theColors;
  }


}


