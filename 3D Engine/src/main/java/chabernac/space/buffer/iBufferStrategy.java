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

import chabernac.space.Polygon2D;
import chabernac.space.Vertex2D;
import chabernac.space.geom.Point2D;

public interface iBufferStrategy {
	public void drawPolygon(Polygon2D aPolygon);
	public Image getImage();
	public void clear();
	public void setGraphics(Graphics g);
	public void setDebugMode(int aDebugMode);
	public int getDebugMode();
	public void drawLine(Vertex2D theStartVertex, Vertex2D theEndVertex, int aColor);
	public void setValueAt(int x, int y, double aDepth, int aColor, boolean ignoreDepth);
	public void setBackGroundColor(Color aBackgroundColor);
  public void drawText(Point2D aPoint, String aText, Color aColor);
}
