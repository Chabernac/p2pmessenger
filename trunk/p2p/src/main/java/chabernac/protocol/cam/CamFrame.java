/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.cam;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class CamFrame extends JFrame implements iCamListener{
  private static final long serialVersionUID = 7244851544793739106L;
  private BufferedImage myImage = null;

  @Override
  public void imageReceived( BufferedImage anImage ) {
    myImage = anImage;
    setSize( anImage.getWidth(), anImage.getHeight() );
    repaint();
  }
  
  public void paint(Graphics g){
    g.drawImage( myImage, 0, 0, null );
  }

}
