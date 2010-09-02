/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class StringTools {
  private static Set< String > UUID = new LinkedHashSet< String >();
  
  public static boolean isNumeric(String aString){
    byte[] theBytes = aString.getBytes();
    for(int i=0;i<theBytes.length;i++){
      if(theBytes[i] < '0' || theBytes[i] > '9') return false;
    }
    return true;
  }
  
  public static String convertToLocalUniqueId(String anID){
    UUID.add( anID );
    return Integer.toString( new ArrayList< String >(UUID).indexOf( anID ));
  }
}
