package chabernac.chat.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import chabernac.command.Command;

public class EasterEggPanel extends JPanel implements Command, Runnable{
  private ChatMediator myMediator = null;
  private BufferedImage myImage = null;
  private BufferedImage myMainImage = null;
  private boolean stop = false;
  private int x = 0;
  private int y = 0;
  
  public EasterEggPanel(BufferedImage anImage, ChatMediator aMediator){
   myImage = anImage; 
   myMediator = aMediator;
  }

  public void execute() {
    startAnimation();
  }
  
  public void startAnimation(){
    new Thread(this).start();
  }

  public void run() {
    stop = false;
    myMediator.getMainFrame().setGlassPane(this);
    setVisible(true);
    while(!stop){
      moveImage();
      repaint();
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    setVisible(false);
  }
  
  private void moveImage(){
    stop = true;
  }
  
  public void paintComponent(Graphics g){
//    myMediator.getMainFrame().repaint();
//    myMediator.getMainFrame().paint(g);
//    Graphics2D theG = (Graphics2D)g;
//    g.setColor(Color.white);
//    theG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0F));
//    theG.fillRect(0, 0, getWidth(), getHeight());
//    theG.setComposite(AlphaComposite.Src);
//    g.drawImage(myImage, 100, 100, null);
  }
}
