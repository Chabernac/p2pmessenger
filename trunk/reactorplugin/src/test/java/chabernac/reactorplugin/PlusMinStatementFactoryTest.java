/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.reactorplugin;

import junit.framework.TestCase;

public class PlusMinStatementFactoryTest extends TestCase {
  public void testPlusMinStatementFactory(){
    iStatementFactory theFactory = new PlusMinStatementFactory();

    for(int i=0;i<20;i++){
      AbstractStatement theStatement = theFactory.createStatement();
      System.out.println(theStatement.toString());
    }
  }
}
