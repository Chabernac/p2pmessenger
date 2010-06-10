package chabernac.easteregg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import chabernac.chat.gui.ChatMediator;

public class EasterEggPanel extends JPanel implements Runnable, iEasterEggWindow{
	private ChatMediator myMediator = null;
	private iPaintable myPaintable = null;
	private boolean stop = false;
	private boolean isRunning = false;
	private iEasterEggWindowListener myListener = null;


	public EasterEggPanel(ChatMediator aMediator, iPaintable aPaintable){
		myMediator = aMediator;
		myPaintable = aPaintable;
		addListeners();
		setFocusable(true);
		requestFocus();
	}

	private void addListeners(){
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent anE) {
				System.out.println("keypressed: " + anE);
				stop = true;
			}
		});
	}

	public void start() {
    stop = false;
    new Thread(this).start();
	}

  
	public void run() {
		isRunning = true;
    
		Dimension theSize = myMediator.getMainFrame().getSize();
		
    BufferedImage theWindowImage = new BufferedImage(theSize.width, theSize.height, BufferedImage.TYPE_INT_RGB);
    myMediator.getMainFrame().getContentPane().paint(theWindowImage.getGraphics());

    myMediator.getMainFrame().setGlassPane(this);
    setVisible(true);
    setFocusable(true);
    requestFocus();
    Graphics theG = getGraphics();
    
    
		BufferedImage theImage = new BufferedImage(theSize.width, theSize.height, BufferedImage.TYPE_INT_RGB);
    Graphics theImageGraphics = theImage.getGraphics();

		try {
			while(!stop){
				if(theG != null){
					if(!theSize.equals(myMediator.getMainFrame().getSize())){
						theSize = myMediator.getMainFrame().getSize();
						theImage = new BufferedImage(theSize.width, theSize.height, BufferedImage.TYPE_INT_RGB);
            theImageGraphics = theImage.getGraphics();
					}
					myPaintable.paint(theImageGraphics, new Rectangle(theSize), theWindowImage);
          getGraphics().drawImage(theImage, 0,0,null);
          
          if(!hasFocus()){
            requestFocus();
          }
				}
				Thread.sleep(30);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		setVisible(false);
    myMediator.getMainFrame().setGlassPane(this);
		//myMediator.getMainFrame().setResizable(true);
		isRunning = false;
	}
  
  /*
  public void paint(Graphics g){
    if(!hasFocus()){
      requestFocus();
    }
    if(isFirst){
      //isFirst = false;
      myWindowImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
      super.paint(myWindowImage.getGraphics());
      g.drawImage(myWindowImage, 0, 0, null);
      System.out.println("window image created");
      //startThread();
    }
    super.paint(g);
  }
  */

	public boolean isRunning() {
		return isRunning;
	}

	public void stop() {
		stop = true;
		myListener.easterEggWindowClosed();
	}

  public void setEasterEggWindowListener( iEasterEggWindowListener aListener ) {
    myListener = aListener;
  }
}
