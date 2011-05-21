package chabernac.comet;

import junit.framework.TestCase;

public class CometEventTest extends TestCase {
  public void testCometEvent() throws CometException{
    final CometEvent theCometEvent = new CometEvent("1", "input");
    new Thread(new Runnable(){
      public void run(){
        try {
          theCometEvent.setOutput("output");
        } catch (CometException e) {
        }
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

  public void testCometEventExpirationListener() throws CometException{
    CometEventExpirationCounter theCounter = new CometEventExpirationCounter();
    assertEquals(0, theCounter.getCounter());

    CometEvent theEvent = new CometEvent("1", "input");
    theEvent.addExpirationListener(theCounter);
    try{
      theEvent.getOutput(1);
    }catch(Exception e){}
    assertEquals(1, theCounter.getCounter());

    theEvent = new CometEvent("2", "input");
    theEvent.addExpirationListener(theCounter);
    try{
      theEvent.getOutput(1);
    }catch(Exception e){}
    assertEquals(2, theCounter.getCounter());
  }

  public void testCometEventExpired(){
    CometEvent theEvent = new CometEvent("1", "input");
    assertFalse(theEvent.isExpired());
    try{
      theEvent.getOutput(1);
    }catch(Exception e){}
    assertTrue(theEvent.isExpired());
    
    try{
      theEvent.getOutput(1);
      fail("the event is expired should not get here");
    }catch(Exception e){
    }
    
    try{
      theEvent.setOutput("output");
      fail("the event is expired should not get here");
    }catch(Exception e){
    }
    
    try{
      theEvent.setOutput(new CometException("test"));
      fail("the event is expired should not get here");
    }catch(Exception e){
    }
  }

  public void testCometEventExceptionOutput() throws CometException{
    CometEvent theEvent = new CometEvent("1", "input");
    theEvent.setOutput(new CometException("test comet exception"));
    try{
      theEvent.getOutput(1);
      fail("Should not get here");
    }catch(CometException e){
      assertEquals("test comet exception", e.getMessage());
    }
  }
  
  public void testCometEventGetters(){
    CometEvent theEvent = new CometEvent("1", "input");
    assertEquals("input", theEvent.getInput());
    assertEquals("1", theEvent.getId());
    assertTrue(theEvent.getCreationTime() > 0);
    assertTrue(theEvent.getCreationTime() <= System.currentTimeMillis());
  }

  private class CometEventExpirationCounter implements iCometEventExpirationListener{
    private int myCounter = 0;

    @Override
    public void cometEventExpired(CometEvent anEvent) {
      myCounter++;
    }

    public int getCounter(){
      return myCounter;
    }

  }
}
