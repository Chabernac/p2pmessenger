/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.shading;

import chabernac.space.buffer.Pixel;

public class TextureShader implements iPixelShader {
  private int width, height;

  public TextureShader(){
  }

  @Override
  public void calculatePixel( Pixel aPixel ) {
    aPixel.uInt = (int)aPixel.u;
    aPixel.vInt = (int)aPixel.v;

    if(aPixel.texture.myImage == null){
      aPixel.color = aPixel.texture.myColor;
    } else {
      if(aPixel.uInt < 0 || aPixel.vInt <0){
        aPixel.color = 0;
      } else {
        width = aPixel.texture.myImage.width;
        height = aPixel.texture.myImage.height;
//        while(aPixel.uInt < 0) aPixel.uInt += width;
        while(aPixel.uInt >= width) aPixel.uInt -= width;
//        while(aPixel.vInt < 0) aPixel.vInt += height;
        while(aPixel.vInt >= height) aPixel.vInt -= height;

        aPixel.color = aPixel.texture.myImage.colors[aPixel.vInt * height + aPixel.uInt];
      }
    }
//          aPixel.color = aPixel.texture.getColor( aPixel.uInt, aPixel.vInt );
//        aPixel.color = aPixel.texture.myImage.getColorAt( aPixel.uInt, aPixel.vInt );

  }
}
