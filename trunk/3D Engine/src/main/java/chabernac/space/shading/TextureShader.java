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
//    aPixel.color = 0xAAAAAAAA;
    
//    aPixel.texture
    
    if(aPixel.texture.myImage == null){
      aPixel.color = aPixel.texture.myColor;
    } else {
//      if(aPixel.uInt < 0 || aPixel.vInt <0){
//        aPixel.color = 0;
//      }
////      while(x < 0) x += width;
//      while(aPixel.uInt >= aPixel.texture.myImage.width) aPixel.uInt -= aPixel.texture.myImage.width;
////      while(y < 0) y += height;
//      while(aPixel.vInt >= aPixel.texture.myImage.height) aPixel.vInt -= aPixel.texture.myImage.height;
//
//      aPixel.color = aPixel.texture.myImage.colors[aPixel.vInt * aPixel.texture.myImage.width + aPixel.uInt];
      aPixel.color = aPixel.texture.getColor( aPixel.uInt, aPixel.vInt );
    }
//    aPixel.color = aPixel.texture.myImage.getColorAt( aPixel.uInt, aPixel.vInt );
    
  }
}
