package chabernac.plugin;

import chabernac.plugin.exception.PluginNotLoadedException;
import chabernac.plugin.exception.PluginNotShutDownException;

public interface iPlugin {
	public void loadPlugin(PluginRegistry aPluginRegistry) throws PluginNotLoadedException;
	public void shutDown(PluginRegistry aRegistry) throws PluginNotShutDownException;
}
