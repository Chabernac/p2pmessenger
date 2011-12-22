/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.BasicConfigurator;

import junit.framework.TestCase;

public class TestApacheURLConnectionHelperTest extends TestCase {
  static{
    BasicConfigurator.configure();
  }
  
  public void testApacheURLConnectionHelper() throws IOException{
    AbstractURLConnectionHelper theHelper = new ApacheURLConnectionHelper( new URL("http://www.google.be"), false );
    theHelper.connect( false, true );
    String theLine = null;
    while((theLine = theHelper.readLine())!= null){
      System.out.println(theLine);
    }
  }
}
