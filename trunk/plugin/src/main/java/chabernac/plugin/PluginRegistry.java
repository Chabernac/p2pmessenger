package chabernac.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import chabernac.plugin.exception.PluginNotFoundException;
import chabernac.plugin.exception.PluginNotLoadedException;
import chabernac.plugin.exception.PluginNotShutDownException;

public class PluginRegistry {
	private static Logger LOGGER = Logger.getLogger(PluginRegistry.class);
	private Set<Item> myPlugins= null;
  private Set<iPluginRegistryListener> myListeners = Collections.synchronizedSet( new HashSet<iPluginRegistryListener>() );
  private ExecutorService myListenerThread = Executors.newFixedThreadPool( 1 );
	
	private static class InstanceHolder{
		public static PluginRegistry INSTANCE = new PluginRegistry();
	}
	
	public static PluginRegistry getInstance(){
		return InstanceHolder.INSTANCE;
	}
	
	/**
	 * public because it might be handy for test purposes to create
	 * multiple plugin regestries, nevertheless in a production environment
	 * only one plugin registry must be present, create it with the static factory method
	 */
	public PluginRegistry(){
		myPlugins = new HashSet<Item>();
	}
	
	public synchronized void registerPlugin(final iPlugin aPlugin) throws PluginNotLoadedException{
	  aPlugin.loadPlugin(this);
		myPlugins.add(new Item( aPlugin ));
		
		myListenerThread.execute( new Runnable(){
      public void run(){
        for(iPluginRegistryListener theListener : myListeners) theListener.pluginRegistred( aPlugin );
      }
    });
		
		notifyAll();
	}
	
	public synchronized void removePlugin(final iPlugin aPlugin) throws PluginNotShutDownException{
	  aPlugin.shutDown( this );
	  myPlugins.remove( new Item(aPlugin) );
	  myListenerThread.execute( new Runnable(){
      public void run(){
        for(iPluginRegistryListener theListener : myListeners) theListener.pluginRemoved(  aPlugin );
      }
    });
	}
	
  public <T> List<T> getPlugins(Class<T> aClass){
    List<T> theList = new ArrayList<T>();
    for(Item theObject : myPlugins){
      if(aClass.isAssignableFrom( theObject.item.getClass() )){
        theList.add( (T)(theObject.item) );
      }
    }
    return theList;
  }
	
	@SuppressWarnings("unchecked")
	public <T> T getPlugin(Class<T> aClass) throws PluginNotFoundException {
		T thePlugin = findPlugin(aClass);
		if(thePlugin != null) {
			return thePlugin;
		}
		throw new PluginNotFoundException("Could not find plugin of class: " + aClass);
	}
	
	@SuppressWarnings("unchecked")
	private synchronized <T> T findPlugin(Class<T> aClass){
		for(Iterator<Item> i=myPlugins.iterator();i.hasNext();){
			iPlugin thePlugin = i.next().item;
			if(aClass.isAssignableFrom(thePlugin.getClass())){
				return (T)thePlugin;
			}
		}
		return null;
	}
	
  public Set<iPlugin> getPlugins(){
    Set<iPlugin> thePluginSet = new HashSet<iPlugin>();
    for(Iterator<Item> i=myPlugins.iterator();i.hasNext();){
      Item theItem = i.next();
      thePluginSet.add(theItem.item);
    }
    return Collections.unmodifiableSet(thePluginSet);
  }
	
	public synchronized <T> T waitForPlugin(Class<T> aClass){
		T thePlugin = null;
		while((thePlugin = findPlugin(aClass)) == null){
			try {
				wait();
			} catch (InterruptedException e) {
				LOGGER.error("Could not wait", e);
			}
		}
		return thePlugin;
	}
	
	public synchronized void removeAllPlugins() throws PluginNotShutDownException{
	  while(!myPlugins.isEmpty()){
	    removePlugin( myPlugins.iterator().next().item);
	  }
	}
	
  public void addPluginListener(iPluginRegistryListener aListener){
    myListeners.add(aListener);
  }

  public void removePluginListener(iPluginRegistryListener aListener){
    myListeners.remove(aListener);
  }

	
	private class Item{
		public iPlugin item;
		
		public Item(iPlugin anPlugin) {
			item = anPlugin;
		}

		public int hashCode(){
			return item.getClass().hashCode();
		}
		
		public boolean equals(Object anObject){
			if(!(anObject instanceof Item)){
				return false;
			}
			return ((Item)anObject).item.getClass().equals(item.getClass());
		}
	}

}
