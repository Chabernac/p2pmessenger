package chabernac.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
  private static final long serialVersionUID = 889792030833964868L;
  private  BufferedImage myImage;

  public ImagePanel(BufferedImage anImage) {
    myImage = anImage;
  }

  public BufferedImage getImage() {
    return myImage;
  }

  public void setImage(BufferedImage anImage) {
    myImage = anImage;
    repaint();
  }
  
  public void paint(Graphics g){
    g.drawImage(myImage, 0, 0, null);
  }

}
