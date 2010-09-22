package chabernac.comet;

import junit.framework.TestCase;

public class CometEventTest extends TestCase {
  public void testCometEvent() throws CometException{
    final CometEvent theCometEvent = new CometEvent("1", "input");
    new Thread(new Runnable(){
      public void run(){
        theCometEvent.setOutput("output");
      }
    }).start();

    String theOutput = theCometEvent.getOutput(2000);
    assertEquals("output", theOutput);

    CometEvent theCometEvent2 = new CometEvent("1", "input");
    try{
      theCometEvent2.getOutput(1);
      fail("An error must have occured");
    }catch(Exception e){
    }
  }
}
