package chabernac.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class StreamSplitterTest extends TestCase {
  public void testStreamSplitter() throws IOException, InterruptedException{
    final ServerSocket theServSocket = new ServerSocket(21300);

    final int theFactor1 = 2;
    final int theFactor2 = 3;

    final int runs = 5000;

    final CountDownLatch theLatch1 = new CountDownLatch(runs);
    final CountDownLatch theLatch2 = new CountDownLatch(runs);
    
    CountDownLatch theCloseLatch1 = new CountDownLatch(runs);
    CountDownLatch theCloseLatch2 = new CountDownLatch(runs);

    final StreamListener theListener1 = new StreamListener(theLatch1);
    final StreamListener theListener2 = new StreamListener(theLatch2);
    
    
    ExecutorService theService =  Executors.newFixedThreadPool(2);
    theService.execute(new Runnable(){
      public void run(){
        try{
          Socket theSocket = theServSocket.accept();
          StreamSplitter theSplitter = new StreamSplitter(theSocket.getInputStream(), theSocket.getOutputStream(), new MultiplyHandler(theFactor1));
          theSplitter.addStreamListener(theListener1);
          theSplitter.sendWithoutReply("test1");
          assertEquals("test2", theSplitter.readLine());
          theSplitter.startSplitting();
          testStreamSplitter(theSplitter, runs, theFactor2, theLatch1);
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
          theSplitter.startSplitting();
          testStreamSplitter(theSplitter, runs, theFactor1, theLatch2);
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
    theCloseLatch1.await(5, TimeUnit.SECONDS);
    theCloseLatch2.await(5, TimeUnit.SECONDS);
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
