package chabernac.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

public class BufferTest extends TestCase {
  public void testBuffer() throws InterruptedException{
    Buffer<String> theBuffer = new Buffer<String>(2, 5);
    
    List<String> theReadItems = new ArrayList<String>();
    Executors.newSingleThreadExecutor().execute(new BufferReader(theBuffer, theReadItems));
    
    theBuffer.put("1");
    Thread.sleep(50);
    assertEquals(0, theReadItems.size());
    assertEquals(1, theBuffer.size());
    theBuffer.put("2");
    Thread.sleep(50);
    assertEquals(0, theReadItems.size());
    assertEquals(2, theBuffer.size());
    theBuffer.put("3");
    Thread.sleep(50);
    assertEquals(0, theReadItems.size());
    assertEquals(3, theBuffer.size());
    theBuffer.put("4");
    Thread.sleep(50);
    assertEquals(0, theReadItems.size());
    assertEquals(4, theBuffer.size());
    theBuffer.put("5");
    Thread.sleep(50);
    //the buffer is full all items untill lower limit should be read
    assertEquals(3, theReadItems.size());
    assertEquals(2, theBuffer.size());
    assertTrue(theReadItems.contains("1"));
    assertTrue(theReadItems.contains("2"));
    assertTrue(theReadItems.contains("3"));
    //the buffer must now wait again untill it is full before it gives anything
    theBuffer.put("6");
    Thread.sleep(50);
    assertEquals(3, theBuffer.size());
    assertEquals(3, theReadItems.size());
    theBuffer.put("7");
    Thread.sleep(50);
    assertEquals(3, theReadItems.size());
    assertEquals(4, theBuffer.size());
    theBuffer.put("8");
    Thread.sleep(50);
    assertEquals(6, theReadItems.size());
    assertEquals(2, theBuffer.size());
  }
  
  private class BufferReader implements Runnable{
    private final Buffer<String> myBuffer;
    private final List<String> myReadItems;

    public BufferReader(Buffer<String> aBuffer, List<String> anOutputList) {
      super();
      myBuffer = aBuffer;
      myReadItems = anOutputList;
    }
    
    public void run(){
      while(true){
        myReadItems.add(myBuffer.get());
      }
    }
  }

}
