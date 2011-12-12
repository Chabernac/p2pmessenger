/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.util.Date;

public class ConvertTimestamp {
  public static void main(String args[]){
    Date theData = new Date();
    theData.setTime( 1318844696911L );
    System.out.println(theData);
    theData.setTime( 1319076296911L );
    System.out.println(theData);
    theData.setTime( 1318844696911L );
    System.out.println(theData);
    
  }
}

