/*
 * Created on 19-jul-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.shading;

import java.util.ArrayList;

import chabernac.space.LightSource;
import chabernac.space.Polygon;
import chabernac.space.Shape;
import chabernac.space.Vertex;
import chabernac.space.World;
import chabernac.space.geom.GVector;

public class GouroudShading implements iLightManager {
	private double myAmbient;
	
	public GouroudShading(double ambient){
		myAmbient = ambient;
	}

	public void calculateLight(World aWorld) {
		LightSource theCurrentLight = null;
		Shape theCurrentShape = null;
		Polygon theCurrentPolygon = null;
		Vertex theCurrentVertex = null;
		ArrayList theLightSources = aWorld.getLightSources();
		
		
		for(int j=0;j<aWorld.myShapes.length;j++){
			theCurrentShape = aWorld.myShapes[j];
			if(theCurrentShape.visible){
				for(int k=0;k<theCurrentShape.myPolygons.length;k++){
					theCurrentPolygon = theCurrentShape.myPolygons[k];
					if(theCurrentPolygon.visible)	{
						for(int l=0;l<theCurrentPolygon.myCamSize;l++){
							theCurrentVertex = theCurrentPolygon.c[l];
							theCurrentVertex.lightIntensity = myAmbient;
							for(int i=0;i<theLightSources.size();i++){
								theCurrentLight = (LightSource)theLightSources.get(i);
								theCurrentVertex.lightIntensity += calculateIlluminatingFactor(theCurrentLight, theCurrentVertex);
							}
						}
					}
				}
			}
		}
	}

	private double calculateIlluminatingFactor(LightSource theCurrentLight, Vertex theCurrentVertex) {
		GVector theDirectionToPolygon = new GVector(theCurrentVertex.myPoint, theCurrentLight.getCamLocation());
		double distance = theDirectionToPolygon.length();
		theDirectionToPolygon.normalize();
		double lightningFactor = theDirectionToPolygon.dotProdukt(theCurrentVertex.normal) *  theCurrentLight.getIntensity() / distance;
		//double lightningFactor = theDirectionToPolygon.dotProdukt(theCurrentVertex.normal);
		if(lightningFactor < 0) lightningFactor = 0;
		return lightningFactor;
	}
}
