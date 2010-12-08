/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.shading;

import chabernac.space.buffer.Pixel;

public class DepthShading implements iPixelShader {
  private float myBlackDepth;
  
  public DepthShading(int aBlackDepth){
    myBlackDepth = aBlackDepth;
  }

  @Override
  public void calculatePixel( Pixel aPixel ) {
    float theLigthFactor = (myBlackDepth - (1 / aPixel.invZ)) / myBlackDepth;
//    float theLigthFactor = ((aPixel.invZ * myBlackDepth - 1) * myBlackDepth) / aPixel.invZ;
    if(theLigthFactor < 0) theLigthFactor = 0;
    aPixel.light *= theLigthFactor;
  }

}
