package chabernac.test;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.synchro.SynchronizedEventListener;
import chabernac.synchro.event.LocationChangedEvent;

public class TestSynchronizedEvent {

  public static void main(String[] args) {
    try{
      SynchronizedEventListener.connectToServer("localhost",14003);

      ApplicationEventDispatcher.addListener(new EventListener());
      for(int i=0;i<100;i++){
        ApplicationEventDispatcher.fireEvent(new LocationChangedEvent(Integer.parseInt(args[0]), i, 200 - i));
        Thread.sleep(100);
      }
    }catch(Exception e){
      e.printStackTrace();
    }

  }

  private static class EventListener implements iEventListener{

    public void eventFired(Event anEvt) {
      System.out.println("Event received: " + anEvt);
    }

  }

}
