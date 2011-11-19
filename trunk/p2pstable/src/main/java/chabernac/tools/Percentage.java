/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

public class Percentage {
  private final int myDivisor;
  private final int myDenominator;
  
  public Percentage( int aDenominator, int aDivisor ) {
    super();
    myDivisor = aDivisor;
    myDenominator = aDenominator;
  }
  
  public int getDivisor() {
    return myDivisor;
  }
  
  public int getDenominator() {
    return myDenominator;
  }
  
  public double getPercentage(){
    if(myDivisor == 0) return 0;
    return (double)myDenominator / (double)myDivisor;
  }
  
  public String toString(){
    return myDenominator + " / " + myDivisor;
  }
  
}
