/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.worlds;

import java.awt.Dimension;

import chabernac.space.Camera;
import chabernac.space.World;

public class WorldFrame extends AbstractWorld {
  private static final long serialVersionUID = -3954168740017432083L;
  
  public WorldFrame( World aWorld, Camera aCamera, Dimension aDimensions ) {
    super(aDimensions, aWorld, aCamera);
  }

  @Override
  protected void buildWorld( World aWorld ) {
//    Shape theShape = ShapeFactory.makeCube(new Point3D(5,0,400), 94,94,94);
//    theShape.setColor(new Color(0,0,255,100));
//    theShape.setTexture("axa","guy", false, false);
//    theShape.done();
//    aWorld.addShape( theShape );
    
    myPanel3D.getGraphics3D().setUseClipping( false );
    myPanel3D.getGraphics3D().getGraphics3D2D().setUsePartialClearing( false );
    myPanel3D.getGraphics3D().setDrawRibs( false );
  }
}
