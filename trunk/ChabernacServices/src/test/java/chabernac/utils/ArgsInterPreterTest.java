/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import junit.framework.TestCase;

public class ArgsInterPreterTest extends TestCase {
  
  public void testInterPreter(){
    ArgsInterPreter theInterpreter = new ArgsInterPreter(new String[]{"show", "visible=true", "hidden=false"});
    assertTrue( theInterpreter.containsKey( "show" ) );
    assertTrue( theInterpreter.containsKey( "visible" ) );
    assertTrue( theInterpreter.containsKey( "hidden" ) );
    
    assertEquals( null, theInterpreter.getKeyValue( "show" ) );
    assertEquals( "true", theInterpreter.getKeyValue( "visible" ) );
    assertEquals( "false", theInterpreter.getKeyValue( "hidden" ) );
    
    assertEquals( "abc", theInterpreter.getKeyValue( "dummy", "abc" ) );
    assertEquals( "true", theInterpreter.getKeyValue( "visible", "abc" ) );
  }
  
  
  
}
