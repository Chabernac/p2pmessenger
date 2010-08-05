/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventDispatcher <T extends Event>{
  private static Map< Class, EventDispatcher > myDispatchers = new HashMap< Class, EventDispatcher>();
  
  private static ExecutorService myService = Executors.newFixedThreadPool( 1 );
  
  private List< iEventListener< T > > myListeners = new ArrayList< iEventListener<T> >();
  
  
  public static synchronized <T extends Event> EventDispatcher<T> getInstance(Class<T> aClass){
    if(!myDispatchers.containsKey( aClass )){
      myDispatchers.put( aClass, new EventDispatcher< T >() );
    }
    return myDispatchers.get( aClass );
  }
  
  public void addEventListener(iEventListener< T > anEventListener){
    myListeners.add(anEventListener);
  }
  
  public void removeEventListener(iEventListener< T > anEventListener){
    myListeners.remove( anEventListener );
  }
  
  public void fireEvent(T anEvent){
    myService.execute( new ProcessEvent(anEvent) );
  }
  
  private void dispatchEvent(T anEvent){
    for(iEventListener< T > theListener : myListeners){
      theListener.eventFired( anEvent );
    }
  }
  
  private class ProcessEvent implements Runnable{
    private final T myEvent;
    
    public ProcessEvent(T anEvent){
      myEvent = anEvent;
    }

    @Override
    public void run() {
      dispatchEvent(myEvent);
    }
    
  }
}
