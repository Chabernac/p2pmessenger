/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

public interface iMessageProvider {
  public String getMessage();
  public void setMessage(String aMessage);
  public void clear();
}
