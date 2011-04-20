package chabernac.plugin;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class PluginActivator {
	private static Logger LOGGER = Logger.getLogger(PluginActivator.class);
	public static void loadPlugins(boolean isWaitUntillLoaded){
		Executor theExecutor = Executors.newFixedThreadPool(20);
		try{
			Enumeration<URL> e = ClassLoader.getSystemClassLoader().getResources("META-INF/MANIFEST.MF");
			List<URL> theResourses = new ArrayList<URL>();
			while(e.hasMoreElements()){
			  theResourses.add( e.nextElement() );
			}
			
			final CountDownLatch theLatch = new CountDownLatch( theResourses.size() );
			
			for(final URL theURL : theResourses){
				theExecutor.execute(new Runnable(){
					public void run(){
					  theLatch.countDown();
						loadURL(theURL);
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
	
	private static void loadURL(URL aURL){
		Properties theProperties = new Properties();
		try{
			theProperties.load(aURL.openStream());
			loadPlugin(theProperties);
		}catch(Exception e){
			LOGGER.error("Could not start plugin with manifest: " + aURL, e);
		}
	}

	@SuppressWarnings("unchecked")
	private static void loadPlugin(Properties aProperties) throws Exception{
		String theActivator = aProperties.getProperty("plugin.activator");
		if(theActivator != null){
			Class theActivatorClass = Class.forName(theActivator);
			Object theActivatorInstance = theActivatorClass.newInstance();
			if(!(theActivatorInstance instanceof iActivator)){
				throw new ClassNotFoundException("Activator class: " + theActivator + " is not an instanceof iActivator");
			}
			LOGGER.debug("Executing activator: " + theActivator);
			((iActivator)theActivatorInstance).registerPlugin(PluginRegistry.getInstance(), aProperties);
		}
	}

}
