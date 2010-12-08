/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;

import javax.swing.JFrame;

public class ImageUtils {
  public static void showImage(Image anImage){
    ImageFrame theFrame = new ImageFrame( anImage );
    theFrame.setAlwaysOnTop( true );
    theFrame.setVisible( true );
  }
  
  private final static class ImageFrame extends JFrame{
    private final Image myImage;
    private final Color myBackGround = new Color(200,200,255);
    
    public ImageFrame( Image aImage ) throws HeadlessException {
      super();
      myImage = aImage;
      setSize( myImage.getWidth(null), myImage.getHeight(null) );
    }

    public void paint(Graphics g){
      g.setColor( myBackGround );
      g.fillRect( 0, 0, getWidth(), getHeight() );
      g.drawImage( myImage, 0, 0, null);
    }
  }
  
  
}
