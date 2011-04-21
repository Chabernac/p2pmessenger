package chabernac.plugin.b;

import java.util.Properties;

import chabernac.plugin.PluginRegistry;
import chabernac.plugin.iActivator;
import chabernac.plugin.exception.PluginNotLoadedException;

public class Activator implements iActivator {

	public void registerPlugin(PluginRegistry aRegistry, Properties aProperties) throws PluginNotLoadedException {
		aRegistry.registerPlugin(new PluginB());
	}

}
