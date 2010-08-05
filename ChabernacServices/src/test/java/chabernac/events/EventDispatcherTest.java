/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.events;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class EventDispatcherTest extends TestCase {
  public void testEventDispatcher() throws InterruptedException{
    EventDispatcher<TestEvent> theEventDispatcher = EventDispatcher.getInstance( TestEvent.class );
  
    TestEventCollector theCollector = new TestEventCollector();
    theEventDispatcher.addEventListener( theCollector  );
    
    for(int i=0;i<10;i++){
      theEventDispatcher.fireEvent( new TestEvent() );
    }
    
    //the events are proccessed on a seperate thread, so wait for a second to make sure all events where processed
    Thread.sleep( 1000 );
    
    assertEquals( 10, theCollector.getEvents().size() );
  }
  
  private class TestEventCollector implements iEventListener< TestEvent >{
    private List< TestEvent > myEvents = new ArrayList< TestEvent >();

    @Override
    public void eventFired( TestEvent anEvent ) {
      myEvents.add( anEvent );
    }
    
    public List<TestEvent> getEvents(){
      return myEvents;
    }
  }
}
