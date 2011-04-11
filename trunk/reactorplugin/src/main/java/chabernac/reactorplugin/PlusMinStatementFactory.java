/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.reactorplugin;

import java.util.Random;

public class PlusMinStatementFactory implements iStatementFactory {
  private final Random myRandom = new Random();
  private final int myLimit = 20;
  private final int myTrueFactor = 3;
  private final String[] myOperations = new String[]{"+","-"};

  @Override
  public AbstractStatement createStatement() {
    int theVar1 = Math.abs(myRandom.nextInt() % myLimit); 
    int theVar2 = Math.abs(myRandom.nextInt() % myLimit);
    
    boolean isTrue = (myRandom.nextInt() % myTrueFactor == 0);
    
    String theOperation = myOperations[Math.abs( myRandom.nextInt()) % myOperations.length];
    
    int theResult = getResult( theVar1, theVar2,theOperation, isTrue);
    
    return new PlusMinStatement( theOperation, theVar1, theVar2, theResult); 
  }
  
  private int getResult(int aVar1, int aVar2, String anOperation, boolean isTrue){
    if("+".equals( anOperation)){
      int theTrueResult = aVar1 + aVar2;
      if(isTrue) {
        return theTrueResult;
      } else {
        int theResult = theTrueResult;
        while( theResult == theTrueResult){
          theResult = Math.abs(myRandom.nextInt() % (2 * myLimit));
        }
        return theResult;
      }
    } else if("-".equals( anOperation)){
      int theTrueResult = aVar1 - aVar2;
      if(isTrue) {
        return theTrueResult;
      } else {
        int theResult = theTrueResult;
        while( theResult == theTrueResult){
          theResult = myRandom.nextInt() % myLimit;
        }
        return theResult;
      }
    }
    
    return 0;
    
  }

}
