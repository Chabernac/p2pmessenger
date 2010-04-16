
package chabernac.test;

import java.util.Random;

import chabernac.log.Logger;
import chabernac.queue.ArrayQueue;
import chabernac.queue.ObjectDispatcher;
import chabernac.queue.TriggeredQueueReader;
import chabernac.queue.TriggeringQueue;
import chabernac.queue.iObjectProcessor;
import chabernac.queue.iQueue;

public class TestQueue {
  public TestQueue(){
    iQueue theQueue = new ArrayQueue(10);
    TriggeringQueue theTQueue = new TriggeringQueue(theQueue);
    //TriggeredQueuePrinter thePrinter = new TriggeredQueuePrinter(theTQueue);
    ObjectDispatcher theDispatcher = new ObjectDispatcher();
    theDispatcher.setProcessor(String.class, new StringProcessor());
    theDispatcher.setProcessor(String.class, new StringProcessor2());
    theDispatcher.setProcessor(Integer.class, new IntegerProcessor());
    TriggeredQueueReader theReader = new TriggeredQueueReader(theTQueue, 1, theDispatcher);
   
    new Thread(new PutThread(theTQueue)).start();
    /*
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    new Thread(new GetThread(theQueue)).start();
    */
  }

  public static void main(String[] args) {
    Logger.setDebug(true);
    new TestQueue();
  }
  
  private class PutThread implements Runnable{
    private iQueue myQueue = null;
    
    public PutThread(iQueue aQueue){
      myQueue = aQueue;
    }
    
    public void run(){
      Random theRandom = new Random();
      for(int i=0;i<20;i++){
        if(i%2 == 0) myQueue.put(new Integer(i));
        else myQueue.put("Object: " + i);
        try {
          //Thread.sleep(Math.abs(theRandom.nextLong()) % 100);
          Thread.sleep(200);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }
  
  private class GetThread implements Runnable{
    private iQueue myQueue = null;
    
    public GetThread(iQueue aQueue){
      myQueue = aQueue;
    }
    
    public void run(){
      Random theRandom = new Random();
      for(int i=0;i<20;i++){
        Logger.log(this, "Object: " + myQueue.get());
        try {
          //Thread.sleep(Math.abs(theRandom.nextLong()) % 100);
          Thread.sleep(5);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private class ObjectProcessor implements iObjectProcessor{
    public void processObject(Object anObject){
      Logger.log(this, "Object: " + anObject);
      try {
        //Thread.sleep(Math.abs(theRandom.nextLong()) % 100);
        Thread.sleep(1500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private class StringProcessor implements iObjectProcessor{
    public void processObject(Object anObject){
      Logger.log(this,"String received: " + (String)anObject);
    }
  }
  
  private class StringProcessor2 implements iObjectProcessor{
    public void processObject(Object anObject){
      Logger.log(this,"(2) String received: " + (String)anObject);
    }
  }
  
  private class IntegerProcessor implements iObjectProcessor{
    public void processObject(Object anObject){
      Logger.log(this,"Integer received: " + ((Integer)anObject).intValue());
    }
  }
}
