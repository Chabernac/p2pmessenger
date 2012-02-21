/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

public class MultiplyHandler implements iInputOutputHandler {

  private final int myFactor;

  public MultiplyHandler(int aFactor) {
    super();
    myFactor = aFactor;
  }

  @Override
  public String handle(String anInput) {
    return Integer.toString(Integer.parseInt(anInput) * myFactor);
  }

  @Override
  public void close() {
    
  }

}
