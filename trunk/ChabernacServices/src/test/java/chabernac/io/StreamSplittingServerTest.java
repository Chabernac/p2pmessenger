/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class StreamSplittingServerTest extends TestCase {
  private static Logger LOGGER = Logger.getLogger(StreamSplittingServerTest.class);
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testCloseStreamSplittingServerOnOneSide() throws IOException, InterruptedException{

    StreamSplittingServer theServer1 = new StreamSplittingServer( new MultiplyHandler( 2 ), 13000, false, "1" );
    StreamSplittingServer theServer2 = new StreamSplittingServer( new MultiplyHandler( 3 ), 13001, false, "2" );
    theServer1.start();
    theServer2.start();
    assertTrue(theServer1.isStarted());
    assertTrue(theServer2.isStarted());

    try{
      assertEquals( Integer.toString(5 * 3), theServer1.send( "localhost", 13001, Integer.toString(5) ).getReply());
      assertEquals( Integer.toString(5 * 2), theServer2.send( "localhost", 13000, Integer.toString(5) ).getReply());
      
      assertTrue( theServer1.containsSocketForId( "2" ) );
      assertTrue( theServer2.containsSocketForId( "1" ) );
      
      theServer1.close();
      //the connection between peer 1 and 2 should be closed, both peers should have no connections left in the pool
      
      Thread.sleep( 3000 );
      
      assertFalse( theServer1.containsSocketForId( "2" ) );
      assertFalse( theServer2.containsSocketForId( "1" ) );
      
    }finally{
      theServer1.close();
      theServer2.close();
    }

  }

  public void testStreamSplittingServer() throws IOException, InterruptedException{

    StreamSplittingServer theServer1 = new StreamSplittingServer( new MultiplyHandler( 2 ), 13000, false, "1" );
    StreamSplittingServer theServer2 = new StreamSplittingServer( new MultiplyHandler( 3 ), 13001, false, "2" );
    theServer1.start();
    theServer2.start();
    assertTrue(theServer1.isStarted());
    assertTrue(theServer2.isStarted());

    try{
      int times = 10000;
      assertEquals( Integer.toString(5 * 3), theServer1.send( "localhost", 13001, Integer.toString(5) ).getReply());
      assertEquals( Integer.toString(5 * 2), theServer2.send( "localhost", 13000, Integer.toString(5) ).getReply());
      for(int i=0;i<times;i++){
        assertEquals( Integer.toString(i * 3), theServer1.send( "2", Integer.toString(i) ));
        assertEquals( Integer.toString(i * 2), theServer2.send( "1", Integer.toString(i) ));
      }
    }finally{
      theServer1.close();
      theServer2.close();
      Thread.sleep(1000);
      assertFalse(theServer1.isStarted());
      assertFalse(theServer2.isStarted());
    }
  }

  public void testSimultanousConnectionAttempt() throws InterruptedException{
    final CyclicBarrier theBarrier = new CyclicBarrier( 2 );

    final int times = 20;

    ExecutorService theExecutorService= Executors.newFixedThreadPool( 2 );
    
    final CountDownLatch theLatch1 = new CountDownLatch( times );
    final CountDownLatch theLatch2 = new CountDownLatch( times );

    theExecutorService.execute( new Runnable(){
      public void run(){
        for(int i=0;i<times;i++){
          StreamSplittingServer theServer = new StreamSplittingServer( new MultiplyHandler( 2 ), 13000, false, "1" );
          theServer.start();
          try {
            theBarrier.await();
            assertEquals( Integer.toString(5 * 3), theServer.send( "localhost", 13001, Integer.toString(5) ).getReply());
            theLatch1.countDown();
            theBarrier.await();
          } catch ( Exception e ) {
            LOGGER.error("Error occured while sending", e);
          }
          theServer.close();
        }
      }
    });
    
    theExecutorService.execute( new Runnable(){
      public void run(){
        for(int i=0;i<times;i++){
          StreamSplittingServer theServer = new StreamSplittingServer( new MultiplyHandler( 3 ), 13001, false, "2" );
          theServer.start();
          try {
            theBarrier.await();
            assertEquals( Integer.toString(5 * 2), theServer.send( "localhost", 13000, Integer.toString(5) ).getReply());
            theLatch2.countDown();
            theBarrier.await();
          } catch ( Exception e ) {
            LOGGER.error("Error occured while sending");
          }
          theServer.close();
        }
      }
    });
    
    theLatch1.await( 5, TimeUnit.SECONDS );
    theLatch2.await( 5, TimeUnit.SECONDS );
    
    assertEquals( 0, theLatch1.getCount() );
    assertEquals( 0, theLatch2.getCount() );
  }
  
  public void testGetRemoteId() throws IOException{
    StreamSplittingServer theServer1 = new StreamSplittingServer( new MultiplyHandler( 2 ), 13000, false, "1" );
    StreamSplittingServer theServer2 = new StreamSplittingServer( new MultiplyHandler( 3 ), 13001, false, "2" );
    theServer1.start();
    theServer2.start();
    assertTrue(theServer1.isStarted());
    assertTrue(theServer2.isStarted());

    try{
      assertEquals( "2",theServer1.getRemoteId( "localhost", 13001 ));
//      assertEquals( "1",theServer1.getRemoteId( "localhost", 13000 ));
//      assertEquals( "2",theServer2.getRemoteId( "localhost", 13001 ));
      assertEquals( "1",theServer2.getRemoteId( "localhost", 13000 ));
      
    }finally{
      theServer1.close();
      theServer2.close();
    }    
  }
}
