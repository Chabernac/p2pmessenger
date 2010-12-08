package chabernac.space.shading;

import java.util.ArrayList;

import chabernac.space.LightSource;
import chabernac.space.Vertex;
import chabernac.space.World;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Shape;

public class FlatShading implements iVertexShader{
  private float myAmbient = 0;

  public FlatShading(float ambient){
    myAmbient = ambient;
  }


  public void applyShading(World aWorld) {
    LightSource theCurrentLight = null;
    Shape theCurrentShape = null;
    Polygon theCurrentPolygon = null;
    ArrayList<LightSource> theLightSources = aWorld.getLightSources();
    float illuminatingFactor = 0;


    for(int j=0;j<aWorld.myShapes.length;j++){
      theCurrentShape = aWorld.myShapes[j];
      for(int k=0;k<theCurrentShape.myPolygons.length;k++){
        theCurrentPolygon = theCurrentShape.myPolygons[k];
        if(theCurrentPolygon.c.length > 0 && theCurrentPolygon.visible)	{
          illuminatingFactor = myAmbient;
          for(int i=0;i<theLightSources.size();i++){
            theCurrentLight = theLightSources.get(i);
            illuminatingFactor += calculateIlluminatingFactor(theCurrentLight, theCurrentPolygon);
            applyIlluminatingFactor(illuminatingFactor, theCurrentPolygon);
          }
        }
      }
    }
  }


  private float calculateIlluminatingFactor(LightSource theCurrentLight, Polygon theCurrentPolygon) {
    GVector theDirectionToPolygon = new GVector(theCurrentPolygon.myCamCenterPoint, theCurrentLight.getCamLocation());
    float distance = theDirectionToPolygon.length();
    theDirectionToPolygon.normalize();
    float lightningFactor = theDirectionToPolygon.dotProdukt(theCurrentPolygon.myNormalCamVector) *  theCurrentLight.getIntensity() / distance;
    if(lightningFactor < 0) lightningFactor = 0;
    return lightningFactor;
  }

  private void applyIlluminatingFactor(float illuminatingFactor, Polygon theCurrentPolygon) {
    Vertex theCurrentVertex = null;

    for(int l=0;l<theCurrentPolygon.myCamSize;l++){
      theCurrentVertex = theCurrentPolygon.c[l];
      theCurrentVertex.lightIntensity = illuminatingFactor;
    }
  }
}
