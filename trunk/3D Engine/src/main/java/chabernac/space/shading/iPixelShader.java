/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.shading;

import chabernac.space.buffer.Pixel;

public interface iPixelShader {
  public void calculatePixel(Pixel aPixel);
}
