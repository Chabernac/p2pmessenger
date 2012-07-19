/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import chabernac.protocol.message.DeliveryReport.Status;
import chabernac.protocol.routing.DummyPeer;

public class MessageArchiveTest extends TestCase {
  private Random myRandom = new Random();
      
  private MultiPeerMessage myMultiMessage = MultiPeerMessage.createMessage( "test" )
      .addDestination( "1" );
  
  private int myTimes = 1000;
  
  public void testSimultanousDeliveryReportsRetrieval() throws InterruptedException{
    final MessageArchive theMessageArchive = new MessageArchive( );

    //one thread that spawns delivery reports
    final CountDownLatch theSpawningLatch = new CountDownLatch( myTimes );

    final List<Exception> theExceptions = new ArrayList<Exception>();
    
    Executors.newSingleThreadExecutor().execute( new Runnable(){
      public void run(){
        for(int i=0;i<myTimes;i++){
          theMessageArchive.acceptDeliveryReport( createDeliveryReport() );
          theSpawningLatch.countDown();
        }
      }
    });

    //one thread that listens to delivery reports
    Executors.newSingleThreadExecutor().execute( new Runnable(){
      public void run(){
        while(theSpawningLatch.getCount() > 0){
          try{
            Map< MultiPeerMessage, Map< String, DeliveryReport >> theReports =  theMessageArchive.getDeliveryReports();
            for(Map< String, DeliveryReport > theReport : theReports.values()){
              System.out.println(theReport);
            }
            
            Map<String, DeliveryReport>  theDeliveryReports =  theMessageArchive.getDeliveryReportsForMultiPeerMessage(myMultiMessage);
            for(String thePeerId : theDeliveryReports.keySet()){
              System.out.println(thePeerId + ":" + theDeliveryReports.get( thePeerId ).toString());
            }
          }catch(Exception e){
            e.printStackTrace();
            theExceptions.add(e);
          }
        }
      }
    });
    
    theSpawningLatch.await( 5, TimeUnit.SECONDS );
    assertEquals( 0, theSpawningLatch.getCount() );
    assertEquals( 0, theExceptions.size());
  }

  private DeliveryReport createDeliveryReport(){
   

    Message theMessage =  new Message();
    theMessage.setDestination( new DummyPeer( Integer.toString( Math.abs(myRandom.nextInt() % 100) ) ));

    DeliveryReport theReport = new DeliveryReport(myMultiMessage, Status.DELIVERED, theMessage);
    return theReport;
  }
  
  public void testSimultanousMessageRetrieval() throws InterruptedException{
    final MessageArchive theMessageArchive = new MessageArchive( );

    //one thread that spawns delivery reports
    final CountDownLatch theSpawningLatch = new CountDownLatch( myTimes );

    final List<Exception> theExceptions = new ArrayList<Exception>();
    
    final MultiPeerMessage theMultiMessage = MultiPeerMessage.createMessage( "test" )
        .addDestination( "1" );


    Executors.newSingleThreadExecutor().execute( new Runnable(){
      public void run(){
        for(int i=0;i<myTimes;i++){
          theMessageArchive.messageReceived( theMultiMessage );
          theSpawningLatch.countDown();
        }
      }
    });

    //one thread that listens to delivery reports
    Executors.newSingleThreadExecutor().execute( new Runnable(){
      public void run(){
        while(theSpawningLatch.getCount() > 0){
          try{
            Set< MultiPeerMessage> theMessages =  theMessageArchive.getAllMessages();
            for(MultiPeerMessage theMessage : theMessages){
              System.out.println(theMessage);
            }
            
            List<MultiPeerMessage> theReceivedMessages =  theMessageArchive.getReceivedMessages();
            for(MultiPeerMessage theMessage : theReceivedMessages){
              System.out.println(theMessage);
            }
          }catch(Exception e){
            e.printStackTrace();
            theExceptions.add(e);
          }
        }
      }
    });
    
    theSpawningLatch.await( 5, TimeUnit.SECONDS );
    assertEquals( 0, theSpawningLatch.getCount() );
    assertEquals( 0, theExceptions.size());
  }
  
  public void testClear(){
    MessageArchive theArchive = new MessageArchive();
    
    MultiPeerMessage theMultiMessage = MultiPeerMessage.createMessage( "test" )
        .addDestination( "1" );
    
    theArchive.messageReceived( theMultiMessage );
    theArchive.acceptDeliveryReport( createDeliveryReport() );
    
    assertEquals( 2, theArchive.getAllMessages().size() );
    assertEquals( 1, theArchive.getReceivedMessages().size() );
    assertEquals( 1, theArchive.getDeliveryReports().size() );
    
    theArchive.clear();
    
    assertEquals( 0, theArchive.getAllMessages().size() );
    assertEquals( 1, theArchive.getReceivedMessages().size() );
    assertEquals( 0, theArchive.getDeliveryReports().size() );
    

  }
}
