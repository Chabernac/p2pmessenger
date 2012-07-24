/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.awt.Color;
import java.awt.Graphics;

import chabernac.space.buffer.Graphics3D2D;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Polygon2D;
import chabernac.space.geom.VertexLine2D;

public class GraphicsAdapter implements i3DGraphics {
  private Graphics myGraphics;
  private final Graphics3D2D my3D2DGraphics;
  
  public GraphicsAdapter( Graphics aGraphics, Graphics3D2D aGraphics3D2D ) {
    super();
    myGraphics = aGraphics;
    my3D2DGraphics = aGraphics3D2D;
  }

  @Override
  public void drawPolygon( Polygon2D aPolygon, Polygon anOrigPolygon ) {
    my3D2DGraphics.drawPolygon( aPolygon, anOrigPolygon );
  }

  @Override
  public void drawLine( VertexLine2D aLine ) {
    my3D2DGraphics.drawLine( aLine );
  }

  @Override
  public void drawText( Point2D aPoint, String aText, Color aColor ) {
    my3D2DGraphics.drawText( aPoint, aText, aColor );

  }

  @Override
  public void drawRect( int aX, int aY, int aWidth, int aHeight ) {
    myGraphics.drawRect( aX, aY, aWidth, aHeight );
  }

  @Override
  public void fillOval( int x, int y, int width, int height ) {
    myGraphics.fillOval( x, y, width, height );

  }

  @Override
  public void setColor( Color aC ) {
    myGraphics.setColor( aC );
  }

  public void setGraphics( Graphics aGraphics ) {
    myGraphics = aGraphics;
  }

  @Override
  public void setBackGroundColor( int aBackGroundColor ) {
    my3D2DGraphics.setBackGroundColor( aBackGroundColor );
    
  }

  @Override
  public void clear() {
    my3D2DGraphics.clear();
  }
}
