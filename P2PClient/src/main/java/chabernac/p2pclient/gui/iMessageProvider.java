/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

public interface iMessageProvider {
  public String getMessage();
  public void setMessage(String aMessage);
  public boolean isSendClosed();
  public void setSendClosed(boolean isSendClosed);
  public void clear();
  public void setMessageTitle(String aMessage);
  public String getMessageTitle();
}
