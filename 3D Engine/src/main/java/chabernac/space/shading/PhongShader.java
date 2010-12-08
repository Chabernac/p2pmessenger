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

/**
 * http://www.gamedev.net/reference/articles/article325.asp+calculate+specular+lighting&cd=1&hl=nl&ct=clnk&gl=be&client=firefox-a 
 */
public class PhongShader implements iPixelShader {
 private final World myWorld;
 
 private final float myAmbient = (float)0.4;
 private final float myDiffuse = (float)0.2;
 private final float mySpecular = 100;
 private final float myPower = 200;
  
  private final Point3D myCamLocation = new Point3D( 0, 0, 0 ); 
  
  public PhongShader( World aWorld) {
    super();
    myWorld = aWorld;
  }

  @Override
  public void calculatePixel( Pixel aPixel ) {
    
//    Ka + Kd * (N dot L) + Ks * (N dot ( L + V / 2))^n
//    Ka = ambient lightning
//    Kd = the diffuse relfection constant
//    N  = surface normal
//    L  = unit vector between point and light
//    KS = specular light constant
//    V  = unit vector between point and view
//    R  = light reflection unit vector (mirror of L about N)

  //we might already have calculated this point for the bump mapping, make sure we do not do the calculation twice
    Point3D theCamPoint = aPixel.getCamPoint();

    GVector theVectorTowardsCamera = new GVector( theCamPoint, myCamLocation ).norm();
    
    //the normal of the plane
//    GVector theNormalAtCamPoint = aPixel.texture.getSystem().getZUnit();
    
    //but we can as well use the normal of the  bump map
    GVector theNormalAtCamPoint = null;
    if(aPixel.texture.getBumpMap() != null){
      theNormalAtCamPoint = aPixel.getNormal();
    } else {
      theNormalAtCamPoint = aPixel.texture.getSystem().getZUnit();
    }
    
    
    float theSpecularLightning = 0;
    
    for(LightSource theLightSource : myWorld.getLightSources()){
      GVector theVectorTowarsLightSource = new GVector( theCamPoint, theLightSource.getCamLocation()).norm();
      
      GVector theMoyenVector = theVectorTowarsLightSource.addition( theVectorTowardsCamera ).multip( (float)0.5 ); 
      
      float theLight = 0;
      theLight += myAmbient + myDiffuse * theNormalAtCamPoint.dotProdukt( theVectorTowarsLightSource );
      //onlye specular
      theLight += mySpecular * Math.pow( theNormalAtCamPoint.dotProdukt( theMoyenVector ), myPower);
      
      theSpecularLightning += theLight;
    }
    
//    System.out.println(aPixel.u + ", " + aPixel.v + ": " + theSpecularLightning);
    
    aPixel.light += theSpecularLightning; 

  }

}
