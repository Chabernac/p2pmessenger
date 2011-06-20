/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

import junit.framework.TestCase;

public class URLConnectionHelperTest extends TestCase {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testURLConnectionManager() throws IOException{
    int theTimes = 224;
    for(int i=0;i<theTimes;i++){
      System.out.println(theTimes);
      readFromURL( "http://www.axa.be/" );
    }
  }
  
  private void readFromURL(String aURL) throws IOException{
    URLConnectionHelper theManager = new URLConnectionHelper( aURL );
    try{
      theManager.connect(true, false);
      String theLine = null;
      while((theLine = theManager.readLine()) != null){
        System.out.println(theLine);
      }
    } finally {
      theManager.close();
    }
    
  }
}
