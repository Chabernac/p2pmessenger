/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.lock;

import junit.framework.TestCase;

public class FileLockTest extends TestCase {
  public void testFileLock(){
    iLock theLock = new FileLock("test");
    assertFalse( theLock.isLocked() );
    assertTrue( theLock.createLock() );
    assertTrue( theLock.isLocked() );
    
    iLock theLock2 = new FileLock("test");
    assertTrue( theLock2.isLocked() );
    assertFalse( theLock2.removeLock() );
    
    assertTrue( theLock.removeLock() );
    assertFalse( theLock.isLocked() );
    
    assertFalse( theLock2.isLocked() );
    assertTrue( theLock2.createLock() );
    assertTrue( theLock2.isLocked() );
    assertTrue( theLock2.removeLock() );
  }
  
  public void testShutDownHook(){
   iLock theLock = new FileLock("shutdownhook");
   assertTrue( theLock.createLock() );
   //you must mannually check if the file shutdownhook.lock has been removed from the system.
  }
}
