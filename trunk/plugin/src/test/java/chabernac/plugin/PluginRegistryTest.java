/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.plugin;

import java.util.List;

import junit.framework.TestCase;

public class PluginRegistryTest extends TestCase {
  public void testPluginRegistry() throws InterruptedException{
    PluginRegistryCounter theCounter = new PluginRegistryCounter();
    PluginRegistry.getInstance().addPluginListener( theCounter );
    
    Implementation1 theImp1 = new Implementation1();
    PluginRegistry.getInstance().registerPlugin( theImp1 );
    
    Implementation2 theImp2 = new Implementation2();
    PluginRegistry.getInstance().registerPlugin( theImp2 );
    
    Thread.sleep( 500 );
    
    assertEquals( 2, theCounter.getCounter() );
    
    PluginRegistry.getInstance().removePlugin( theImp2 );
    
    Thread.sleep( 500 );
    
    assertEquals( 1, theCounter.getCounter() );
    
    List<iTestInterface1> theInt1List = PluginRegistry.getInstance().getInstancesOf( iTestInterface1.class );
    
    for(iTestInterface1 theInt : theInt1List){
      theInt.doSomething();
    }
  }
  
  private class Implementation1 implements iTestInterface1{
    @Override
    public void doSomething() {
      System.out.println("1");
    }
  }
  
  private class Implementation2 implements iTestInterface2{
    @Override
    public void doSomething2() {
    }
  }
  
  private class PluginRegistryCounter implements iPluginRegistryListener{
    private int myCounter = 0;

    public int getCounter(){
      return myCounter;
    }
    
    @Override
    public void pluginRegistred( Object aPlugin ) {
      myCounter++;
    }

    @Override
    public void pluginRemoved( Object aPlugin ) {
      myCounter--;
    }
    
  }
}
