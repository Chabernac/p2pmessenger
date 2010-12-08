/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.test;

public class TestDevide {
  public static void main(String args[]){
    for(int i=0;i<1000;i++){
      System.out.println( i + ":" + (i >> 6 << 6 == i));
    }
  }
}
