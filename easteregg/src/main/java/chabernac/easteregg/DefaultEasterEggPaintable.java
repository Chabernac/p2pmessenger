/*
 * Created on 24-jan-08
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.easteregg;

import java.awt.Dimension;

import javax.swing.JFrame;

public abstract class DefaultEasterEggPaintable implements iPaintable, iEasterEgg {
	private boolean isFullScreen = false;
	private boolean isPaintBackground = false;
	protected iEasterEggWindow myWindow = null;
	protected final JFrame myRootFrame;
	private Dimension myResolution = null;
  private iEasterEggListener myEasterEggListener = null;

	public DefaultEasterEggPaintable(JFrame aRootFrame){
	  myRootFrame = aRootFrame;
	}

	public boolean isFullScreen() {
		return isFullScreen;
	}

	public void setFullScreen(boolean isFullScreen) {
		this.isFullScreen = isFullScreen;
	}

	public boolean isPaintBackground() {
		return isPaintBackground;
	}

	public void setPaintBackground(boolean anIsPaintBackground) {
		isPaintBackground = anIsPaintBackground;
	}

	public boolean isRunning() {
		if(myWindow != null){
			return myWindow.isRunning();
		}
		return false;
	}

	public void setParameter(Object aParameter) {
		if(aParameter.equals("fullscreen")){
			setFullScreen(true);
		}
		if(aParameter.equals("paintbackground")){
			setPaintBackground(true);
		}
	}

	public void start() {
		if(isRunning()) return;
		if(isFullScreen){
			myWindow = new EasterEggFrame(this, myResolution); 
		} else {
			myWindow = new EasterEggPanel(myRootFrame, this);
		}
		myWindow.setEasterEggWindowListener( new MyEasterEggViewListener() );
		myWindow.start();

	}

	public void stop() {
		if(myWindow != null){
			myWindow.stop();
			myWindow = null;
		}

	}

	public Dimension getResolution() {
		return myResolution;
	}

	public void setResolution(Dimension aResolution) {
		this.myResolution = aResolution;
	}
	
	public void setEasterEggListener(iEasterEggListener aListener){
	  myEasterEggListener = aListener;
	}
	
	private class MyEasterEggViewListener implements iEasterEggWindowListener{
    public void easterEggWindowClosed() {
      myWindow = null;
      clear();
      myEasterEggListener.easterEggStopped();
    }
	}

  protected void clear(){}	
}
