/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.comet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class EndPointContainerTest extends TestCase {
  public void testEndPointContainer() throws InterruptedException{
    EndPointContainer theContainer = new EndPointContainer();
    theContainer.setEndPointsPerId(10);
    
    assertEquals( 0,theContainer.getNrOfEndPoints( "3" ));
    
    theContainer.addEndPoint( new EndPoint( "1" ) );
    assertEquals( 1,theContainer.getNrOfEndPoints( "1" ));
    assertEquals( "1",theContainer.getEndPointFor( "1", 5, TimeUnit.SECONDS ).getId());
    
    theContainer.addEndPoint( new EndPoint( "2" )  );
    theContainer.addEndPoint( new EndPoint( "2" ) );
    assertEquals( 2,theContainer.getNrOfEndPoints( "2" ));
    assertEquals( "2",theContainer.getEndPointFor( "2", 5, TimeUnit.SECONDS ).getId());
    assertEquals( "2",theContainer.getEndPointFor( "2", 5, TimeUnit.SECONDS ).getId());
  }
  
  public void testAsynchronousPut() throws InterruptedException{
    final List<EndPoint> theEndPoints = new ArrayList<EndPoint>();
    
    final EndPointContainer theContainer = new EndPointContainer();
    
    int times = 10;
    for(int i=0;i<times;i++){
      final int j = i;
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            theEndPoints.add( theContainer.getEndPointFor( Integer.toString( j ), 5, TimeUnit.SECONDS ) );
          } catch ( InterruptedException e ) {
          }
        }
      });
    }
    for(int i=0;i<times;i++){
      theContainer.addEndPoint( new EndPoint( Integer.toString( i ) ) );
    }
    
    assertEquals( times, theContainer.size() );
    
  }
  
  public void testRemoveEndPoint() throws InterruptedException{
    EndPointContainer theContainer = new EndPointContainer();
    theContainer.setEndPointsPerId( 10 );
    
    EndPoint theEndPoint = new EndPoint( "2" );
    theContainer.addEndPoint( theEndPoint  );
    EndPoint theEndPoint2 = new EndPoint( "2" );
    theContainer.addEndPoint( theEndPoint2  );
    
    assertEquals( 2, theContainer.getNrOfEndPoints( "2" ) );
    theContainer.removeEndPoint( theEndPoint  );
    assertEquals( 1, theContainer.getNrOfEndPoints( "2" ) );
    theContainer.removeEndPoint( theEndPoint2  );
    assertEquals( 0, theContainer.getNrOfEndPoints( "2" ) );
  }
  
  public void testContainsEndPoint() throws InterruptedException{
    EndPointContainer theContainer = new EndPointContainer();
    theContainer.setEndPointsPerId( 10 );
    
    EndPoint theEndPoint = new EndPoint( "2" );
    assertFalse( theContainer.containsEndPointFor( "2" ) );
    theContainer.addEndPoint( theEndPoint  );
    assertTrue( theContainer.containsEndPointFor( "2" ) );
  }
  
  public void testEndPointsPerId(){
    EndPointContainer theContainer = new EndPointContainer();
    theContainer.setEndPointsPerId( 10 );
    assertEquals( 10, theContainer.getEndPointsPerId() );
  }
  
  public void testAddMoreEndPointsAsAllowed() throws InterruptedException{
    EndPointContainer theContainer = new EndPointContainer();
    theContainer.setEndPointsPerId( 2 );
    
    EndPoint theEndPoint = new EndPoint( "2" );
    theContainer.addEndPoint( theEndPoint  );
    assertEquals( 1, theContainer.getNrOfEndPoints( "2" ) );
    
    theEndPoint = new EndPoint( "2" );
    theContainer.addEndPoint( theEndPoint  );
    assertEquals( 2, theContainer.getNrOfEndPoints( "2" ) );
    
    theEndPoint = new EndPoint( "2" );
    theContainer.addEndPoint( theEndPoint  );
    assertEquals( 2, theContainer.getNrOfEndPoints( "2" ) );
  }
  
  public void testGetAllEndPoints() throws InterruptedException{
    EndPointContainer theContainer = new EndPointContainer();
    theContainer.setEndPointsPerId( 2 );
    
    EndPoint theEndPoint1 = new EndPoint( "1" );
    theContainer.addEndPoint( theEndPoint1  );
    
    EndPoint theEndPoint2 = new EndPoint( "1" );
    theContainer.addEndPoint( theEndPoint2  );
    
    EndPoint theEndPoint3 = new EndPoint( "3" );
    theContainer.addEndPoint( theEndPoint3  );
    
    EndPoint theEndPoint4 = new EndPoint( "4" );
    theContainer.addEndPoint( theEndPoint4  );
    
    List<EndPoint> theAllEndPoints = theContainer.getAllEndPoints();
    
    assertEquals( 4, theAllEndPoints.size() );
    assertTrue( theAllEndPoints.contains( theEndPoint1 ) );
    assertTrue( theAllEndPoints.contains( theEndPoint2 ) );
    assertTrue( theAllEndPoints.contains( theEndPoint3 ) );
    assertTrue( theAllEndPoints.contains( theEndPoint4 ) );
  }
  
  public void testAmbigousSituation() throws InterruptedException{
    final EndPointContainer theContainer = new EndPointContainer();
    
    Executors.newScheduledThreadPool(1).schedule(new Runnable(){
      public void run(){
        try{
        theContainer.removeEndPoint(new EndPoint("1"));
        theContainer.addEndPoint(new EndPoint("1"));
        }catch(Exception e){
          
        }
      }
    }, 1 , TimeUnit.SECONDS);
    
    assertNotNull(theContainer.getEndPointFor("1", 5, TimeUnit.SECONDS));
  }
}
