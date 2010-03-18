/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

public interface IProtocol {
  public abstract String handleCommand(long aSessionId, String anInput);
  public String getId();
  public void setMasterProtocol(IProtocol aProtocol);
  public void stop();
}
