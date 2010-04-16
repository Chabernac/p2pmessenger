/*
 * Created on 31-jul-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.buffer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


public abstract class AbstractBuffer implements iBufferStrategy {
	//private BufferedImage myImage = null;
  protected BufferedImage myImage = null;
	protected int myWidth, myHeight,mySize;
	//Graphics object only serves for debugging purposes
	protected Graphics g = null;
	protected Graphics myGraphics = null;
	private int debugMode = 0;
	private Color myBackGroundColor = new Color(0,0,0,0);
  protected int[] myDataBuffer = null;


	public AbstractBuffer(int aWidth, int aHeight){
		myWidth = aWidth;
		myHeight = aHeight;
    mySize = myWidth * myHeight;
    
		myImage = new BufferedImage(myWidth, myHeight, BufferedImage.TYPE_INT_ARGB);
    myDataBuffer = ((DataBufferInt)myImage.getData().getDataBuffer()).getData();
    
		myGraphics = myImage.getGraphics();
	}

	public final Image getImage(){
		prepareImage();
    
		return myImage;
	}

	protected void prepareImage(){
    myImage.setRGB(0, 0, myWidth, myHeight, myDataBuffer, 0, myWidth);
  }

	public final void clear(){
		clearBuffer();
    
    for(int i=0;i<myDataBuffer.length;i++){
      myDataBuffer[i] = myBackGroundColor.getRGB();
    }
    
		myGraphics.setColor(myBackGroundColor);
		myGraphics.fillRect(0,0,myWidth, myHeight); 
	}

	protected void drawSegment(Segment aSegment, int y){
		while(aSegment.hasNext()){
			aSegment.next();
			setValueAt(aSegment.getX(), y, aSegment.getInverseZ(), aSegment.getColor(), false);
		}
		/*
		aSegment.setXStart(Math.ceil(aSegment.getXStart()));
		int color = aSegment.getColor();
		double redStart = aSegment.getLStart() * ( ( color & 0x00FFFFFF ) >> 16 );
		double greenStart = aSegment.getLStart() * ( ( color & 0x0000FFFF ) >> 8 );
		double blueStart = aSegment.getLStart() * ( ( color & 0x000000FF ) >> 0 );

		double redEnd = aSegment.getLEnd() * ( ( color & 0x00FFFFFF ) >> 16 );
		double greenEnd= aSegment.getLEnd() * ( ( color & 0x0000FFFF ) >> 8 );
		double blueEnd= aSegment.getLEnd() * ( ( color & 0x000000FF ) >> 0 );

		double deltaRed = (redEnd - redStart) / aSegment.getXDiff();
		double deltaGreen = (greenEnd - greenStart) / aSegment.getXDiff();
		double deltaBlue = (blueEnd - blueStart) / aSegment.getXDiff();

		double currentRed = redStart;
		double currentGreen = greenStart;
		double currentBlue = blueStart;

		double z = aSegment.getZStart();
		double deltaZ = aSegment.getZRico();



		for(int x = (int)(aSegment.getXStart());x <= aSegment.getXEnd();x++){
			int red = (int)currentRed;
			int green = (int)currentGreen;
			int blue = (int)currentBlue;
			if(red > 255) red = 255;
			if(green > 255) green = 255;
			if(blue > 255) blue = 255;
			if(blue < 0 ) blue = 0;
			if(green < 0) green = 0;
			if(blue < 0) blue = 0;
			if(aSegment.getTexture() != null){
				setValueAt(x, y, z, aSegment.getTexture().getColorAtScreenLocation(x, y, z), false);
			} else {
				setValueAt(x, y, z, 0xFF << 24 | red << 16 | green << 8 | blue, false);
			}

			currentRed += deltaRed;
			currentGreen += deltaGreen;
			currentBlue += deltaBlue;
			z+= deltaZ;
		}
		 */
	}

	protected abstract void clearBuffer();


	public final int getHeight(){
		return myHeight;
	}

	public final int getWidth(){
		return myWidth;
	}

  protected void setPixelAt(int i, int aColor){
    myDataBuffer[i] = aColor;
  }
  
	protected void setPixelAt(int x, int y, int aColor){
    myDataBuffer[y * myWidth + x] = aColor; 
		//myImage.setRGB(x, y, aColor);
	}
  
  protected int getPixelAt(int i){
    return myDataBuffer[i];
  }
  
  protected int getPixelAt(int x, int y){
    return myDataBuffer[y * myWidth + x];
  }

	public void setValueAt(int x, int y, double z, int aColor, boolean ignoreDepth){
		setPixelAt(x, y, aColor);
	}

	public void setDebugMode(int aDebugMode){
		debugMode = aDebugMode;
	}

	public int getDebugMode(){
		return debugMode;
	}

	public void setGraphics(Graphics g){
		this.g = g;
	}

	public Graphics getGraphics(){
		return g;
	}

	public Color getBackGroundColor() {
		return myBackGroundColor;
	}

	public void setBackGroundColor(Color aBackGroundColor) {
		myBackGroundColor = new Color(aBackGroundColor.getRed(), aBackGroundColor.getGreen(), aBackGroundColor.getBlue(), 255);
	}
	
}
