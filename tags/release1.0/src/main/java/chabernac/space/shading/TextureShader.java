/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.shading;

import chabernac.space.buffer.Pixel;

public class TextureShader implements iPixelShader {

  public TextureShader(){
  }

  @Override
  public void calculatePixel( Pixel aPixel ) {
    aPixel.uInt = (int)aPixel.u;
    aPixel.vInt = (int)aPixel.v;
    aPixel.color = aPixel.texture.getColor( aPixel.uInt, aPixel.vInt );
  }
}
