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

public class SpecularShader implements iPixelShader {
  private final World myWorld;
  
  private final Point3D myCamLocation = new Point3D( 0, 0, 0 );
  
  public SpecularShader( World aWorld ) {
    super();
    myWorld = aWorld;
  }


  @Override
  public void calculatePixel( Pixel aPixel ) {
    //we might already have calculated this point for the bump mapping, make sure we do not do the calculation twice
    Point3D theCamPoint = aPixel.texture.getSystem().getTransformator().inverseTransform(new Point3D(aPixel.u, aPixel.v, (float)0.0));

    GVector theVectorTowardsCamera = new GVector( theCamPoint, myCamLocation ).norm();
    GVector theNormalAtCamPoint = aPixel.texture.getSystem().getZUnit();
    
    float theSpecularLightning = 0;
    
    for(LightSource theLightSource : myWorld.getLightSources()){
      GVector theVectorTowarsLightSource = new GVector( theCamPoint, theLightSource.getCamLocation());
      
      float theDistanceTowardsLightSource = theVectorTowarsLightSource.length();
      
      theVectorTowarsLightSource = theVectorTowarsLightSource.norm();
      
      float theProjectionOfVectorTowardsCamera = theNormalAtCamPoint.dotProdukt( theVectorTowardsCamera );
      float theProjectionsOfVectorTowardsLightSource = theNormalAtCamPoint.dotProdukt( theVectorTowarsLightSource );
      
      //when the angle of the normal and the vector towards camera and light source are equal the division between the 2 will be 1
      float theFactor = (1 - Math.abs(theProjectionOfVectorTowardsCamera - theProjectionsOfVectorTowardsLightSource));
      theFactor *= theLightSource.getIntensity();
      theFactor /= theDistanceTowardsLightSource;
      
      theSpecularLightning += theFactor;
    }
    
    aPixel.light +=  theSpecularLightning; 
    
  }

}
