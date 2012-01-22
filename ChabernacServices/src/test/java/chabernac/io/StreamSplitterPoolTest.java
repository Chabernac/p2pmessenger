/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;

import junit.framework.TestCase;

public class StreamSplitterPoolTest extends TestCase {
  private ServerSocket myServerSocket = null;
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void setUp() throws Exception{
    myServerSocket = new ServerSocket(21305);
  }
  
  public void tearDown(){
    if(myServerSocket != null){
      try {
        myServerSocket.close();
      } catch ( IOException e ) {
      }
    }
  }
  
  public void testStreamSplitter() throws IOException, InterruptedException{
    final int theFactor1 = 2;
    final int theFactor2 = 3;

    final int runs = 5000;

    final CountDownLatch theLatch1 = new CountDownLatch(runs);
    final CountDownLatch theLatch2 = new CountDownLatch(runs);
    
    final StreamSplitterPool thePool1 = new StreamSplitterPool( "1" );
    final StreamSplitterPool thePool2 = new StreamSplitterPool( "2" );
    
    ExecutorService theService =  Executors.newFixedThreadPool(2);
    theService.execute(new Runnable(){
      public void run(){
        try{
          Socket theSocket = myServerSocket.accept();
          StreamSplitter theSplitter = new StreamSplitter(theSocket.getInputStream(), theSocket.getOutputStream(), new MultiplyHandler(theFactor1));
          assertEquals("2", thePool1.add( theSplitter ));
          testStreamSplitter(thePool1, "2", runs, theFactor2, theLatch1);
          theLatch1.await(5, TimeUnit.SECONDS);
          theLatch2.await(5, TimeUnit.SECONDS);
        }catch(Exception e){
          e.printStackTrace();
        }
      }
    });

    theService.execute(new Runnable(){
      public void run(){
        try{
          Socket theSocket = new Socket("localhost", 21305);
          StreamSplitter theSplitter = new StreamSplitter(theSocket.getInputStream(), theSocket.getOutputStream(), new MultiplyHandler(theFactor2));
          assertEquals("1",thePool2.add( theSplitter ));
          testStreamSplitter(thePool2, "1", runs, theFactor1, theLatch2);
          theLatch1.await(5, TimeUnit.SECONDS);
          theLatch2.await(5, TimeUnit.SECONDS);
        }catch(Exception e){
          e.printStackTrace();
        }
      }
    });
    
    theLatch1.await(5, TimeUnit.SECONDS);
    theLatch2.await(5, TimeUnit.SECONDS);
    assertEquals(0, theLatch1.getCount());
    assertEquals(0, theLatch2.getCount());

    assertEquals(1, thePool1.getStreamSplitters().size());
    assertEquals(1, thePool2.getStreamSplitters().size());
    //close on one side, an it should be closed on the other too
    thePool1.closeAll();
    Thread.sleep( 200 );
    assertEquals(0, thePool1.getStreamSplitters().size());
    assertEquals(0, thePool2.getStreamSplitters().size());
    
    
  }
  
  private void testStreamSplitter(StreamSplitterPool aSplitterPool, String aDestination, int aRuns, int anExpectedFactor, CountDownLatch aLatch) throws InterruptedException, IOException{
    for(int i=0;i<aRuns;i++){
      String theExpectedResult = Integer.toString(i * anExpectedFactor);
      String theResult = aSplitterPool.send(aDestination, Integer.toString(i));
//      System.out.println("Input '" + i + "' output: '" + theResult + "' expected '" + theExpectedResult + "'");
      if(theResult.equals(theExpectedResult)){
        aLatch.countDown();
      }
    }
  }
  
  private class MultiplyHandler implements iInputOutputHandler{
    private final int myFactor;

    public MultiplyHandler(int aFactor) {
      super();
      myFactor = aFactor;
    }

    @Override
    public String handle(String anInput) {
      return Integer.toString(Integer.parseInt(anInput) * myFactor);
    }
  }
}
