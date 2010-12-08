package chabernac.space.shading;

import java.util.ArrayList;

import chabernac.space.LightSource;
import chabernac.space.Vertex;
import chabernac.space.World;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Shape;

public class FlatShading implements iVertexShader{
  private double myAmbient = 0;

  public FlatShading(double ambient){
    myAmbient = ambient;
  }


  public void applyShading(World aWorld) {
    LightSource theCurrentLight = null;
    Shape theCurrentShape = null;
    Polygon theCurrentPolygon = null;
    ArrayList<LightSource> theLightSources = aWorld.getLightSources();
    double illuminatingFactor = 0;


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


  private double calculateIlluminatingFactor(LightSource theCurrentLight, Polygon theCurrentPolygon) {
    GVector theDirectionToPolygon = new GVector(theCurrentPolygon.myCamCenterPoint, theCurrentLight.getCamLocation());
    double distance = theDirectionToPolygon.length();
    theDirectionToPolygon.normalize();
    double lightningFactor = theDirectionToPolygon.dotProdukt(theCurrentPolygon.myNormalCamVector) *  theCurrentLight.getIntensity() / distance;
    if(lightningFactor < 0) lightningFactor = 0;
    return lightningFactor;
  }

  private void applyIlluminatingFactor(double illuminatingFactor, Polygon theCurrentPolygon) {
    Vertex theCurrentVertex = null;

    for(int l=0;l<theCurrentPolygon.myCamSize;l++){
      theCurrentVertex = theCurrentPolygon.c[l];
      theCurrentVertex.lightIntensity = illuminatingFactor;
    }
  }
}
