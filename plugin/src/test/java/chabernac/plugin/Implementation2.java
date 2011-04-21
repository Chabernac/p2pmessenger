/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.plugin;

import chabernac.plugin.exception.PluginNotLoadedException;
import chabernac.plugin.exception.PluginNotShutDownException;

public class Implementation2 implements iTestInterface2, iPlugin {

  @Override
  public void doSomething2() {

  }

  @Override
  public void loadPlugin( PluginRegistry aPluginRegistry ) throws PluginNotLoadedException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void shutDown( PluginRegistry aRegistry ) throws PluginNotShutDownException {
    // TODO Auto-generated method stub
    
  }

}
