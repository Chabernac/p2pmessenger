package chabernac.comet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CometEventContainer {
  private static final long MAX_EVENT_TIME = 30 * 1000;
  
  private  Map<String, CometEvent> myEvents = new HashMap<String, CometEvent>();
  
  public synchronized void removeOldEvents(){
    long theCurrentTime = System.currentTimeMillis();
    for(Iterator<CometEvent> i = myEvents.values().iterator();i.hasNext();){
      CometEvent theEvent = i.next();
      long theLiveTime = theCurrentTime - theEvent.getCreationTime();
      if(theLiveTime > MAX_EVENT_TIME){
        i.remove();
      }
    }
  }
  
  public synchronized void remove(String anEventId){
    myEvents.remove(anEventId);
  }
  
  public synchronized void setOutput(String anEventId, String aResult) throws CometException{
    if(myEvents.containsKey(anEventId)){
      myEvents.get(anEventId).setOutput(aResult);
    }
  }
  
  public synchronized void addCommetEvent(CometEvent anEvent){
    myEvents.put(anEvent.getId(), anEvent);
  }
  
  public synchronized boolean containsEvent(String anEventId){
    return myEvents.containsKey(anEventId);
  }
}
