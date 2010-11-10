package chabernac.easteregg;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class EasterEggPanel extends JPanel implements Runnable, iEasterEggWindow{
  private static final long serialVersionUID = 3499894199962819258L;
  private JFrame myRootFrame = null;
	private iPaintable myPaintable = null;
	private boolean stop = false;
	private boolean isRunning = false;
	private iEasterEggWindowListener myListener = null;
	private ExecutorService myService = Executors.newFixedThreadPool( 1 );


	public EasterEggPanel(JFrame aRootFrame, iPaintable aPaintable){
	  myRootFrame = aRootFrame;
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
    myService.execute( this );
	}

  
	public void run() {
		isRunning = true;
    
		Dimension theSize = myRootFrame.getSize();
		
    BufferedImage theWindowImage = new BufferedImage(theSize.width, theSize.height, BufferedImage.TYPE_INT_RGB);
    
    myRootFrame.setGlassPane(this);
    myRootFrame.getContentPane().paint(theWindowImage.getGraphics());

    setVisible(true);
    setFocusable(true);
    requestFocus();
    Graphics theG = getGraphics();
    
    
		BufferedImage theImage = new BufferedImage(theSize.width, theSize.height, BufferedImage.TYPE_INT_RGB);
    Graphics theImageGraphics = theImage.getGraphics();

		try {
			while(!stop){
				if(theG != null){
					if(!theSize.equals(myRootFrame.getSize())){
						theSize = myRootFrame.getSize();
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
		myRootFrame.setGlassPane(this);
		isRunning = false;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void stop() {
		stop = true;
		myService.shutdownNow();
		myListener.easterEggWindowClosed();
		myListener = null;
		myPaintable = null;
		myRootFrame = null;
		myService = null;
	}

  public void setEasterEggWindowListener( iEasterEggWindowListener aListener ) {
    myListener = aListener;
  }
}
