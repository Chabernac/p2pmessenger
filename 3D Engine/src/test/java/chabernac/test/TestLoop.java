/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.test;

import junit.framework.TestCase;

public class TestLoop extends TestCase{
  public void testLoop(){
    for(int i=0;i<1000;i++){
      System.out.println(i >> 2 << 2 == i);
    }
  }
}
