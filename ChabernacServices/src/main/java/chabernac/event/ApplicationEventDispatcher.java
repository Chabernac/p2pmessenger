package chabernac.event;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.queue.ArrayQueue;
import chabernac.queue.TriggeredQueueReader;
import chabernac.queue.TriggeringQueue;
import chabernac.queue.iObjectProcessor;

public class ApplicationEventDispatcher implements iObjectProcessor{
  public static String SYSTEM_EVENT_QUEUE="SYSTEM_EVENT_QUEUE";
  private static Logger LOGGER = Logger.getLogger(ApplicationEventDispatcher.class);
  private static Map<String, ApplicationEventDispatcher> instances = Collections.synchronizedMap(new HashMap<String, ApplicationEventDispatcher>());

  private ArrayList< iEventListener >                      myAllEventListeners     = null;
  private HashMap< Class, List< iEventListener >>          myEventListeners        = null;
  private TriggeringQueue myApplicationEventQueue = null;
  private TriggeredQueueReader myQueueReader = null;
  private String myEventQueue = null;

  private ApplicationEventDispatcher(String anEventQueue){
    myEventQueue = anEventQueue;
    myApplicationEventQueue = new TriggeringQueue(new ArrayQueue(200, true));
    myQueueReader = new TriggeredQueueReader(myApplicationEventQueue, this);
    myAllEventListeners = new ArrayList< iEventListener >();
    myEventListeners = new HashMap< Class, List< iEventListener >>();
  }

  public static ApplicationEventDispatcher getInstance(){
    return getInstance(SYSTEM_EVENT_QUEUE);
  }
  public static ApplicationEventDispatcher getInstance(String anEventQueue){
    if(!instances.containsKey(anEventQueue)){
      instances.put(anEventQueue, new ApplicationEventDispatcher(anEventQueue));
    }
    return instances.get(anEventQueue);
  }

  private void removeEventListener( iEventListener aListener ) {
    myAllEventListeners.remove(aListener);
    for ( Iterator< List< iEventListener >> i = myEventListeners.values().iterator(); i.hasNext(); ) {
      List< iEventListener > theListeners = i.next();
      theListeners.remove(aListener);
    }
  }

  public void removeAllEventListeners(){
    myAllEventListeners.clear();
    myEventListeners.clear();
  }

  public static void removeListener(iEventListener aListener){
    getInstance().removeEventListener(aListener);
  }

  public static void removeListener(String anEventQueue, iEventListener aListener){
    getInstance(anEventQueue).removeEventListener(aListener);
  }

  public static void removeEventListener(String anEventQueue){
    getInstance(anEventQueue).removeAllEventListeners();
    instances.remove(anEventQueue);
  }


  private void addEventListener( iEventListener aListener, Class< Event >[] anEventClasses ) {
    for(int i=0;i<anEventClasses.length;i++){
      if(!myEventListeners.containsKey((anEventClasses[i]))){
        myEventListeners.put( anEventClasses[ i ], new ArrayList< iEventListener >() );
      }
      List< iEventListener > theEventListeners = myEventListeners.get( anEventClasses[ i ] );
      theEventListeners.add(aListener);
    }
  }

  private void addEventListener( iEventListener aListener ) {
    myAllEventListeners.add( aListener );
  }

  public static void addListener( iEventListener aListener ) {
    getInstance().addEventListener( aListener );
  }

  public static void addListener( iEventListener aListener, Class anEventClasses ) {
    getInstance().addEventListener(aListener, new Class[]{anEventClasses});
  }

  public static void addListener( String anEventQueue, iEventListener aListener ) {
    getInstance( anEventQueue ).addEventListener( aListener );
  }

  public static void addListener( String anEventQueue, iEventListener aListener, Class anEventClasses ) {
    getInstance(anEventQueue).addEventListener(aListener, new Class[]{anEventClasses});
  }

  public static void addListener(iEventListener aListener, Class[] anEventClasses){
    getInstance().addEventListener(aListener, anEventClasses);
  }

  public static void addListener(String anEventQueue, iEventListener aListener, Class[] anEventClasses){
    getInstance(anEventQueue).addEventListener(aListener, anEventClasses);
  }

  public static void waitForEmpty(long aTimeout, TimeUnit aTimeUnit) throws InterruptedException{
    getInstance().waitFrEmpty(aTimeout, aTimeUnit);
  }

  public static void waitForEmpty(String anEventQueue, long aTimeout, TimeUnit aTimeUnit) throws InterruptedException{
    getInstance(anEventQueue).waitFrEmpty(aTimeout, aTimeUnit);
  }

  static TriggeringQueue getApplicationEventQueue(){
    return getInstance().myApplicationEventQueue;
  }

  private void fireAnEvent(Event anEvent){
    if(LOGGER.isDebugEnabled()){
      LOGGER.debug("Event fired: [" + anEvent.getDescription() + "]");
    }
    anEvent.setEventQueue(myEventQueue);
    myApplicationEventQueue.put(anEvent);
  }

  public static void fireEvent(Event anEvent){
    getInstance().fireAnEvent(anEvent); 
  }

  public static void fireEvent(String anEventQueue, Event anEvent){
    getInstance(anEventQueue).fireAnEvent(anEvent); 
  }

  public void processObject(Object anObject) {
    try{
      if(anObject instanceof Event){
        Event theEvent = (Event)anObject;
//				LOGGER.debug("Dispatching event: " + theEvent.getClass() + " " + theEvent.getDescription());
        Class theEventClass = theEvent.getClass();
        for(Iterator i=myAllEventListeners.iterator();i.hasNext();){
          dispatchEvent(theEvent, (iEventListener)i.next());
        }

        for(Iterator i=myEventListeners.keySet().iterator();i.hasNext();){
          Class theClass = (Class)i.next();
          if(theClass.isAssignableFrom(theEventClass)){
            for(Iterator j = ((ArrayList)myEventListeners.get(theClass)).iterator();j.hasNext();){
              dispatchEvent(theEvent, (iEventListener)j.next());
            }  
          }
        }
      }
    }finally{
      synchronized(this){
        notifyAll();
      }
    }

  }

  private void waitFrEmpty(long aTimeout, TimeUnit aTimeUnit) throws InterruptedException{
    myQueueReader.waitTillFinished(aTimeout, aTimeUnit);
  }



  private void dispatchEvent(Event anEvent, iEventListener aListener){
    if(LOGGER.isDebugEnabled()){
      LOGGER.debug("Dispatching event: [" + anEvent.getDescription() + "] to listener: " + aListener.getClass() + " on event queue: " + myEventQueue);
    }
    aListener.eventFired(anEvent);
  }

}
