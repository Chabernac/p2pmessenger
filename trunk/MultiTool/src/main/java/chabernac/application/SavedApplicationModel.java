package chabernac.application;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.task.event.ApplicationSaveEvent;

public abstract class SavedApplicationModel implements iEventListener {
  
  public SavedApplicationModel(){
    ApplicationEventDispatcher.addListener(this, ApplicationSaveEvent.class);
  }

  public void eventFired(Event evt) {
      save();
  }
  
  public abstract void load();
  public abstract void save();

}
