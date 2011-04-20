package chabernac.plugin;

import java.util.Properties;

import chabernac.plugin.exception.PluginNotLoadedException;

public interface iActivator {
	public void registerPlugin(PluginRegistry aRegistry, Properties aManifestProperties) throws PluginNotLoadedException;
}
