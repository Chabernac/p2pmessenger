/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.pominfoexchange;

import java.io.IOException;

import junit.framework.TestCase;

public class POMInfoTest extends TestCase {
  public void testPOMInfo() throws IOException{
    POMInfo thePOMInfo = new POMInfo();
    System.out.println(thePOMInfo.toString());
    
  }
}
