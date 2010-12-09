/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.worlds;

import java.awt.Color;
import java.awt.Dimension;

import chabernac.math.MatrixOperations;
import chabernac.space.ShapeFactory;
import chabernac.space.Transformation;
import chabernac.space.World;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;
import chabernac.space.geom.Shape;

public class CrossingPlanes extends AbstractWorld{

  private static final long serialVersionUID = -6073586440715690547L;

  @Override
  protected void buildWorld( World aWorld ) {
    Shape thePlane1 = ShapeFactory.makeSinglePolygonShape(new Point3D( -100, -100, 300 ), 200,200);
    thePlane1.setColor( Color.blue );
    
    Shape thePlane2 = ShapeFactory.makeSinglePolygonShape(new Point3D( -100, -100, 200 ), 200,200);
    thePlane2.setColor( Color.red );
    
    Shape thePlane3 = ShapeFactory.makeSinglePolygonShape(new Point3D( 100, 0, 200 ), 200,200);
    thePlane3.setColor( Color.green );
    
    Transformation theTransform = new Transformation();
    theTransform.addTransformation(MatrixOperations.buildTranslationMatrix( new GVector( 100, 100, -300 ) ));
    theTransform.addTransformation(MatrixOperations.buildRotationMatrix(new Rotation( 0, -(float)Math.PI / 8, 0 )));
    theTransform.addTransformation(MatrixOperations.buildTranslationMatrix( new GVector( -100, -100, +300 ) ));
    thePlane1.translate( theTransform );
    
    theTransform = new Transformation();
    theTransform.addTransformation(MatrixOperations.buildTranslationMatrix( new GVector( 100, 100, -300 ) ));
    theTransform.addTransformation(MatrixOperations.buildRotationMatrix(new Rotation( 0, (float)Math.PI / 8, 0 )));
    theTransform.addTransformation(MatrixOperations.buildTranslationMatrix( new GVector( -100, -100, +300 ) ));
    thePlane2.translate( theTransform );
    
    theTransform = new Transformation();
    theTransform.addTransformation(MatrixOperations.buildTranslationMatrix( new GVector( -100, 0, -300 ) ));
    theTransform.addTransformation(MatrixOperations.buildRotationMatrix(new Rotation( 0, 0, (float)Math.PI / 2 )));
    theTransform.addTransformation(MatrixOperations.buildTranslationMatrix( new GVector( 100, 0, +300 ) ));
    thePlane3.translate( theTransform );
    
    aWorld.addShape( thePlane1);
    aWorld.addShape( thePlane2);
    aWorld.addShape( thePlane3);
  }

  @Override
  protected Dimension getPanelSize() {
    return new Dimension( 300, 300 );
  }
  
  public static void main(String[] args){
    new CrossingPlanes();
  }

}
