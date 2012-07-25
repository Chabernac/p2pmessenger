/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.awt.Color;
import java.util.Collection;

import chabernac.space.buffer.DrawingRectangleContainer;
import chabernac.space.buffer.iPixelListener;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Polygon2D;
import chabernac.space.geom.VertexLine2D;
import chabernac.space.shading.iPixelShader;

public interface i3DGraphics {
  public void drawPolygon(Polygon2D aPolygon, Polygon anOrigPolygon);
  public void drawLine(VertexLine2D aLine);
  public void drawText(Point2D aPoint, String aText, Color aColor);
  public void drawRect(int x, int y, int width, int height);
  public void fillOval(int x, int y, int width, int height);
  public void setColor(Color c);
  public void setBackGroundColor(int aBackGroundColor);
  public void clear();
  public void cycleDone();
  public void setPixelListener(iPixelListener anPixelListener);
  public Collection<DrawingRectangleContainer> getDrawingRectangles();
  public void setDimensions(int aWidth, int aHeight);
  public void setUsePartialClearing( boolean aUsePartialClearing );
  public void setPixelShaders(iPixelShader[] aPixelShaders);
}
