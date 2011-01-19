/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.shading;

import chabernac.space.buffer.Pixel;

public class DepthShader implements iPixelShader {
  private float myBlackDepth;
  
  public DepthShader(int aBlackDepth){
    myBlackDepth = aBlackDepth;
  }

  @Override
  public void calculatePixel( Pixel aPixel ) {
    float theLigthFactor = (myBlackDepth - (1 / aPixel.invZ)) / myBlackDepth;
//    float theLigthFactor = ((aPixel.invZ * myBlackDepth - 1) * myBlackDepth) / aPixel.invZ;
    if(theLigthFactor < 0) {
      theLigthFactor = 0;
    }
    aPixel.light *= theLigthFactor;

//    Color theColor = new Color(1 / aPixel.invZ);
//    int theColor = (int)(1 / aPixel.invZ);
//    int theBlue = theColor % 256;
//    theColor /= 256;
//    int theGreen = theColor % 256;
//    theColor /= 256;
//    int theRed = theColor % 256;
//    aPixel.color = new Color(theRed, theGreen, theBlue).getRGB();
  }

}
