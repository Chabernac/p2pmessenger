/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import junit.framework.TestCase;

public class TestToolsTest extends TestCase {
  public void testTestTools(){
    assertTrue( TestTools.isInUnitTest() );
    
    TestTools.setIsUnitTest( false );
    
    assertFalse(  TestTools.isInUnitTest() );
    
    TestTools.setIsUnitTest( true);
    
    assertTrue(  TestTools.isInUnitTest() );
  }
}
