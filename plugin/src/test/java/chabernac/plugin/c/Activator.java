package chabernac.plugin.c;

import java.util.Properties;

import chabernac.plugin.PluginRegistry;
import chabernac.plugin.iActivator;
import chabernac.plugin.a.iPluginA;
import chabernac.plugin.b.iPluginB;
import chabernac.plugin.exception.PluginNotLoadedException;

public class Activator implements iActivator {

	public void registerPlugin(PluginRegistry aRegistry, Properties aProperties) throws PluginNotLoadedException {
		aRegistry.waitForPlugin(iPluginA.class);
		aRegistry.waitForPlugin(iPluginB.class);
		aRegistry.registerPlugin(new PluginC());
	}

}
