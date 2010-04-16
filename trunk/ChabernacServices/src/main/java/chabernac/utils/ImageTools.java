package chabernac.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImageTools {
  public static BufferedImage makeTransparent(BufferedImage anImage, Color[] colors){
    BufferedImage theNewImage = new BufferedImage(anImage.getWidth(), anImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    for(int x=0;x<anImage.getWidth();x++){
      for(int y=0;y<anImage.getHeight();y++){
        int theRGB = anImage.getRGB(x, y);
        for(int i=0;i<colors.length;i++){
          if((theRGB & 0x00FFFFFF) == (colors[i].getRGB() & 0x00FFFFFF)){
            theRGB &= 0x00FFFFFF;
          } 
          theNewImage.setRGB(x, y, theRGB);
        }
      }
    }
    return theNewImage;
  }
}
