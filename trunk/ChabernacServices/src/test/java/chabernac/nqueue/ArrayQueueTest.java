/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.nqueue;

import org.apache.log4j.BasicConfigurator;

import chabernac.queue.ArrayQueue;
import junit.framework.TestCase;

public class ArrayQueueTest extends TestCase {
  static{
    BasicConfigurator.configure();
  }
  
  public void testArrayQueueOverload(){
    ArrayQueue theQueue = new ArrayQueue(5, true);
    
    for(int i=0;i<10;i++){
      theQueue.put( i );
    }
    
    assertEquals( 5, theQueue.size() );
    for(int i=5;i<10;i++){
      assertEquals( i, theQueue.get() );
    }
  }
}
