/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.shading;

import chabernac.space.buffer.Segment;

public interface iPixelShader {
  public int calculatePixel(Segment aSegment);
}
