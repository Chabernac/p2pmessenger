package chabernac.test;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;

public class TestApplicationEventQueue implements iEventListener{
  public TestApplicationEventQueue(){
    ApplicationEventDispatcher.addListener(this, Event.class);
    //ApplicationEventDispatcher.addListener(this, TestEvent.class);
    //ApplicationEventDispatcher.addListener(this, TestEvent2.class);
    for(int i=0;i<100;i++){
      if(i%3 == 0) ApplicationEventDispatcher.fireEvent(new Event("test " + i));
      else if(i%3 == 1)  ApplicationEventDispatcher.fireEvent(new TestEvent("test " + i));
      else if(i%3 == 2)  ApplicationEventDispatcher.fireEvent(new TestEvent2("test " + i));
      try{
        Thread.sleep(200);
      }catch(Exception e){}
    }
  }
  
  public static void main(String args[]){
   new TestApplicationEventQueue();   
  }

  public void eventFired(Event anEvt) {
    System.out.println("Event received: " + anEvt.getDescription());
  }
  
}
