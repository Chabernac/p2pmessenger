/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PluginRegistry {
  private List<Object> myPlugins = Collections.synchronizedList( new ArrayList<Object>());
  private List<iPluginRegistryListener> myListeners = Collections.synchronizedList( new ArrayList<iPluginRegistryListener>() );
  private ExecutorService myListenerThread = Executors.newFixedThreadPool( 1 );
  
  private static class InstanceHolder{
    private static PluginRegistry INSTANCE = new PluginRegistry();
  }
  
  public static PluginRegistry getInstance(){
    return InstanceHolder.INSTANCE;
  }
  
  public void registerPlugin(final Object aPlugin){
    myPlugins.add(aPlugin);
    
    myListenerThread.execute( new Runnable(){
      public void run(){
        for(iPluginRegistryListener theListener : myListeners) theListener.pluginRegistred( aPlugin );
      }
    });
  }
  
  public void removePlugin(final Object aPlugin){
    myPlugins.remove(aPlugin);
    
    myListenerThread.execute( new Runnable(){
      public void run(){
        for(iPluginRegistryListener theListener : myListeners) theListener.pluginRemoved( aPlugin );
      }
    });
  }
  
  public void addPluginListener(iPluginRegistryListener aListener){
    myListeners.add(aListener);
  }
  
  public void removePluginListener(iPluginRegistryListener aListener){
    myListeners.remove(aListener);
  }
  
  
  
  public <T> List<T> getInstancesOf(Class<T> aClass){
    List<T> theList = new ArrayList<T>();
    for(Object theObject : myPlugins){
      if(aClass.isAssignableFrom( theObject.getClass() )){
        theList.add( (T)theObject );
      }
    }
    return theList;
  }
}
