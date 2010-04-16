package chabernac.event;

import java.io.Serializable;

public class Event implements Serializable {
  private static final long serialVersionUID = -5739584569831661408L;
  
  private String description = "";
  
  //the event queue on which this event was processed
  private String myEventQueue = "";
  
  public Event(String aDescription){
    description = aDescription;
  }
  
  public String getDescription(){
    return description;
  }

	public String getEventQueue() {
		return myEventQueue;
	}

	public void setEventQueue(String anEventQueue) {
		myEventQueue = anEventQueue;
	}
	
	public String toString(){
	  return "<Event description='" + description + "' eventqueue='" + myEventQueue + "'/>";
	}

  
}
