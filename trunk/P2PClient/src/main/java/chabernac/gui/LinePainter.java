/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui;

import java.awt.Color;
import java.awt.Graphics;

public class LinePainter implements iPaintable {
  private int x;
  private int y;
  private int with;
  private int heigth;
  private Color myColor;
  
  

  public LinePainter ( int anX , int anY , int anWith , int anHeigth, Color aColor ) {
    super();
    x = anX;
    y = anY;
    with = anWith;
    heigth = anHeigth;
    myColor = aColor;
  }

  @Override
  public void paint( Graphics anG ) {
   anG.setColor( myColor );
   anG.drawLine( x, y, with, heigth );
  }

}
