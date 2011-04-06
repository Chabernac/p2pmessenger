/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.plugin;

public interface iPluginRegistryListener {
  public void pluginRegistred(Object aPlugin);
  public void pluginRemoved(Object aPlugin);
}
