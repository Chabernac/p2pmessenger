/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.testingutils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DumpAnalysis {
  private static Map<String, Integer> myFoundLines = new HashMap< String, Integer>();
  
  public static void main(String[] args) throws IOException{
    BufferedReader theReader = null;
    try{
      int theLineNumber = 0;
      theReader = new BufferedReader( new InputStreamReader( new FileInputStream( args[0] )));
      String theLine = null;
      while((theLine = theReader.readLine()) != null){
        theLineNumber++;
        if(theLine.toLowerCase().contains( args[1].toLowerCase() )){
          myFoundLines.put(theLine, theLineNumber);
        }
      }
      
      for(String theKey : myFoundLines.keySet()){
        System.out.println(theKey + "[" + myFoundLines.get(theKey) + "]");
      }
    } finally {
      if(theReader != null){
        theReader.close();
      }
    }
  }
}
