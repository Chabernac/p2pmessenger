/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

public class ThreadPoolTest extends TestCase {
  public void testThreadPool() throws InterruptedException{
    System.out.println(Thread.activeCount());
    ExecutorService theService = Executors.newFixedThreadPool( 100 );
    for(int i=0;i<20;i++){
      theService.execute( new DummyRunnable() );
    }
    System.out.println(Thread.activeCount());
    theService.shutdownNow();
    Thread.sleep( 100 );
    System.out.println(Thread.activeCount());
    
    
  }
  
  private class DummyRunnable implements Runnable{
    public void run(){
//      System.out.println("run");
    }
  }

}
