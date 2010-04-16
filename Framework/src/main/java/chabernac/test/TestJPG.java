

package chabernac.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;

public class TestJPG {

  public static void main(String[] args) {
    JPEGEncodeParam theParams = new JPEGEncodeParam();
    try{
      ImageEncoder encoder = ImageCodec.createImageEncoder("JPEG", new FileOutputStream("test.jpg"), theParams);
      BufferedImage theImage = new BufferedImage(200,200, BufferedImage.TYPE_INT_RGB);
      Graphics g = theImage.getGraphics();
      g.setColor(Color.white);
      g.fillRect(0,0,200,200);
      g.setColor(Color.black);
      g.drawRect(20,20,150,150);
      encoder.encode(theImage);
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
