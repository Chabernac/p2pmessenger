/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;


public class TestTools {
  private static Boolean isUnitTest = null;
  
  public static void setIsUnitTest(boolean isUnitTest){
    TestTools.isUnitTest = isUnitTest;
  }
  public static boolean isInUnitTest(){
    if(isUnitTest != null) {
      return TestTools.isUnitTest;
    }
    
    Thread theCurrentThread = Thread.currentThread();
    ThreadGroup theGroup = theCurrentThread.getThreadGroup();
    while(theGroup.getParent() != null){
      theGroup = theGroup.getParent();
    }
    
    Thread[] theThreads = new Thread[theGroup.activeCount()];
    theGroup.enumerate(theThreads);
    
    for(Thread theThread : theThreads){
      for(StackTraceElement theElement : theThread.getStackTrace()) {
        if(theElement.getClassName().toString().equalsIgnoreCase("junit.framework.TestCase")){
          return true;
        }
      }
    }
    
    return false;
  }
}
