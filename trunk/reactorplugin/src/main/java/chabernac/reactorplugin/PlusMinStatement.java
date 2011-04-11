/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.reactorplugin;

public class PlusMinStatement extends AbstractStatement {
  public final String myOperation; 
  private final int myVar1;
  private final int myVar2;
  private final int myResult;
  
  public PlusMinStatement( String aOperation, int aVar1, int aVar2, int aResult ) {
    super();
    myOperation = aOperation;
    myVar1 = aVar1;
    myVar2 = aVar2;
    myResult = aResult;
  }

  @Override
  public String getStatement() {
    return myVar1 + " " + myOperation + " " + myVar2 + " = " + myResult;
  }
  
  public String getOperation() {
    return myOperation;
  }

  public int getVar1() {
    return myVar1;
  }

  public int getVar2() {
    return myVar2;
  }

  public int getResult() {
    return myResult;
  }

  @Override
  public boolean isTrue() {
    if("+".equals(myOperation)) return myVar1 + myVar2 == myResult;
    else if("-".equals(myOperation)) return myVar1 - myVar2 == myResult;
    return false;
  }

}
