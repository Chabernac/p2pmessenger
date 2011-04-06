/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class PluginRegistry {
  private static final Logger LOGGER = Logger.getLogger(PluginRegistry.class);
  
  private Set<Object> myPlugins = Collections.synchronizedSet( new HashSet<Object>());
  private Set<iPluginRegistryListener> myListeners = Collections.synchronizedSet( new HashSet<iPluginRegistryListener>() );
  private ExecutorService myListenerThread = Executors.newFixedThreadPool( 1 );
  
  private PluginRegistry(){
    loadPlugins();
  }

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

  private void loadPlugins(){
    try{
      Set<String> theProcessedLines  = new HashSet<String>();
      Enumeration<URL> theResources = ClassLoader.getSystemClassLoader().getResources( "META-INF/plugins.txt" );
      while(theResources.hasMoreElements()){
        URL theURL = theResources.nextElement();
        BufferedReader theReader = null;
        try{
          theReader = new BufferedReader( new InputStreamReader( theURL.openStream() ));
          String theLine = null;
          while((theLine = theReader.readLine()) != null){
            try {
              if(!theProcessedLines.contains( theLine )){
                theProcessedLines.add( theLine );
                registerPlugin( Class.forName( theLine ).newInstance() );
              }
            } catch ( Exception e ) {
              LOGGER.error("Unable to load plugin with class '" + theLine + "'", e);
            }
          }
        } finally {
          if(theReader != null){
            theReader.close();
          }
        }
      }
    }catch(IOException e){
      LOGGER.error("An error occured while looking for plugins", e);
    }
  }
}
