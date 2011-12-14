package chabernac.comet;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import chabernac.newcomet.EndPoint2;

public class EndPoint2Test extends TestCase{
  public void testEndPoint() throws InterruptedException{
    final EndPoint2 theEndPoint = new EndPoint2("1");

    
    int times = 100000;
    int theNrOfCometEvents = 5;
    
    final CountDownLatch theEventCounter = new CountDownLatch(1);
    final CountDownLatch theExceptionCounter = new CountDownLatch(times - 1);

    ExecutorService theExecutor = Executors.newFixedThreadPool(30);

    final CountDownLatch theLatch = new CountDownLatch(times);
    
    final ArrayList<CometEvent> theEventContainer = new ArrayList<CometEvent>();
    
    for(int i=0;i<times;i++){
      theExecutor.execute(new Runnable(){
        public void run(){
          try {
            theLatch.countDown();
            theEndPoint.waitForEvent(this);
            while(theEndPoint.hasEvents()){
              theEventContainer.add(theEndPoint.getFirstEvent());
            }
            theEventCounter.countDown();
          } catch (InterruptedException e) {
            theExceptionCounter.countDown();
          } 
        }
      });
    }
    
    theLatch.await(3, TimeUnit.SECONDS);
    assertEquals(0, theLatch.getCount());
    
    //now fire one event
    for(int i=0;i<theNrOfCometEvents;i++){
      theEndPoint.addCometEvent(new CometEvent(Integer.toString(i), "input" + i));
    }
    
    theExceptionCounter.await(2, TimeUnit.SECONDS);
    theEventCounter.await(1, TimeUnit.SECONDS);

    
    //there can only be one owner of the endpoint, so we should have had times - 1 exceptions and 1 successfull get
    
    assertEquals(0, theExceptionCounter.getCount());
    assertEquals(0, theEventCounter.getCount());
    assertEquals(theNrOfCometEvents, theEventContainer.size());
  }
}
