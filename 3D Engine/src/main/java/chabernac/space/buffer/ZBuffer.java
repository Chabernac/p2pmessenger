/*
 * Created on 18-jul-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.buffer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import chabernac.space.Polygon2D;
import chabernac.space.Vertex2D;
import chabernac.space.World;
import chabernac.space.geom.Point2D;

public class ZBuffer extends AbstractBuffer{
	private double myZBuffer[];
  private int myAlphaBuffer[];
  private double myTransparencyBuffer[];
	private double myMinY, myMaxY;
	private int cycle = 0;
	private boolean isBufferEnabled = true;
  private Font myFont = new Font("Arial", Font.PLAIN, 10);


	public ZBuffer(World aWorld, int aWidth, int aHeight){
		super(aWorld, aWidth, aHeight);
		myZBuffer = new double[mySize];
    myAlphaBuffer = new int[mySize];
    myTransparencyBuffer = new double[mySize];
		clearBuffer();
	}

	protected void clearBuffer(){
		if(isBufferEnabled){ 
			cycle = cycle + 1;
			if(cycle == 0){
				for(int i=0;i<mySize;i++){
          myZBuffer[i] = 0;
          myTransparencyBuffer[i] = 0;
				}
			}
		}
	}

	public void setValueAt(int x, int y, double aDepth, int aColor, boolean ignoreDepth){
    int i = y * myWidth + x; 

    if( i < 0 || i >= mySize || aDepth < 0 || aDepth > 1){
      return;
    }

    int theTransparency = (aColor >> 24) & 0xff;
    
		if( theTransparency == 0x00 ){ 
			return;  
		}

    //true when 0x00 < transparency < 0xff 
    boolean transparent = (theTransparency != 0xff);
    
    double cycleDepth = cycle + aDepth;
    
    if(transparent && cycleDepth > myZBuffer[i]){
      myAlphaBuffer[i] = aColor;
      myTransparencyBuffer[i] = cycleDepth;
      
      setPixelAt(i, mixColors(aColor, getPixelAt(i)));
    } else {
      if(cycleDepth > myZBuffer[i]){
        myZBuffer[i] = cycleDepth;
        if(cycleDepth < myTransparencyBuffer[i]){
          setPixelAt(i, mixColors(myAlphaBuffer[i], aColor));
        } else {
          setPixelAt(i, aColor);
        }
      }
    }
	}
  
  private int mixColors(int aTransparencyColor, int aColor){
    int alpha = aTransparencyColor >> 24 & 0xff;
    int red = aTransparencyColor >> 16 & 0xff;
    int green = aTransparencyColor >> 8 & 0xff;
    int blue = aTransparencyColor & 0xff;
    
    //int alpha2 = aColor >> 24 & 0xff;
    int red2 = aColor >> 16 & 0xff;
    int green2 = aColor >> 8 & 0xff;
    int blue2 = aColor & 0xff;
    
    double thePercentage = alpha / 256D;
    double theInvPercentage = 1 - thePercentage;
    
    red = (int)(thePercentage * red + theInvPercentage * red2); 
    green = (int)(thePercentage * green + theInvPercentage * green2);
    red = (int)(thePercentage * red + theInvPercentage * blue2);
    
    return (0xff << 24 & 0xFF000000) | (red << 16 & 0x00FF0000) | (green << 8 & 0x0000FF00) | (blue << 0 & 0x000000FF);
  }

	public void drawPolygon(Polygon2D aPolygon) {
		//TimeTracker.start();
		findMinMaxY(aPolygon);
		//TimeTracker.logTime("finding min max y");
		Vertex2D[] theScanLine;
		Color theColor = aPolygon.getColor();
		for(int y = (int)Math.ceil(myMinY);y <= myMaxY;y++){
			//TimeTracker.start();
			theScanLine = aPolygon.intersectHorizontalLine(y);
			//TimeTracker.logTime("Intersecting with horizontal line: " + y);
			if(theScanLine.length == 2 && theScanLine[0] != null && theScanLine[1] != null){
        Segment theSegment = Segment.getInstance(myWorld, theScanLine[0],theScanLine[1], theColor.getRGB(), aPolygon.getTexture() ); 
        //Segment theSegment = new Segment(theScanLine[0],theScanLine[1], theColor.getRGB(), aPolygon.getTexture() );
				drawSegment(theSegment, y);
        Segment.freeInstance(theSegment);
				//TimeTracker.logTime("Drawing segment");;
			}
      Point2D.freeInstance(theScanLine[0].getPoint());
      Point2D.freeInstance(theScanLine[1].getPoint());
      Vertex2D.freeInstance(theScanLine[0]);
      Vertex2D.freeInstance(theScanLine[1]);
		}
	}
  
  
  public void drawText(Point2D aPoint, String aText, Color aColor) {
    FontMetrics theMetrics = myGraphics.getFontMetrics(myFont);
    int theWidth = theMetrics.stringWidth(aText) + 2;
    int theHeight = theMetrics.getHeight();
    int theAscent = theMetrics.getAscent();
    BufferedImage theCharImage = new BufferedImage(theWidth, theHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics theGraphics = theCharImage.getGraphics();
    Color theTransparentColor = new Color(0,0,0,0); 
    int theTransparentC =  theTransparentColor.getRGB();
    theGraphics.setColor(theTransparentColor);
    theGraphics.fillRect(0, 0, theWidth, theHeight);
    theGraphics.setColor(aColor);
    theGraphics.drawString(aText, 0, theAscent);
    int[] thePixels = (int[])((DataBufferInt)theCharImage.getData().getDataBuffer()).getData();
    
    int baseX = (int)aPoint.x;
    int baseY = (int)aPoint.y;
    int x = baseX;
    int y = baseY;
    int j = 0;
    for(int i=0;i<thePixels.length;i++, x++, j++){
      if(j == theWidth){
        x = baseX;
        y++;
        j = 0;
      }
      
      if(x < myWidth && y < myHeight && thePixels[i] != theTransparentC){
        //System.out.println(thePixels[i] + " =?= " + theTransparentC);
        setPixelAt(x, y, thePixels[i]);
      }
    }
    
  }

	public void drawLine(Vertex2D aStartPoint, Vertex2D anEndEPoint, int aColor){
		Vertex2D theTempVertex = null;

		double xDiff =  anEndEPoint.getPoint().x - aStartPoint.getPoint().x;
		double yDiff =  anEndEPoint.getPoint().y - aStartPoint.getPoint().y;

		if(Math.abs(xDiff) > Math.abs(yDiff)){
			if(aStartPoint.getPoint().x > anEndEPoint.getPoint().x){
				theTempVertex = aStartPoint;
				aStartPoint = anEndEPoint;
				anEndEPoint = theTempVertex;
			}
			double zDiff = anEndEPoint.getInverseDepth() - aStartPoint.getInverseDepth();
			double deltaY = yDiff / xDiff;
			double deltaZ = zDiff / xDiff;
			double y = aStartPoint.getPoint().y;
			double z = aStartPoint.getInverseDepth();
			for(int x=(int)Math.ceil(aStartPoint.getPoint().x);x<(int)Math.floor(anEndEPoint.getPoint().x);x++){
				setValueAt(x, (int)y, (int)z, aColor, true);
				y += deltaY;
				z += deltaZ;
			}

		} else {
			if(aStartPoint.getPoint().y > anEndEPoint.getPoint().y){
				theTempVertex = aStartPoint;
				aStartPoint = anEndEPoint;
				anEndEPoint = theTempVertex;
			}
			double zDiff = anEndEPoint.getInverseDepth() - aStartPoint.getInverseDepth();
			double deltaX = xDiff / yDiff;
			double deltaZ = zDiff / yDiff;
			double x = aStartPoint.getPoint().x;
			double z = aStartPoint.getInverseDepth();
			for(int y=(int)Math.ceil(aStartPoint.getPoint().y);y<(int)Math.floor(anEndEPoint.getPoint().y);y++){
				setValueAt((int)x, y, (int)z, aColor, true);
				x += deltaX;
				z += deltaZ;
			}
		}
	}

	public void findMinMaxY(Polygon2D aPolygon){
		double[] minmax = BufferTools.findMinMaxY(aPolygon);
		myMinY = minmax[0];
		myMaxY = minmax[1];
	}

	//protected void prepareImage() {}

	public boolean isBufferEnabled() {
		return isBufferEnabled;
	}

	public void setBufferEnabled(boolean isBufferEnabled) {
		this.isBufferEnabled = isBufferEnabled;
	}

  public Font getFont() {
    return myFont;
  }

  public void setFont(Font anFont) {
    myFont = anFont;
  }
	
  
  




}
