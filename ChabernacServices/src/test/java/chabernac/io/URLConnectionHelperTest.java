/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

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
  
  public void testExceptionOnReadLine() throws MalformedURLException{
    URLConnectionHelper theManager = new URLConnectionHelper( "http://www.google.com" );
    try{
      theManager.readLine();
      fail("should not come here");
    }catch(Exception e){
    }
  }
  
  public void testResolveURL() throws UnknownHostException, MalformedURLException{
    URL theURL = new URL("http://guyenleslie.dyndns-server.com/pp/prot");
    System.out.println(theURL);
    InetAddress inet = InetAddress.getByName(theURL.getHost());
    System.out.println(inet.getHostAddress());
    URL theNewURL = new URL(new URL("http://" + inet.getHostAddress()), theURL.getPath());
    System.out.println(theNewURL.toString());
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
