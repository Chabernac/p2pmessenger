/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.shading;

import chabernac.space.LightSource;
import chabernac.space.World;
import chabernac.space.buffer.Pixel;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;

public class BumpShader implements iPixelShader {
  private final World myWorld;
  
  public BumpShader(World aWorld){
    myWorld = aWorld;
  }

  @Override
  public void calculatePixel( Pixel aPixel ) {
    if(aPixel.texture.getBumpMap() != null){
      GVector theCamNormalVector = aPixel.texture.getNormalVector(aPixel.uInt, aPixel.vInt);
      Point3D theCamPoint = aPixel.getCamPoint();
      aPixel.light += LightSource.calculateLight(myWorld, theCamPoint, theCamNormalVector);
        
      aPixel.light /= 2D;
    }
  }

}
