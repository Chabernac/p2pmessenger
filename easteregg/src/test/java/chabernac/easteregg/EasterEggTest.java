/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.easteregg;

import javax.swing.JFrame;

import junit.framework.TestCase;

public class EasterEggTest extends TestCase {
  public void testMatrix() throws InterruptedException{
    testEasterEgg( "matrix", "The quick brown fox jumps easily over the fat and lazy dog" );
  }
  
  public void testMistify() throws InterruptedException{
    testEasterEgg( "mystify" );
  }
  
  public void test3d() throws InterruptedException{
    testEasterEgg( "3d" );
  }
  
  private void testEasterEgg(String anEgg, String... aParameters) throws InterruptedException{
    JFrame theFrame = new JFrame();
    theFrame.setSize( 300, 300 );
    theFrame.setVisible( true );
    theFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    iEasterEgg theEgg = EasterEggFactory.createEasterEgg( theFrame, anEgg);
    for(String theParameter : aParameters){
      theEgg.setParameter( theParameter );
    }
    theEgg.start();
    Thread.sleep( 500000 );
  }
}
