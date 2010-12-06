/*
 * Created on 25-jul-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.buffer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Collection;

import chabernac.space.geom.Point2D;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Polygon2D;
import chabernac.space.geom.VertexLine2D;
import chabernac.space.shading.iPixelShader;

public interface iBufferStrategy {
	public Image getImage();
	public void clear();
	public void cycleDone();
	public void setGraphics(Graphics g);
	public void setDebugMode(int aDebugMode);
	public int getDebugMode();
	
	public void setBackGroundColor(int aBackGroundColor);
	
	public void drawLine(VertexLine2D aLine);
	public void drawPolygon(Polygon2D aPolygon, Polygon aOrigPoligon);
  public void drawText(Point2D aPoint, String aText, Color aColor);
  
  public Collection<DrawingRectangleContainer> getDrawingRectangles();
  
  public void setPixelShaders(iPixelShader[] aPixelShaders);
}
