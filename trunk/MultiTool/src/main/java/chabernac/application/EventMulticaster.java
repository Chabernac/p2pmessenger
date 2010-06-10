package chabernac.application;

import java.util.ArrayList;

public class EventMulticaster {
	
	private static EventMulticaster instance = null;
	
	private ArrayList listeners = null;
	
	
	public EventMulticaster(){
		listeners = new ArrayList();
	}
	
	public static EventMulticaster getInstance(){
		if(instance == null) instance = new EventMulticaster();
		return instance;
	}
	
	public void addListener(iEventListener aListener){
		listeners.add(aListener);
	}
	
	public void removeListener(iEventListener aListener){
		listeners.remove(aListener);
	}
	
	public void fireEvent(Event anEvent){
		for(int i=0;i<listeners.size();i++){
			((iEventListener)listeners.get(i)).eventFired(anEvent);
		}
	}
}
