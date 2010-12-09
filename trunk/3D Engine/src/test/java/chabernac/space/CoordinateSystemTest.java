/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.space;

import junit.framework.TestCase;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;

public class CoordinateSystemTest extends TestCase {
  public void testCoordinateSystem(){
    //this coordinate system will just shift the points along the axis 
    CoordinateSystem theSystem = new CoordinateSystem(new Point3D(1,1,1), new GVector(1,0,0), new GVector(0,1,0));
    
    assertEquals( new Point3D(-1,-1,-1), theSystem.getTransformator().transform( new Point3D(0,0,0) ));
    assertEquals( new Point3D(0,0,0), theSystem.getTransformator().transform( new Point3D(1,1,1) ));
    assertEquals( new GVector(1,1,1), theSystem.getTransformator().transform( new GVector(1,1,1) ));
    
    
    //now lets rotate the system
    theSystem = new CoordinateSystem(new Point3D(0,0,0), new GVector(0,1,0), new GVector(-1,0,0));
    assertEquals( new Point3D(0,-1,0), theSystem.getTransformator().transform( new Point3D(1,0,0) ));
    assertEquals( new Point3D(1,0,0), theSystem.getTransformator().transform( new Point3D(0,1,0) ));
  }
  
  public void testPerformance(){
    
    CoordinateSystem theSystem = new CoordinateSystem(new Point3D(1,1,1), new GVector(1,0,0), new GVector(0,1,0));
    Transformation theTransform = theSystem.getTransformator();
    
    Point3D thePoint = new Point3D( 5, 6, 7 );
    
    int times = 20000000;

    long t1 = System.currentTimeMillis();
    for(int i=0;i<times;i++){
      Point3D theP = theTransform.transform( thePoint );
      theTransform.inverseTransform( theP );
    }
    long t2 = System.currentTimeMillis();
    
    System.out.println(times  * 2 / (t2-t1)  + " transformation / msecond");
    
  }
}
