/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.reactorplugin;

public abstract class AbstractStatement {

  public abstract String getStatement();
  public abstract boolean isTrue();
  
  public String toString(){
    return getStatement() + " [" + isTrue() + "]";
  }
}
