/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import chabernac.protocol.message.DeliveryReport.Status;
import chabernac.protocol.routing.DummyPeer;

public class MessageArchiveTest extends TestCase {
  public void testSimultanousDeliveryReportsRetrieval() throws InterruptedException{
    final MessageArchive theMessageArchive = new MessageArchive( );

    //one thread that spawns delivery reports
    final int theTimes = 10000;
    final CountDownLatch theSpawningLatch = new CountDownLatch( theTimes );

    final List<Exception> theExceptions = new ArrayList<Exception>();
    
    final DeliveryReport theDeliveryReport = createDeliveryReport();

    Executors.newSingleThreadExecutor().execute( new Runnable(){
      public void run(){
        for(int i=0;i<theTimes;i++){
          theMessageArchive.acceptDeliveryReport( theDeliveryReport );
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
            
            Map<String, DeliveryReport>  theDeliveryReports =  theMessageArchive.getDeliveryReportsForMultiPeerMessage(theDeliveryReport.getMultiPeerMessage());
            for(DeliveryReport theReport : theDeliveryReports.values()){
              System.out.println(theReport);
            }
          }catch(Exception e){
            e.printStackTrace();
            theExceptions.add(e);
          }
        }
      }
    });
    
    theSpawningLatch.await( 5, TimeUnit.SECONDS );
    assertEquals( 0, theExceptions.size());
  }

  private DeliveryReport createDeliveryReport(){
    MultiPeerMessage theMultiMessage = MultiPeerMessage.createMessage( "test" )
        .addDestination( "1" );

    Message theMessage =  new Message();
    theMessage.setDestination( new DummyPeer( "1" ) );

    DeliveryReport theReport = new DeliveryReport(theMultiMessage, Status.DELIVERED, theMessage);
    return theReport;
  }
  
  public void testSimultanousMessageRetrieval() throws InterruptedException{
    final MessageArchive theMessageArchive = new MessageArchive( );

    //one thread that spawns delivery reports
    final int theTimes = 10000;
    final CountDownLatch theSpawningLatch = new CountDownLatch( theTimes );

    final List<Exception> theExceptions = new ArrayList<Exception>();
    
    final MultiPeerMessage theMultiMessage = MultiPeerMessage.createMessage( "test" )
        .addDestination( "1" );


    Executors.newSingleThreadExecutor().execute( new Runnable(){
      public void run(){
        for(int i=0;i<theTimes;i++){
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
