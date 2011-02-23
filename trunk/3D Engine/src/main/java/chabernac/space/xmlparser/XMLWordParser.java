/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space.xmlparser;

import java.awt.Color;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import chabernac.space.Camera;
import chabernac.space.LightSource;
import chabernac.space.ShapeFactory;
import chabernac.space.Vertex;
import chabernac.space.World;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Shape;

public class XMLWordParser extends DefaultHandler{
  private final World myWorld;
  private Camera myCamera;
  private Point3D myLocation;
  private LightSource myLightSource;
  private Color myColor;
  private Shape myShape;
  private Polygon myPolygon;
  private Cube myCube;
  private Dimension myDimension;

  public XMLWordParser( World aWorld ) {
    super();
    myWorld = aWorld;
  }

  public void read(InputStream anInputStream) throws XMLWordParserException{
    try{
      SAXParserFactory theFactory = SAXParserFactory.newInstance();
      SAXParser theParser = theFactory.newSAXParser();

      theParser.parse( anInputStream, this );
    }catch(Exception e){
      throw new XMLWordParserException("An error occured while trying to read world", e);
    }
  }

  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if(qName.equalsIgnoreCase( "camera" )){
      myCamera = new Camera();
    } else if(qName.equalsIgnoreCase( "location" )){
      extractLocation(attributes);
    } else if(qName.equalsIgnoreCase( "lightsource" )){
      myLightSource = new LightSource( null, 0 );
    } else if(qName.equalsIgnoreCase( "color" )){
      extractColor(attributes);
    } else if(qName.equalsIgnoreCase( "intensity" )){
      myLightSource.setIntensity( Float.parseFloat( attributes.getValue( 0 ) ));
    } else if(qName.equalsIgnoreCase( "shape" )){
      myShape = new Shape( 1 );
    } else if(qName.equalsIgnoreCase( "polygon" )){
      myPolygon = new Polygon( 1 );
    } else if(qName.equalsIgnoreCase( "vertex" )){
      extractLocation(attributes);
    } else if(qName.equalsIgnoreCase( "texture" )){
      if(myPolygon != null) myPolygon.setTexture( attributes.getValue( 0 ) );
      if(myCube != null) myCube.myTexture = attributes.getValue( 0 );
    } else if(qName.equalsIgnoreCase( "cube" )){
      myCube = new Cube();
    } else if(qName.equalsIgnoreCase( "dimensions" )){
      extractDimensions(attributes);
    }
    
    
    System.out.println("Starting element " + uri + " " + localName + " " + qName + " " + attributes);
  }


  private void extractLocation( Attributes anAttributes ) {
    float x=0,y=0,z=0;
    for(int i=0;i<anAttributes.getLength();i++){
      String theName = anAttributes.getLocalName( i );
      if("x".equalsIgnoreCase( theName )) x = Float.parseFloat( anAttributes.getValue( i ));
      else if("y".equalsIgnoreCase( theName )) y = Float.parseFloat( anAttributes.getValue( i ));
      else if("z".equalsIgnoreCase( theName )) z = Float.parseFloat( anAttributes.getValue( i ));
    }
    myLocation = new Point3D( x, y, z );
  }
  
  private void extractColor( Attributes anAttributes ) {
    int red=0,green=0,blue=0;
    for(int i=0;i<anAttributes.getLength();i++){
      String theName = anAttributes.getLocalName( i );
      if("red".equalsIgnoreCase( theName )) red = Integer.parseInt( anAttributes.getValue( i ));
      else if("green".equalsIgnoreCase( theName )) green = Integer.parseInt( anAttributes.getValue( i ));
      else if("blue".equalsIgnoreCase( theName )) blue = Integer.parseInt( anAttributes.getValue( i ));
    }
    myColor = new Color( red, green, blue );
  }
  
  private void extractDimensions( Attributes anAttributes ) {
    float width=0,height=0,depth=0;
    for(int i=0;i<anAttributes.getLength();i++){
      String theName = anAttributes.getLocalName( i );
      if("width".equalsIgnoreCase( theName )) width = Integer.parseInt( anAttributes.getValue( i ));
      else if("height".equalsIgnoreCase( theName )) height = Integer.parseInt( anAttributes.getValue( i ));
      else if("depth".equalsIgnoreCase( theName )) depth = Integer.parseInt( anAttributes.getValue( i ));
    }
    myDimension = new Dimension();
    myDimension.width = width;
    myDimension.heigth = height;
    myDimension.depth = depth;
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    System.out.println("characters: " + new String(ch, start, length) + " " + start + " " + length);
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    if(qName.equalsIgnoreCase( "camera" )){
      myCamera.setLocation( myLocation );
    }else if(qName.equalsIgnoreCase( "lightsource" )){
      myLightSource.setLocation( myLocation );
    }else if(qName.equalsIgnoreCase( "vertex" )){
      myPolygon.addVertex( new Vertex( myLocation ) );
    }else if(qName.equalsIgnoreCase( "vertexes" )){
      myPolygon.done();
    }else if(qName.equalsIgnoreCase( "world" )){
      myWorld.done();
    }else if(qName.equalsIgnoreCase( "shape" )){
      myShape.done();
      myWorld.addShape( myShape );
      myShape = null;
    }else if(qName.equalsIgnoreCase( "polygon" )){
      myShape.addPolygon( myPolygon );
    }else if(qName.equalsIgnoreCase( "cube" )){
      myCube.myLocation = myLocation;
      Shape theCube = ShapeFactory.makeCube( myCube.myLocation, myCube.myDimension.width, myCube.myDimension.heigth, myCube.myDimension.depth );
      theCube.setTexture( myCube.myTexture );
      theCube.done();
      myWorld.addShape( theCube );
      myCube = null;
    }else if(qName.equalsIgnoreCase( "dimensions" )){
      myCube.myDimension = myDimension;
    }
    System.out.println("Ending element " + uri + " " + localName + " " + qName );
  }

  public Camera getCamera() {
    return myCamera;
  }

  public World getWorld() {
    return myWorld;
  }
  
  private class Cube{
    public Point3D myLocation;
    public Dimension myDimension;
    public String myTexture;
  }
  
  private class Dimension{
    public float width, heigth, depth;
  }
}
