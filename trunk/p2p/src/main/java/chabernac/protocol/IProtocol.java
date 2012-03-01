/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import chabernac.tools.PropertyMap;

public interface IProtocol {
  public abstract String handleCommand(String aSession, PropertyMap aProperties, String anInput);
  public String getId();
  public void setMasterProtocol(IProtocol aProtocol);
  public void stop();
  public void setServerInfo(ServerInfo aServerInfo) throws ProtocolException;
  public int getImportance();
}
