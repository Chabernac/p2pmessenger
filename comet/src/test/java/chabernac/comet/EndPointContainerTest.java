/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.comet;

import java.util.ArrayList;
import java.util.List;
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
}
