/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.pominfoexchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POM {
  private final List<String> myHeaders = new ArrayList< String >();
  private final Map<String, String> myProperties = new HashMap< String, String >();
  
  public void addLine(String aLine){
    if(aLine.startsWith( "#" )) myHeaders.add( aLine );
    else{
       String[] theParts = aLine.split( "=" );
       myProperties.put(theParts[0], theParts[1]);
    }
  }
  
  public String toString(){
    StringBuilder theBuilder = new StringBuilder();
    for(String theLine : myHeaders){
      theBuilder.append(theLine);
      theBuilder.append("\r\n");
    }
    for(String theKey : myProperties.keySet()){
      theBuilder.append(theKey);
      theBuilder.append("=");
      theBuilder.append(myProperties.get(theKey));
      theBuilder.append("\r\n");
    }
    return theBuilder.toString();
  }
}
