/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.pominfoexchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class POMInfo implements Serializable {
  private static final long serialVersionUID = -1177621244988498718L;

  private final List< POM > myPomInfo = new ArrayList< POM >();


  public POMInfo() throws IOException{
    loadPOMInfo();
  }

  private void loadPOMInfo() throws IOException{
    String[] theClasspath = System.getProperty( "java.class.path" ).split( ";" );
    
    for(String theEntry : theClasspath){
      if(theEntry.endsWith( ".jar" )){
        JarFile theJarFile = new JarFile(theEntry);
        System.out.println(theJarFile.getName());
        Enumeration< JarEntry > theJarEntries = theJarFile.entries();
        while(theJarEntries.hasMoreElements()){
          JarEntry theJarEntry = theJarEntries.nextElement();
//          System.out.println(theJarEntry.getName());
          if(theJarEntry.getName().endsWith( "pom.properties" )){
            readPOMProperties( theJarFile.getInputStream( theJarEntry ) );
          }
        }
      }
    }
  }

  private void readPOMProperties( InputStream anInputStream) throws IOException {
    POM thePOM = new POM();
    BufferedReader theReader = null;
    try{
      theReader = new BufferedReader(new InputStreamReader(anInputStream));
      String theLine = null;
      while((theLine = theReader.readLine()) != null){
        System.out.println(theLine);
//        thePOM.addLine( theLine );
      }
    } finally {
      theReader.close();
    }
  }

  public List< POM > getPomInfo() {
    return myPomInfo;
  }

  public String toString(){
    StringBuilder theBuilder = new StringBuilder();
    for(POM thePOM : myPomInfo){
      theBuilder.append(thePOM.toString());
    }
    return theBuilder.toString();
  }

}
