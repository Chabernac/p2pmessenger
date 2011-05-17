package chabernac.comet;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

public class EndPointTest extends TestCase {
  public void testEndPoint() throws CometException{
    final EndPoint theEndPoint = new EndPoint("1" );
    
    assertEquals("1", theEndPoint.getId());
    
    ExecutorService theService = Executors.newCachedThreadPool();
    int times = 1000;
    final AtomicLong theLong = new AtomicLong();
    for(int i=0;i<times;i++){
      theService.execute(new Runnable(){
        public void run(){
          try{
            long theCounter = theLong.incrementAndGet();
            theEndPoint.setEvent(new CometEvent(Long.toString(theCounter), "input" + theCounter)); 
          }catch(Throwable e){
            e.printStackTrace();
          }
        }
      });
    }
    for(int i=0;i<times;i++){
      assertNotNull(theEndPoint.getEvent());
    }
    
  }
  
  public void testDestroyEndPoint(){
    final EndPoint theEndPoint = new EndPoint("A");
    AtomicInteger theCounter = new AtomicInteger(1);
    Executors.newScheduledThreadPool(1).schedule(new Runnable(){
      public void run(){
        theEndPoint.destroy();
      }
    }, 1, TimeUnit.SECONDS);
    try{
      theEndPoint.getEvent();
    }catch(CometException e){
      theCounter.decrementAndGet();
    }
    assertEquals(0, theCounter.intValue());
  }
}
