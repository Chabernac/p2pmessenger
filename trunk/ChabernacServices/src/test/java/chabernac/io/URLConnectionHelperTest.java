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

  /*
  public void testPost() throws IOException, InterruptedException{
    int theSockets = 20;

    ExecutorService theService = Executors.newCachedThreadPool();
    for(int i=0;i<theSockets;i++){
      theService.execute(
          new Runnable() {

            @Override
            public void run() {
              try {
                new Socket("10.0.0.47", 7778);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }}
          );
    }

    //for some reason when first creating some sockets the url connection becomes slow
    Thread.sleep(3000);
    
    long t1 = System.currentTimeMillis();
    URLConnectionHelper theConnectionHelper = new URLConnectionHelper( new URL("http://guyenleslie.dyndns-server.com/pp/prot"), true );
    theConnectionHelper.connectInputOutput();
    theConnectionHelper.write( "session", UUID.randomUUID().toString() );
    theConnectionHelper.write( "peerid", "0" );
    theConnectionHelper.write( "input", "ROUWHO_ARE_YOU" );
    theConnectionHelper.endInput();
    String theLine = theConnectionHelper.readLine();
    assertNotNull(theLine);
    long t2 = System.currentTimeMillis();
    assertTrue((t2-t1)<2000);
  }
  */
}
