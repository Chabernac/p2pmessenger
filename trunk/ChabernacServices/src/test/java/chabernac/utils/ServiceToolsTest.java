/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.TestCase;

public class ServiceToolsTest extends TestCase {
  public void testAddFileToStartup() throws IOException{
    File theFile = new File("c:\\data\\test.exe");
    File theCMDFile = ServiceTools.addRun2Startup( theFile );
    
    assertEquals( "C:\\Documents and Settings\\dgch804\\Start Menu\\Programs\\Startup\\test.exe.cmd", theCMDFile.getAbsolutePath() );
    
    BufferedReader theReader =new BufferedReader(new InputStreamReader(new FileInputStream(theCMDFile)));
    
    String theLine = theReader.readLine();
    
    theReader.close();
    
    assertEquals( "c:\\data\\test.exe", theLine );
    
    assertTrue( ServiceTools.removeRunAtStartup( theFile ) );
    
    assertFalse( theCMDFile.exists() );
  }
  
  public void testGetRegistryKey(){
    assertNotNull( ServiceTools.getRegistryRunKey("Sheduler") );
  }
}
