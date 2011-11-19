/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.util.concurrent.ExecutorService;

import chabernac.thread.DynamicSizeExecutor;


public abstract class Protocol implements IProtocol{
  protected IProtocol myParentProtocol = null;
  
  protected String myId;
  private ServerInfo myServerInfo;
  

  public Protocol(String anId){
    if(anId.length() != 3) throw new IllegalArgumentException("Protocol identifier must be 3 bytes long");
    myId = anId;
  }
  
  public String getId(){
    return myId;
  }
  
  public int getImportance(){
    return 100;
  }
  
  public abstract String getDescription();
  
   /**
   * prefix the message with the id of the protocol
   * 
   * @param aMessage
   * @return
   */
  public String createMessage(String aMessage){
    return myId + aMessage;
  }
  
  @Override
  public void setMasterProtocol( IProtocol aProtocol ) {
    myParentProtocol = aProtocol;
  }
  
  public IProtocol getMasterProtocol(){
    return myParentProtocol;
  }
  
  public ProtocolContainer findProtocolContainer(){
    if(myParentProtocol instanceof ProtocolContainer){
      return (ProtocolContainer)myParentProtocol;
    }
    if(myParentProtocol instanceof Protocol){
      return ((Protocol)myParentProtocol).findProtocolContainer();
    }
    return null;
  }
  
  public ExecutorService getExecutorService(){
    ProtocolContainer theContainer = findProtocolContainer();
    if(theContainer == null) return new DynamicSizeExecutor(5, 256,0);
    return theContainer.getExecutorService();
  }
  
  public ServerInfo getServerInfo() {
    return myServerInfo;
  }

  public void setServerInfo( ServerInfo anServerInfo ) throws ProtocolException {
    myServerInfo = anServerInfo;
  }

}
