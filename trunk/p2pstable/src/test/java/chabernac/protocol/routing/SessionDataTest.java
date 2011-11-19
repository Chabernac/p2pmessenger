/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import junit.framework.TestCase;

public class SessionDataTest extends TestCase {
  public void testSessionData(){
    SessionData theData = new SessionData();
    
    theData.putProperty( "1", "name", "Guy" );
    theData.putProperty( "1", "surname", "Chauliac" );
    theData.putProperty( "2", "name", "Leslie" );
    theData.putProperty( "2", "surname", "Torreele" );
    
    assertEquals( "Guy", theData.getProperty( "1", "name" ) );
    assertEquals( "Chauliac", theData.getProperty( "1", "surname" ) );
    assertEquals( "Leslie", theData.getProperty( "2", "name" ) );
    assertEquals( "Torreele", theData.getProperty( "2", "surname" ) );
    
    assertNull( theData.getProperty( "1", "dummy" ) );
    assertNull( theData.getProperty( "3", "name" ) );
    
    assertTrue( theData.containsSession( "1" ) );
    assertTrue( theData.containsSession( "2" ) );
    assertFalse( theData.containsSession( "3" ) );
    
    theData.clearSessionData( "1" );
    
    assertFalse( theData.containsSession( "1" ) );
    assertTrue( theData.containsSession( "2" ) );
    assertFalse( theData.containsSession( "3" ) );
  }
}
