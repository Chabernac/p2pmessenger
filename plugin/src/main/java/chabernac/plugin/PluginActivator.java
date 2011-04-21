package chabernac.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import sun.security.krb5.internal.APOptions;

public class PluginActivator {
  private static Logger LOGGER = Logger.getLogger(PluginActivator.class);
  private static Executor PLUGIN_LOADER = Executors.newFixedThreadPool(20); 

  public static void loadAll(boolean isWaitUntillLoaded){
    loadPluginsFromManifeset( isWaitUntillLoaded );
    loadPlugins( isWaitUntillLoaded );
  }

  public static void loadPluginsFromManifeset(boolean isWaitUntillLoaded){
    try{
      Enumeration<URL> e = ClassLoader.getSystemClassLoader().getResources("META-INF/MANIFEST.MF");
      List<URL> theResourses = new ArrayList<URL>();
      while(e.hasMoreElements()){
        theResourses.add( e.nextElement() );
      }

      final CountDownLatch theLatch = new CountDownLatch( theResourses.size() );

      for(final URL theURL : theResourses){
        PLUGIN_LOADER.execute(new Runnable(){
          public void run(){
            try{
              loadURL(theURL);
            } finally {
              theLatch.countDown();
            }
          }
        });
      }

      if(isWaitUntillLoaded){
        theLatch.await();
      }
    }catch(Exception e){
      LOGGER.error("Error occured while loading plugins", e);
    }
  }

  public static void loadPlugins(boolean isWaitUntillLoaded){
    Set<String> theLines = findAllPluginLines();

    final CountDownLatch theLatch = new CountDownLatch( theLines.size() );

    for(final String theLine : theLines){
      PLUGIN_LOADER.execute( new Runnable(){
        public void run(){
          try{
            loadFromLine( theLine );
          } finally {
            theLatch.countDown();
          }
        }
      });
    }

    if(isWaitUntillLoaded){
      try {
        theLatch.await();
      } catch ( InterruptedException e ) {
      }
    }
  }

  private static Set<String> findAllPluginLines(){
    Set<String> theLines = new HashSet<String>();

    try{
      Enumeration<URL> theResources = ClassLoader.getSystemClassLoader().getResources( "META-INF/plugins.txt" );
      while(theResources.hasMoreElements()){
        URL theURL = theResources.nextElement();
        BufferedReader theReader = null;
        try{
          theReader = new BufferedReader( new InputStreamReader( theURL.openStream() ));
          String theLine = null;
          while((theLine = theReader.readLine()) != null){
            theLines.add( theLine ); 
          }
        } catch(Exception e){
          LOGGER.error("Unable to load plugin resource with url '" + theURL + "'");
        } finally {
          if(theReader != null){
            try {
              theReader.close();
            } catch ( IOException e ) {
            }
          }
        }
      }
    }catch(IOException e){
      LOGGER.error("Unable to load plugins", e);
    }

    return theLines;
  }

  private static void loadFromLine(String aLine){
    LOGGER.debug( "Loading plugin from line '" + aLine + "'" );
    try{
      Object theInstance = Class.forName( aLine ).newInstance();
      if(theInstance instanceof iPlugin){
        PluginRegistry.getInstance().registerPlugin( (iPlugin)theInstance );
      } else {
        LOGGER.error( "Found plugin line with class which is not instanceof iPlugin '" + aLine + "'" );
      }
    }catch(Exception e){
      LOGGER.error("Unable to load plugin from line '" + aLine + "'");
    }
  }

  private static void loadURL(URL aURL){
    LOGGER.debug( "Trying to load plugin from url '" + aURL + "'" );
    Properties theProperties = new Properties();
    try{
      theProperties.load(aURL.openStream());
      loadPlugin(theProperties);
    }catch(Exception e){
      LOGGER.error("Could not start plugin with manifest: " + aURL, e);
    }
  }

  @SuppressWarnings("unchecked")
  private static void loadPlugin(final Properties aProperties) throws Exception{
    String theActivator = aProperties.getProperty("plugin.activator");
    if(theActivator != null){
      String[] theActivators = theActivator.split( " " );
      final CountDownLatch theLatch = new CountDownLatch( theActivators.length );
      for(final String theAct : theActivators){
        ExecutorService theService = Executors.newFixedThreadPool( theActivators.length );
        theService.execute( new Runnable(){
          public void run(){
            try{
              Class theActivatorClass = Class.forName(theAct);
              Object theActivatorInstance = theActivatorClass.newInstance();
              if(!(theActivatorInstance instanceof iActivator)){
                throw new ClassNotFoundException("Activator class: " + theAct + " is not an instanceof iActivator");
              }
              LOGGER.debug("Executing activator: " + theAct);
              ((iActivator)theActivatorInstance).registerPlugin(PluginRegistry.getInstance(), aProperties);
            } catch(Exception e){
              LOGGER.error( "Error occured while loading plugin '" + theAct + "'", e);
            } finally {
              theLatch.countDown();
            }
          }
        });
      }
      theLatch.await();
    }
  }

}
