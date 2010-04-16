package chabernac.space.texture;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JPanel;

import chabernac.GUI.components.DefaultExitFrame;

public class TexturePanel extends JPanel{
  private TextureImage myImage = null;
  
  public TexturePanel(String aTexture, boolean isTransparent) throws IOException{
    myImage = TextureFactory.getTexture(aTexture, isTransparent);
    setPreferredSize(new Dimension(myImage.width, myImage.height));
  }
  
  protected void paintComponent(Graphics g){
    g.drawImage(myImage.image, 0, 0, null);
  }
  
  public static void main(String args[]){
    try {
      DefaultExitFrame theFrame = new DefaultExitFrame();
      theFrame.getContentPane().setLayout(new BorderLayout());
      theFrame.getContentPane().add(new TexturePanel("GENGRID", true), BorderLayout.CENTER);
      theFrame.pack();
      theFrame.setVisible(true);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
