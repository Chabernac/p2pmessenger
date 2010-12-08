/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.buffer;

public interface iDepthBuffer {
  public boolean isDrawPixel(int x, int y, double anInverseDepth);
  public void clearBuffer();
}
