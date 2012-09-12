/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */

package chabernac.chart;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 *
 *
 * @version v1.0.0      Sep 27, 2005
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Sep 27, 2005 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public abstract class Paintable {
  private int width = 0;
  private int height = 0;
  
  public Paintable(){
    
  }
  
  public Paintable(int aWidth, int aHeight){
    width = aWidth;
    height = aHeight;
  }
  
  public abstract void paint(Graphics g);
  
  public void setWidth(int aWidth){
    width = aWidth;
  }
  
  public int getHeight(){
    return height;
  }
  
  public int getWidth(){
    return width;
  }
  
  public void setHeight(int aHeight){
	  height = aHeight;
  }
  
  public Dimension getDimension(){
    return new Dimension(width, height);
  }
  
  public void setDimension(Dimension aDimension){
    width = (int)aDimension.getWidth();
    height = (int)aDimension.getHeight();
  }
}
