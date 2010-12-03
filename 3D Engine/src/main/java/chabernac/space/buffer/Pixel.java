/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.buffer;

import chabernac.space.texture.Texture2;

public class Pixel {
  //the color of the pixel, initialized at the background color
  public int backGroundColor = 0;

  //the color of the pixel, initialized at the background color
  public int color = 0;
  
  //the x coordinate of the pixel in screen space
  public int x;
  
  //the x coordinate of the pixel in texture space
  public double u;
  
  //the y coordinate of the pixel in texture space
  public double v;
  
  //the x coordinate of the pixel in texture space rounded to an integer
  public int uInt;
  
  //the y  coordinate of the pixel in texture space rounded to an integer
  public int vInt;
  
  //the inverse of the depth in camera space
  public double invZ;
  
  //the depth in camera space
  public double z;

  //the lightning of the pixel initialized at the background light
  public double light;
  
  public Texture2 texture;
  
  public Pixel(){
    
  }
  
  public Pixel(int aBackGroundColor, Texture2 aTextue){
    backGroundColor = aBackGroundColor;
    texture = aTextue;
  }
  
  public void applyLightning(){
    int alpha = color >> 24 & 0xff;
    int red=  (int)(light * (  color >> 16 & 0xff));
    int green= (int) (light * (  color >> 8 & 0xff));
    int blue= (int) (light * (  color & 0xff));
    
    if(red > 255) red = 255;
    if(green > 255) green = 255;
    if(blue > 255) blue = 255;
    if(red < 0 ) red = 0;
    if(green < 0) green = 0;
    if(blue < 0) blue = 0;

    color = (alpha << 24 & 0xFF000000) | (red << 16 & 0x00FF0000) | (green << 8 & 0x0000FF00) | (blue << 0 & 0x000000FF);
  }
}
