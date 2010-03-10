/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

public class StringTools {
  public static boolean isNumeric(String aString){
    byte[] theBytes = aString.getBytes();
    for(int i=0;i<theBytes.length;i++){
      if(theBytes[i] < '0' || theBytes[i] > '9') return false;
    }
    return true;
  }
}
