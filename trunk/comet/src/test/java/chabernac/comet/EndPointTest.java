package chabernac.comet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

public class EndPointTest extends TestCase {
  public void testEndPoint() throws CometException{
    final EndPoint theEndPoint = new EndPoint("1");
    
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
}
