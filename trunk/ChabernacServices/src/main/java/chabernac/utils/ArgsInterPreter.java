/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

public class ArgsInterPreter {
  private final String[] myArgs;
  
  public ArgsInterPreter(String[] anArgs){
    myArgs = anArgs;
  }
  
  public boolean containsKey(String aKey){
    for(String theArg : myArgs){
      String[] theParts = theArg.split( "=" );
      if(theParts[0].equalsIgnoreCase( aKey )){
        return true;
      }
    }
    return false;
  }
  
  public String getKeyValue(String aKey){
    for(String theArg : myArgs){
      String[] theParts = theArg.split( "=" );
      if(theParts[0].equalsIgnoreCase( aKey ) && theParts.length >= 2){
        return theParts[1];
      }
    }
    return null;
  }

}
