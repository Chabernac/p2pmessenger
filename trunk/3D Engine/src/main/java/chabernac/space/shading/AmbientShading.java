/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.shading;

import chabernac.space.Vertex;
import chabernac.space.World;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Shape;

public class AmbientShading implements iVertexShader {
  private float myAmbientLigth;
  
    public AmbientShading( float aAmbientLigth ) {
    super();
    myAmbientLigth = aAmbientLigth;
  }

  @Override
  public void applyShading( World aWorld ) {
    Shape theCurrentShape = null;
    Polygon theCurrentPolygon = null;

    for(int j=0;j<aWorld.myShapes.length;j++){
      theCurrentShape = aWorld.myShapes[j];
      for(int k=0;k<theCurrentShape.myPolygons.length;k++){
        theCurrentPolygon = theCurrentShape.myPolygons[k];
        if(theCurrentPolygon.c.length > 0 && theCurrentPolygon.visible) {
            applyIlluminatingFactor(myAmbientLigth, theCurrentPolygon);
        }
      }
    }
  }
  
  private void applyIlluminatingFactor(float illuminatingFactor, Polygon theCurrentPolygon) {
    Vertex theCurrentVertex = null;

    for(int l=0;l<theCurrentPolygon.myCamSize;l++){
      theCurrentVertex = theCurrentPolygon.c[l];
      theCurrentVertex.lightIntensity = illuminatingFactor;
    }
  }

  public float getAmbientLigth() {
    return myAmbientLigth;
  }

  public void setAmbientLigth( float aAmbientLigth ) {
    myAmbientLigth = aAmbientLigth;
  }
}
