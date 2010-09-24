/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.pominfoexchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class POMInfo implements Serializable {
  private static final long serialVersionUID = -1177621244988498718L;

  private final List< POM > myPomInfo = new ArrayList< POM >();

  public POMInfo() throws IOException{
    loadPOMInfo();
  }

  private void loadPOMInfo() throws IOException{
    Enumeration< URL > theResources = getClass().getClassLoader().getResources( "META-INF/maven/chabernac/LDapUserInfoProvider/pom.properties" );
    while(theResources.hasMoreElements()){
      URL theURL = theResources.nextElement();
      readPOMProperties(theURL);
    }
  }

  private void readPOMProperties( URL anUrl ) throws IOException {
    POM thePOM = new POM();
    BufferedReader theReader = null;
    try{
      theReader = new BufferedReader(new InputStreamReader(anUrl.openStream()));
      thePOM.addLine( theReader.readLine() );
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
