package chabernac.plugin;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.plugin.b.iPluginB;
import chabernac.plugin.c.iPluginC;
import chabernac.plugin.exception.PluginNotFoundException;
import chabernac.plugin.exception.PluginNotShutDownException;

public class PluginTest extends TestCase {
	public void setUp(){
		BasicConfigurator.configure();
		try {
      PluginRegistry.getInstance().removeAllPlugins();
    } catch ( PluginNotShutDownException e ) {
    }
	}

	public void testLoadPluginA() throws InterruptedException{
		PluginActivator.loadAll( true );
		assertEquals( 5, PluginRegistry.getInstance().getPlugins().size());
		iPluginC a = (iPluginC)PluginRegistry.getInstance().waitForPlugin(iPluginC.class);
		a.c();
	}

	public void testLoadPluginB() throws PluginNotShutDownException{
		PluginRegistry.getInstance().removeAllPlugins();
		new Thread(new Runnable(){
			public void run(){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				PluginActivator.loadAll( false );
			}
		}).start();

		try{
			PluginRegistry.getInstance().getPlugin(iPluginB.class);
			assertTrue(false);
		}catch(PluginNotFoundException e){
			assertTrue(true);
		}
		iPluginB thePluginB = (iPluginB)PluginRegistry.getInstance().waitForPlugin(iPluginB.class);
		thePluginB.b();
		iPluginC thePluginC = (iPluginC)PluginRegistry.getInstance().waitForPlugin(iPluginC.class);
		thePluginC.c();
	}
}
