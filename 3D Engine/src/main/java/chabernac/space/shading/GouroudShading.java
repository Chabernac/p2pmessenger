/*
 * Created on 19-jul-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space.shading;

import chabernac.space.LightSource;
import chabernac.space.Vertex;
import chabernac.space.World;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Shape;

public class GouroudShading implements iVertexShader {
	private float myAmbient;
	
	public GouroudShading(float ambient){
		myAmbient = ambient;
	}

	public void applyShading(World aWorld) {
		Shape theCurrentShape = null;
		Polygon theCurrentPolygon = null;
		Vertex theCurrentVertex = null;
		
		for(int j=0;j<aWorld.myShapes.length;j++){
			theCurrentShape = aWorld.myShapes[j];
			if(theCurrentShape.visible){
				for(int k=0;k<theCurrentShape.myPolygons.length;k++){
					theCurrentPolygon = theCurrentShape.myPolygons[k];
					if(theCurrentPolygon.visible)	{
						for(int l=0;l<theCurrentPolygon.myCamSize;l++){
							theCurrentVertex = theCurrentPolygon.c[l];
							theCurrentVertex.lightIntensity = myAmbient;
							for(LightSource theCurrentLight : aWorld.lightSources){
								theCurrentVertex.lightIntensity += calculateIlluminatingFactor(theCurrentLight, theCurrentVertex);
							}
						}
					}
				}
			}
		}
	}

	private float calculateIlluminatingFactor(LightSource theCurrentLight, Vertex theCurrentVertex) {
		GVector theDirectionToPolygon = new GVector(theCurrentVertex.myPoint, theCurrentLight.getCamLocation());
		float distance = theDirectionToPolygon.length();
		theDirectionToPolygon.normalize();
		float lightningFactor = theDirectionToPolygon.dotProdukt(theCurrentVertex.normal) *  theCurrentLight.getIntensity() / distance;
		//float lightningFactor = theDirectionToPolygon.dotProdukt(theCurrentVertex.normal);
		if(lightningFactor < 0) lightningFactor = 0;
		return lightningFactor;
	}
}
