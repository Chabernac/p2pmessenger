/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.util.List;

import junit.framework.TestCase;

public class PoolTest extends TestCase {
  public void testPool() throws InterruptedException{
    Pool< String > thePool = new Pool< String >();
    
    thePool.add("a");
    assertTrue( thePool.contains( "a" ) );
    assertEquals( 1, thePool.size());
    
    thePool.remove( "a" );
    assertFalse( thePool.contains( "a" ) );
    assertEquals( 0, thePool.size());
    
    thePool.add("a");
    Thread.sleep( 1000 );
    thePool.add("b");
    assertEquals( 2, thePool.size());
    List<String> theOldItems = thePool.getItemsOlderThan( System.currentTimeMillis() - 500 );
    
    assertEquals( 1, theOldItems.size() );
    assertTrue( theOldItems.contains( "a" ) );
  }
}
