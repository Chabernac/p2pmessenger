/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TestTools {
  private static Boolean isUnitTest = null;
  
  public static void setIsUnitTest(boolean isUnitTest){
    TestTools.isUnitTest = isUnitTest;
  }
  public static boolean isInUnitTest(){
    if(isUnitTest != null) {
      return TestTools.isUnitTest;
    }
    
    Exception theE = new Exception();
    theE.fillInStackTrace();
    StringWriter theStringWriter =new StringWriter();
    theE.printStackTrace( new PrintWriter(theStringWriter) );
    String theStrackTrace = theStringWriter.toString();
    return theStrackTrace.contains( "junit.framework.TestCase" );
  }
}
