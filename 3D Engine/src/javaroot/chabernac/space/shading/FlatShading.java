package chabernac.space.shading;

import java.awt.Color;
import java.util.ArrayList;

import chabernac.space.LightSource;
import chabernac.space.Polygon;
import chabernac.space.Shape;
import chabernac.space.World;
import chabernac.space.geom.GVector;

public class FlatShading implements iLightManager{
	private double myAmbient = 0;
	
	public FlatShading(double ambient){
		myAmbient = ambient;
	}
	

	public void calculateLight(World aWorld) {
		LightSource theCurrentLight = null;
		Shape theCurrentShape = null;
		Polygon theCurrentPolygon = null;
		ArrayList theLightSources = aWorld.getLightSources();
		double illuminatingFactor = 0;
		
		
		for(int j=0;j<aWorld.myShapes.length;j++){
			theCurrentShape = aWorld.myShapes[j];
			for(int k=0;k<theCurrentShape.myPolygons.length;k++){
				theCurrentPolygon = theCurrentShape.myPolygons[k];
				if(theCurrentPolygon.c.length > 0)	{
					illuminatingFactor = myAmbient;
					for(int i=0;i<theLightSources.size();i++){
						theCurrentLight = (LightSource)theLightSources.get(i);
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
		Color theCurrentColor = theCurrentPolygon.color;
		int red = (int)((theCurrentColor.getRed()) * illuminatingFactor) ;
		int green = (int)((theCurrentColor.getGreen()) * illuminatingFactor) ;
		int blue = (int)((theCurrentColor.getBlue()) * illuminatingFactor) ;
		if(red > 255) red = 255;
		if(green > 255) green = 255;
		if(blue > 255) blue = 255;
		if(red < 0) red = 0;
		if(green < 0) green = 0;
		if(blue < 0) blue = 0;
		theCurrentPolygon.lightedColor = new Color(red, green, blue );
	}
}
