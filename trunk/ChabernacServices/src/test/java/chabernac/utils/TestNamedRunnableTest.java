/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class TestNamedRunnableTest extends TestCase {
  public void testNamedRunnable() throws InterruptedException{
    ExecutorService theService = Executors.newCachedThreadPool();
    
    int runs = 1000;
    CountDownLatch theLatch = new CountDownLatch( runs );
    for(int i=0;i<runs;i++){
      theService.execute( new MyNamedRunnable( theLatch ) );
    }
    theLatch.await( 5, TimeUnit.SECONDS );
    assertEquals( 0, theLatch.getCount() );
  }
  
  private class MyNamedRunnable extends NamedRunnable{
    private final CountDownLatch myLatch;

    public MyNamedRunnable ( CountDownLatch aLatch ) {
      super();
      myLatch = aLatch;
    }

    @Override
    protected void doRun() {
      myLatch.countDown();
    }
    
  }
}
