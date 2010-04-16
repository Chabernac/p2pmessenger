/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class TriggeredQueueReaderTest extends TestCase{
   public void testMultiThreadedPut() throws InterruptedException{
     TriggeringQueue theQueue = new TriggeringQueue(new ArrayQueue(200));
     List theReputList = new ArrayList< Object >();
     TriggeredQueueReader theReader = new TriggeredQueueReader(theQueue, new ObjectProcessor(theReputList));
     
     ExecutorService theService = Executors.newFixedThreadPool( 20 );
     
     int theTimes = 20000;
     
     for(int i=0;i<theTimes;i++){
//       System.out.println("Putting object: " + i);
       theService.execute( new QueuePutter(theQueue, "Object: " + i) );
     }
     
     Thread.sleep(5000);
     
     theReader.waitTillFinished(20, TimeUnit.SECONDS);
     
     assertEquals( 0, theQueue.size() );
     assertEquals( theTimes, theReputList.size());
     
//     Thread.sleep( 5000 );
     
   }
   
   private class QueuePutter implements Runnable{
     private Object myObject = null;
     private iQueue myQueue = null;
     
     public QueuePutter(iQueue aQueue, Object anObject){
       myQueue = aQueue;
       myObject = anObject;
     }
     
     public void run(){
       myQueue.put(myObject);
     }
   }
   
   private class ObjectProcessor implements iObjectProcessor{
     private List myReputList = null;
     
     public ObjectProcessor(List aReputList){
       myReputList = aReputList;
     }

    @Override
    public void processObject( Object anObject ) {
      System.out.println(anObject);
      myReputList.add(anObject);
    }
     
   }

}
