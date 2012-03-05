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

public class StreamSplitterTest extends TestCase {
  private ServerSocket myServerSocket = null;
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void setUp() throws Exception{
    myServerSocket = new ServerSocket(21300);
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
    
    CountDownLatch theCloseLatch1 = new CountDownLatch(1);
    CountDownLatch theCloseLatch2 = new CountDownLatch(1);

    final StreamListener theListener1 = new StreamListener(theCloseLatch1);
    final StreamListener theListener2 = new StreamListener(theCloseLatch2);
    
    final ExecutorService theService =  Executors.newCachedThreadPool();
    theService.execute(new Runnable(){
      public void run(){
        try{
          Socket theSocket = myServerSocket.accept();
          StreamSplitter theSplitter = new StreamSplitter(theSocket.getInputStream(), theSocket.getOutputStream(), new MultiplyHandler(theFactor1));
          theSplitter.addStreamListener(theListener1);
          theSplitter.sendWithoutReply("test1");
          assertEquals("test2", theSplitter.readLine());
          theSplitter.startSplitting(theService);
          testStreamSplitter(theSplitter, runs, theFactor2, theLatch1);
          theLatch1.await(5, TimeUnit.SECONDS);
          theLatch2.await(5, TimeUnit.SECONDS);
          theSplitter.close();
        }catch(Exception e){
          e.printStackTrace();
        }
      }
    });

    theService.execute(new Runnable(){
      public void run(){
        try{
          Socket theSocket = new Socket("localhost", 21300);
          StreamSplitter theSplitter = new StreamSplitter(theSocket.getInputStream(), theSocket.getOutputStream(), new MultiplyHandler(theFactor2));
          theSplitter.addStreamListener(theListener2);
          theSplitter.sendWithoutReply("test2");
          assertEquals("test1", theSplitter.readLine());
          theSplitter.startSplitting(theService);
          testStreamSplitter(theSplitter, runs, theFactor1, theLatch2);
          theLatch1.await(5, TimeUnit.SECONDS);
          theLatch2.await(5, TimeUnit.SECONDS);
          theSplitter.close();
        }catch(Exception e){
          e.printStackTrace();
        }
      }
    });
    
    theLatch1.await(5, TimeUnit.SECONDS);
    theLatch2.await(5, TimeUnit.SECONDS);
    assertEquals(0, theLatch1.getCount());
    assertEquals(0, theLatch2.getCount());
    theCloseLatch1.await(10, TimeUnit.SECONDS);
    theCloseLatch2.await(10, TimeUnit.SECONDS);
    assertEquals(0, theCloseLatch1.getCount());
    assertEquals(0, theCloseLatch2.getCount());
  }
  
  private void testStreamSplitter(StreamSplitter aSplitter, int aRuns, int anExpectedFactor, CountDownLatch aLatch) throws InterruptedException{
    for(int i=0;i<aRuns;i++){
      String theExpectedResult = Integer.toString(i * anExpectedFactor);
      String theResult = aSplitter.send(Integer.toString(i));
//      System.out.println("Input '" + i + "' output: '" + theResult + "' expected '" + theExpectedResult + "'");
      if(theResult.equals(theExpectedResult)){
        aLatch.countDown();
      }
    }
  }
  
  private class StreamListener implements iStreamListener{
    private final CountDownLatch myLatch;
    
    public StreamListener(CountDownLatch myLatch) {
      super();
      this.myLatch = myLatch;
    }

    @Override
    public void streamClosed() {
      myLatch.countDown();
    }
    
  }
}
