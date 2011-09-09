/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.cam;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class CamPanel extends JPanel implements iCamListener {
  private static final long serialVersionUID = -6209885982185841473L;
  private BufferedImage myImage;

  @Override
  public void imageReceived( BufferedImage anImage ) {
    myImage = anImage;
    repaint();
  }
  
  public void paint(Graphics g){
    g.drawImage( myImage, 0, 0, null);
  }

}
